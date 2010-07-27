package com.imaginea.android.sugarcrm;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent;
import com.imaginea.android.sugarcrm.sync.SyncRecord;
import com.imaginea.android.sugarcrm.util.RelationshipStatus;
import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.SugarCrmException;
import com.imaginea.android.sugarcrm.util.Util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * UpdateServiceTask
 * 
 * @author chander
 * @author vasavi
 * 
 */
public class UpdateServiceTask extends AsyncServiceTask<Object, Void, Object> {

    private Context mContext;

    private String mModuleName;

    private String mParentModuleName;

    private Map<String, String> mUpdateNameValueMap;

    private String mBeanId;

    private Uri mUri;

    private String mLinkFieldName;

    private DatabaseHelper mDbHelper;

    /*
     * represents either delete or update, for local database operations, is always an update on the
     * remote server side
     */
    private int mCommand;

    public static final String LOG_TAG = "UpdateServiceTask";

    @SuppressWarnings("unchecked")
    public UpdateServiceTask(Context context, Intent intent) {
        super(context);
        mContext = context;
        mDbHelper = new DatabaseHelper(context);
        Bundle extras = intent.getExtras();
        mUri = intent.getData();
        mModuleName = extras.getString(RestUtilConstants.MODULE_NAME);
        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            Log.d(LOG_TAG, "size : " + mUri.getPathSegments().size());
        }

