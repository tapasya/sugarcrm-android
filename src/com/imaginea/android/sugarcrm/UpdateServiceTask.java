package com.imaginea.android.sugarcrm;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

    private static String mSelection = SugarCRMContent.RECORD_ID + "=?";

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
        // current module being updated/inserted/deleted
        mModuleName = extras.getString(RestUtilConstants.MODULE_NAME);

        if (mUri.getPathSegments().size() == 3) {
            mParentModuleName = mUri.getPathSegments().get(0);
            String relatedModuleName = mUri.getPathSegments().get(2);
            mLinkFieldName = mDbHelper.getLinkfieldName(relatedModuleName);
        }
        mBeanId = extras.getString(RestUtilConstants.BEAN_ID);
        mUpdateNameValueMap = (Map<String, String>) extras.getSerializable(RestUtilConstants.NAME_VALUE_LIST);
        mCommand = extras.getInt(Util.COMMAND);
        debug();
    }

    void debug() {
        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            Log.d(LOG_TAG, "size : " + mUri.getPathSegments().size());
            Log.d(LOG_TAG, "mParentModuleName : " + mParentModuleName + " linkFieldName : "
                                            + mLinkFieldName);
        }
        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            Log.i(LOG_TAG, "linkFieldName : " + mLinkFieldName);
        }
    }

    @Override
    protected Object doInBackground(Object... params) {
        int updatedRows = 0;
        boolean serverUpdated = false;
        // get network status
        boolean netOn = Util.isNetworkOn(mContext);
        try {

            String sessionId = ((SugarCrmApp) SugarCrmApp.app).getSessionId();

            String url = SugarCrmSettings.getSugarRestUrl(mContext);

            ContentValues values = new ContentValues();
            String updatedBeanId = null;
            // Check network is on
            if (netOn) {

                switch (mCommand) {
                case Util.INSERT:
                    // inserts with a relationship
                    if (mLinkFieldName != null) {

                        updatedBeanId = RestUtil.setEntry(url, sessionId, mModuleName, mUpdateNameValueMap);
                        if (updatedBeanId != null) {
                            // get the parent beanId
                            String rowId = mUri.getPathSegments().get(1);
                            mBeanId = lookupBeanId(mParentModuleName, rowId);
                            RelationshipStatus status = RestUtil.setRelationship(url, sessionId, mParentModuleName, mBeanId, mLinkFieldName, new String[] { updatedBeanId }, new LinkedHashMap<String, String>(), Util.EXCLUDE_DELETED_ITEMS);
                            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                                Log.i(LOG_TAG, "created: " + status.getCreatedCount() + " failed: "
                                                                + status.getFailedCount()
                                                                + "deleted: "
                                                                + status.getDeletedCount());
                            }

                            if (status.getCreatedCount() >= 1) {
                                if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                                    Log.i(LOG_TAG, "Relationship is also set!");
                                }
                                serverUpdated = true;
                            } else {
                                if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                                    Log.i(LOG_TAG, "setRelationship failed!");
                                }
                                serverUpdated = false;
                            }

                        } else {
                            serverUpdated = false;
                        }
                    } else {
                        // insert case for an orphan module add without any relationship, the
                        // updatedBeanId is actually a new beanId returned by server
                        updatedBeanId = RestUtil.setEntry(url, sessionId, mModuleName, mUpdateNameValueMap);
                        if (updatedBeanId != null) {
                            serverUpdated = true;
                        } else {
                            serverUpdated = false;
                        }
                    }
                    break;

                // make the same calls for update and delete as delete only changes the DELETED flag
                // to 1
                case Util.UPDATE:
                case Util.DELETE:

                    String serverUpdatedBeanId = null;
                    if (mLinkFieldName != null) {
                        String rowId = mUri.getPathSegments().get(1);
                        mBeanId = lookupBeanId(mParentModuleName, rowId);

                        // related BeanId
                        rowId = mUri.getPathSegments().get(3);
                        updatedBeanId = lookupBeanId(mModuleName, rowId);

                        serverUpdatedBeanId = RestUtil.setEntry(url, sessionId, mModuleName, mUpdateNameValueMap);
                        if (serverUpdatedBeanId.equals(updatedBeanId)) {
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
                                serverUpdated = true;
                            } else {
                                if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                                    Log.i(LOG_TAG, "setRelationship failed!");
                                }
                                serverUpdated = false;
                            }
                        } else {
                            // a new bean was created instead of sending back the same updated bean
                            serverUpdated = false;
                        }
                    } else {
                        String rowId = mUri.getPathSegments().get(1);
                        mBeanId = lookupBeanId(mModuleName, rowId);
                        mUpdateNameValueMap.put(SugarCRMContent.SUGAR_BEAN_ID, mBeanId);
                        updatedBeanId = RestUtil.setEntry(url, sessionId, mModuleName, mUpdateNameValueMap);
                        if (mBeanId.equals(updatedBeanId)) {

                            serverUpdated = true;
                        } else {
                            serverUpdated = false;
                        }
                    }
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
                break;

            }

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            sendUpdateStatus(netOn, serverUpdated, updatedRows);
        }
        mDbHelper.close();
        sendUpdateStatus(netOn, serverUpdated, updatedRows);
        return null;
    }

    private void sendUpdateStatus(boolean netOn, boolean serverUpdated, int updatedRows) {

        // If the update fails when the network is ON, display the message in the
        // activity
        if (netOn) {

            if (!serverUpdated) {
                SugarService.sendMessage(R.id.status, String.format(mContext.getString(R.string.serverUpdateFailed), getCommandStr()));
                if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
                    Log.v(LOG_TAG, "update to server failed");
                }
            } else {
                if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
                    Log.v(LOG_TAG, "updated server successful");
                }
                SugarService.sendMessage(R.id.status, String.format(mContext.getString(R.string.serverUpdateSuccess), getCommandStr()));
            }
        } else {
            // pass the success/failure msg to activity
            if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
                if (updatedRows > 0) {
                    Log.v(LOG_TAG, "update successful");
                    SugarService.sendMessage(R.id.status, String.format(mContext.getString(R.string.serverUpdateSuccess), getCommandStr()));
                } else {
                    SugarService.sendMessage(R.id.status, String.format(mContext.getString(R.string.updateFailed), getCommandStr()));
                    Log.v(LOG_TAG, "update failed");
                }
            }
        }
    }

    private String getCommandStr() {
        switch (mCommand) {
        case Util.INSERT:
            return mContext.getString(R.string.insert);
        case Util.UPDATE:
            return mContext.getString(R.string.update);
        case Util.DELETE:
            return mContext.getString(R.string.delete);
        default:
            return "";
        }
    }

    private void updateSyncRecord() throws SugarCrmException {
        long syncId = Long.parseLong(mUri.getPathSegments().get(1));
        SyncRecord rec = mDbHelper.getSyncRecord(syncId, mModuleName);
        debug(rec);
        if (rec == null)
            insertSyncRecord(mUri);
        else {
            if (mUri.getPathSegments().size() == 3) {
                rec.syncRelatedId = Long.parseLong(mUri.getPathSegments().get(3));
            }
            mDbHelper.updateSyncRecord(rec);
        }
    }

    private void insertSyncRecord(Uri insertUri) throws SugarCrmException {
        SyncRecord record = new SyncRecord();
        record.syncId = Long.parseLong(insertUri.getPathSegments().get(1));
        if (mUri.getPathSegments().size() == 3) {
            record.syncRelatedId = Long.parseLong(insertUri.getPathSegments().get(3));
        }
        record.syncCommand = mCommand;
        record.moduleName = mLinkFieldName != null ? mParentModuleName : mModuleName;
        record.relatedModuleName = mModuleName;
        record.status = Util.UNSYNCED;
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
        Log.d(LOG_TAG, "Sync Status:" + (record.status == Util.UNSYNCED ? "UNSYNCHD" : "CONFLICTS"));
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    /**
     * Returns the beanId id , or null if the item is not found.
     * 
     */
    private String lookupBeanId(String moduleName, String rowId) {
        ContentResolver resolver = mContext.getContentResolver();
        String beanId = null;
        Uri contentUri = mDbHelper.getModuleUri(moduleName);
        String[] projection = new String[] { SugarCRMContent.SUGAR_BEAN_ID };

        final Cursor c = resolver.query(contentUri, projection, mSelection, new String[] { rowId }, null);
        try {
            if (c.moveToFirst()) {
                beanId = c.getString(0);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return beanId;
    }
}
