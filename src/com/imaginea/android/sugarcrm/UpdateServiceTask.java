package com.imaginea.android.sugarcrm;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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

    public static final String TAG = "UpdateServiceTask";

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
                    if (!TextUtils.isEmpty(mLinkFieldName)) {

                        updatedBeanId = RestUtil.setEntry(url, sessionId, mModuleName, mUpdateNameValueMap);
                        if (updatedBeanId != null) {
                            // get the parent beanId
                            String rowId = mUri.getPathSegments().get(1);
                            mBeanId = mDbHelper.lookupBeanId(mParentModuleName, rowId);
                            RelationshipStatus status = RestUtil.setRelationship(url, sessionId, mParentModuleName, mBeanId, mLinkFieldName, new String[] { updatedBeanId }, new LinkedHashMap<String, String>(), Util.EXCLUDE_DELETED_ITEMS);
                            if (Log.isLoggable(TAG, Log.DEBUG)) {
                                Log.i(TAG, "created: " + status.getCreatedCount() + " failed: "
                                                                + status.getFailedCount()
                                                                + "deleted: "
                                                                + status.getDeletedCount());
                            }

                            if (status.getCreatedCount() >= 1) {
                                if (Log.isLoggable(TAG, Log.DEBUG)) {
                                    Log.i(TAG, "Relationship is also set!");
                                }
                                serverUpdated = true;
                            } else {
                                if (Log.isLoggable(TAG, Log.DEBUG)) {
                                    Log.i(TAG, "setRelationship failed!");
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
                    // updates / deletes - relationship
                    if (mLinkFieldName != null) {
                        String rowId = mUri.getPathSegments().get(1);
                        mBeanId = mDbHelper.lookupBeanId(mParentModuleName, rowId);

                        // related BeanId
                        rowId = mUri.getPathSegments().get(3);
                        updatedBeanId = mDbHelper.lookupBeanId(mModuleName, rowId);

                        serverUpdatedBeanId = RestUtil.setEntry(url, sessionId, mModuleName, mUpdateNameValueMap);
                        Log.d(TAG, "updatedBeanId : " + updatedBeanId + "  serverUpdatedBeanId : " + serverUpdatedBeanId );
                        if (serverUpdatedBeanId.equals(updatedBeanId)) {
                            RelationshipStatus status = RestUtil.setRelationship(url, sessionId, mParentModuleName, mBeanId, mLinkFieldName, new String[] { updatedBeanId }, new LinkedHashMap<String, String>(), Util.EXCLUDE_DELETED_ITEMS);
                            if (Log.isLoggable(TAG, Log.DEBUG)) {
                                Log.d(TAG, "created: " + status.getCreatedCount() + " failed: "
                                                                + status.getFailedCount()
                                                                + " deleted: "
                                                                + status.getDeletedCount());
                            }

                            if (status.getCreatedCount() >= 1) {
                                if (Log.isLoggable(TAG, Log.DEBUG)) {
                                    Log.d(TAG, "Relationship is also set!");
                                }
                                serverUpdated = true;
                            } else {
                                if (Log.isLoggable(TAG, Log.DEBUG)) {
                                    Log.d(TAG, "setRelationship failed!");
                                }
                                serverUpdated = false;
                            }
                        } else {
                            // a new bean was created instead of sending back the same updated bean
                            serverUpdated = false;
                        }
                    } else {
                        Log.i(TAG, "update/delete orphan : uri - " + mUri);
                        String rowId = mUri.getPathSegments().get(1);
                        mBeanId = mDbHelper.lookupBeanId(mModuleName, rowId);
                        mUpdateNameValueMap.put(SugarCRMContent.SUGAR_BEAN_ID, mBeanId);
                        updatedBeanId = RestUtil.setEntry(url, sessionId, mModuleName, mUpdateNameValueMap);
                        mUpdateNameValueMap.remove(SugarCRMContent.SUGAR_BEAN_ID);
                        Log.d(TAG, "updatedBeanId : " + updatedBeanId + "  mBeanId : " + mBeanId );
                        
                        if (mBeanId.equals(updatedBeanId)) {
                            String accountName = (String)values.get(ModuleFields.ACCOUNT_NAME);
                            String accountBeanId = mDbHelper.lookupBeanId(Util.ACCOUNTS, rowId);
                            //RelationshipStatus status = RestUtil.setRelationship(url, sessionId, Util.ACCOUNTS, mBeanId, mLinkFieldName, new String[] { updatedBeanId }, new LinkedHashMap<String, String>(), Util.EXCLUDE_DELETED_ITEMS);
                            //TODO:
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
                //TODO:  relationship or orphan
                for (String key : mUpdateNameValueMap.keySet()) {
                    values.put(key, mUpdateNameValueMap.get(key));
                }
                if (serverUpdated) {
                    // add updatedBeanId to the values map
                    values.put(SugarCRMContent.SUGAR_BEAN_ID, updatedBeanId);
                    Uri insertResultUri = mContext.getContentResolver().insert(mUri, values);
                    Log.i(TAG, "insertResultURi - " + insertResultUri);
                    updatedRows = 1;
                } else {
                    /*
                     * we do not have a beanId to add to our valueMap. we add a randomly generated
                     * beanId -with prefix "Sync" only for debugging purposes, do not use to
                     * distinguish with sync and normal operations
                     */
                    values.put(SugarCRMContent.SUGAR_BEAN_ID, "Sync" + UUID.randomUUID());
                    Uri insertResultUri = mContext.getContentResolver().insert(mUri, values);
                    // after success ul insertion, we set the updatedRow to 1 so we dont get a fail
                    // msg
                    updatedRows = 1;
                    Log.i(TAG, "insertResultURi - " + insertResultUri);
                    insertSyncRecord(insertResultUri);
                }
                break;

            case Util.UPDATE:
                //TODO: relationship or orphan
                
                for (String key : mUpdateNameValueMap.keySet()) {
                    values.put(key, mUpdateNameValueMap.get(key));
                }
                updatedRows = mContext.getContentResolver().update(mUri, values, null, null);
                
                if (!serverUpdated && updatedRows > 0) {
                    updateSyncRecord();
                }
                break;

            case Util.DELETE:
                
                //TODO: relationship or orphan
                
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
            Log.e(TAG, e.getMessage(), e);
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
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "update to server failed");
                }
            } else {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "updated server successful");
                }
                SugarService.sendMessage(R.id.status, String.format(mContext.getString(R.string.serverUpdateSuccess), getCommandStr()));
            }
        } else {
            // pass the success/failure msg to activity

            if (updatedRows > 0) {
                Log.v(TAG, "update successful");
                SugarService.sendMessage(R.id.status, String.format(mContext.getString(R.string.serverUpdateSuccess), getCommandStr()));
            } else {
                SugarService.sendMessage(R.id.status, String.format(mContext.getString(R.string.updateFailed), getCommandStr()));
                Log.v(TAG, "update failed");
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
        if (Log.isLoggable(TAG, Log.DEBUG))
            debug(record);
        mDbHelper.insertSyncRecord(record);
    }

    private void debug(SyncRecord record) {
        if (record == null) {
            Log.d(TAG, "Sync Record is null");
            return;
        }
        Log.d(TAG, " id:" + record._id);
        Log.d(TAG, "Sync id:" + record.syncId);
        Log.d(TAG, "Sync command:" + record.syncCommand);
        Log.d(TAG, "Module name:" + record.moduleName);
        Log.d(TAG, "Related Module Name:" + record.relatedModuleName);
        Log.d(TAG, "Sync command:" + (record.syncCommand == 1 ? "INSERT" : "UPDATE/DELETE"));
        Log.d(TAG, "Sync Status:" + (record.status == Util.UNSYNCED ? "UNSYNCHD" : "CONFLICTS"));
    }

    private void debug() {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "size : " + mUri.getPathSegments().size());
            Log.d(TAG, "mParentModuleName : " + mParentModuleName + " linkFieldName : "
                                            + mLinkFieldName);
        }
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.i(TAG, "linkFieldName : " + mLinkFieldName);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