        if (mUri.getPathSegments().size() == 3) {

            mParentModuleName = mUri.getPathSegments().get(0);
            mLinkFieldName = mDbHelper.getLinkfieldName(mUri.getPathSegments().get(2));
            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                Log.d(LOG_TAG, "mParentModuleName : " + mParentModuleName + " linkFieldName : "
                                                + mLinkFieldName);
            }
        }
        mBeanId = extras.getString(RestUtilConstants.BEAN_ID);
        mUpdateNameValueMap = (Map<String, String>) extras.getSerializable(RestUtilConstants.NAME_VALUE_LIST);
        mCommand = extras.getInt(Util.COMMAND);

        /*
         * Log.d(LOG_TAG, "mUri - " + mUri); Log.d(LOG_TAG, "mParentModuleName - " +
         * mParentModuleName); Log.d(LOG_TAG, "mModuleName - " + mModuleName); Log.d(LOG_TAG,
         * "mBeanId - " + mBeanId);
         */
    }

    @Override
    protected Object doInBackground(Object... params) {
        try {

            String sessionId = ((SugarCrmApp) SugarCrmApp.app).getSessionId();

            String url = SugarCrmSettings.getSugarRestUrl(mContext);
            // Check network is on
            String updatedBeanId = null;
            ContentValues values = new ContentValues();
            int updatedRows = 0;
            boolean serverUpdated = false;
            if (Util.isNetworkOn(mContext)) {
                if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                    Log.i(LOG_TAG, "linkFieldName : " + mLinkFieldName);
                }
                switch (mCommand) {
                case Util.INSERT:
                    // inserts with a relationship
                    if (mLinkFieldName != null) {
                        updatedBeanId = RestUtil.setEntry(url, sessionId, mModuleName, mUpdateNameValueMap);
                        RelationshipStatus status = RestUtil.setRelationship(url, sessionId, mParentModuleName, mBeanId, mLinkFieldName, new String[] { updatedBeanId }, new LinkedHashMap<String, String>(), Util.EXCLUDE_DELETED_ITEMS);
                        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                            Log.i(LOG_TAG, "created: " + status.getCreatedCount() + " failed: "
                                                            + status.getFailedCount()
                                                            + " deleted: "
                                                            + status.getDeletedCount());
                        }

                        if (status.getCreatedCount() >= 1) {
                            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                                Log.i(LOG_TAG, "Relationship is also set!");
                            }
                        } else {
                            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                                Log.i(LOG_TAG, "setRelationship failed!");
                            }
                        }
                    } else {
                        // insert case for an orphan module add without any relationship, the
                        // updatedBeanId is actually a new beanId returned by server
                        updatedBeanId = RestUtil.setEntry(url, sessionId, mModuleName, mUpdateNameValueMap);
                        if (updatedBeanId != null) {
                            if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
                                Log.v(LOG_TAG, "insert bean on server successful");
                            }
                            serverUpdated = true;
                        } else {
                            serverUpdated = false;
                        }
                        // TODO:
                        // If the update fails when the network is ON, display the message in the
                        // activity
                    }
                    break;

                // make the same calls for update and delete as delete only changes the DELETED flag
                // to 1
                case Util.UPDATE:
                case Util.DELETE:
                    mUpdateNameValueMap.put(SugarCRMContent.SUGAR_BEAN_ID, mBeanId);
                    updatedBeanId = RestUtil.setEntry(url, sessionId, mModuleName, mUpdateNameValueMap);
                    if (mBeanId.equals(updatedBeanId)) {
                        if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
                            Log.v(LOG_TAG, "updated server successful");
                        }
                        serverUpdated = true;
                    } else {
                        serverUpdated = false;
                    }
                    // TODO:
                    // If the update fails when the network is ON, display the message in the
                    // activity
                    break;
                }

            }

            switch (mCommand) {
            case Util.INSERT:
                for (String key : mUpdateNameValueMap.keySet()) {
                    values.put(key, mUpdateNameValueMap.get(key));
                }
                if (serverUpdated) {
                    // add updatedBeanId to the values map
                    values.put(SugarCRMContent.SUGAR_BEAN_ID, updatedBeanId);
                    Uri insertResultUri = mContext.getContentResolver().insert(mUri, values);
                    Log.i(LOG_TAG, "insertResultURi - " + insertResultUri);
                } else {
                    /*
                     * we do not have a beanId to add to our valueMap. we add a randomly generated
                     * beanId -with prefix "Sync" only for debugging purposes, do not use to
                     * distinguish with sync and normal operations
                     */
                    values.put(SugarCRMContent.SUGAR_BEAN_ID, "Sync" + UUID.randomUUID());
                    Uri insertResultUri = mContext.getContentResolver().insert(mUri, values);
                    Log.i(LOG_TAG, "insertResultURi - " + insertResultUri);
                    insertSyncRecord(insertResultUri);
                }

                break;

            case Util.UPDATE:
                for (String key : mUpdateNameValueMap.keySet()) {
                    values.put(key, mUpdateNameValueMap.get(key));
                }
                updatedRows = mContext.getContentResolver().update(mUri, values, null, null);
                if (!serverUpdated && updatedRows > 0) {
                    updateSyncRecord();
                }
                sendUpdateStatus(updatedRows);
                break;

            case Util.DELETE:
                if (serverUpdated) {
                    updatedRows = mContext.getContentResolver().delete(mUri, null, null);
                } else {
                    // this will update just the delete column, sets it to 1
                    values.put(ModuleFields.DELETED, Util.DELETED_ITEM);
                    // for (String key : mUpdateNameValueMap.keySet()) {
                    // values.put(key, mUpdateNameValueMap.get(key));
                    // }
                    updatedRows = mContext.getContentResolver().update(mUri, values, null, null);
                    if (updatedRows > 0)
                        updateSyncRecord();
                }
                sendUpdateStatus(updatedRows);

                break;

            }

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        mDbHelper.close();
        return null;
    }

    private void sendUpdateStatus(int updatedRows) {

        // pass the success/failure msg to activity
        if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
            if (updatedRows > 0) {
                Log.v(LOG_TAG, "update successful");
            } else {
                Log.v(LOG_TAG, "update failed");
            }
        }
    }

    private void updateSyncRecord() throws SugarCrmException {
        long syncId = Long.parseLong(mUri.getPathSegments().get(1));
        SyncRecord rec = mDbHelper.getSyncRecord(syncId, mModuleName);
        debug(rec);
        if (rec == null)
            insertSyncRecord(mUri);
        else {           
            mDbHelper.updateSyncRecord(rec);            
        }
    }

    private void insertSyncRecord(Uri insertUri) throws SugarCrmException {
        SyncRecord record = new SyncRecord();
        record.syncId = Long.parseLong(insertUri.getPathSegments().get(1));
        record.syncCommand = mCommand;
        record.moduleName = mLinkFieldName != null ? mParentModuleName : mModuleName;
        record.relatedModuleName = mModuleName;
        if (Log.isLoggable(LOG_TAG, Log.DEBUG))
            debug(record);
        mDbHelper.insertSyncRecord(record);
    }

    private void debug(SyncRecord record) {
        if (record == null) {
            Log.d(LOG_TAG, "Sync Record is null");
            return;
        }
        Log.d(LOG_TAG, " id:" + record._id);
        Log.d(LOG_TAG, "Sync id:" + record.syncId);
        Log.d(LOG_TAG, "Sync command:" + record.syncCommand);
        Log.d(LOG_TAG, "Module name:" + record.moduleName);
        Log.d(LOG_TAG, "Related Module Name:" + record.relatedModuleName);
        Log.d(LOG_TAG, "Sync command:" + (record.syncCommand == 1 ? "INSERT" : "UPDATE/DELETE"));
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
