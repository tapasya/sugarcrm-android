package com.imaginea.android.sugarcrm;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.util.RelationshipStatus;
import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.Util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * UpdateServiceTask
 * 
 * @author chander
 * 
 */
public class UpdateServiceTask extends AsyncServiceTask<Object, Void, Object> {

    private Context mContext;

    // TODO - remove this
    private String mSessionId;

    private String mModuleName;
    
    private String mParentModuleName;

    private Map<String, String> mUpdateNameValueMap;

    private String mBeanId;

    private Uri mUri;
    
    private String mLinkFieldName;

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

        Bundle extras = intent.getExtras();
        mUri = intent.getData();
        mModuleName = extras.getString(RestUtilConstants.MODULE_NAME);
        mParentModuleName = extras.getString(RestUtilConstants.PARENT_MODULE_NAME);
        mLinkFieldName = extras.getString(RestUtilConstants.LINK_FIELD_NAME);
        mBeanId = extras.getString(RestUtilConstants.BEAN_ID);
        mUpdateNameValueMap = (Map<String, String>) extras.getSerializable(RestUtilConstants.NAME_VALUE_LIST);
        mCommand = extras.getInt(Util.COMMAND);
        
        /*Log.i(LOG_TAG, "mUri - " + mUri);
        Log.i(LOG_TAG, "mParentModuleName - " + mParentModuleName);
        Log.i(LOG_TAG, "mModuleName - " + mModuleName);
        Log.i(LOG_TAG, "mBeanId - " + mBeanId);*/
    }

    @Override
    protected Object doInBackground(Object... params) {
        try {

            if (mSessionId == null) {
                mSessionId = ((SugarCrmApp) SugarCrmApp.app).getSessionId();
            }

            String url = SugarCrmSettings.getSugarRestUrl(mContext);
            // Check network is on
            String updatedBeanId = null;
            ContentValues values = new ContentValues();
            int updatedRows = 0;
            boolean serverUpdated = false;
            //if (Util.isNetworkOn(mContext)) {
                Log.i(LOG_TAG, "linkFieldName : " + mLinkFieldName);
                if(mLinkFieldName != null){
                    updatedBeanId = RestUtil.setEntry(url, mSessionId, mModuleName, mUpdateNameValueMap);
                    Map<String, String> nameValueList = new LinkedHashMap<String, String>();
                    nameValueList.put(ModuleFields.ID, mBeanId);
                    
                    RelationshipStatus status = RestUtil.setRelationship(url, mSessionId, mParentModuleName, mBeanId, mLinkFieldName, new String[]{updatedBeanId}, nameValueList, 0);
                    Log.i(LOG_TAG, "created: "+ status.getCreatedCount() + " failed: " + status.getFailedCount() + " deleted: " + status.getDeletedCount());
                    if(status.getCreatedCount() >= 1){
                        Log.i(LOG_TAG, "Relationship is also set!");
                    } else{
                        Log.i(LOG_TAG, "setRelationship failed!");
                    }
                } else{
                    updatedBeanId = RestUtil.setEntry(url, mSessionId, mModuleName, mUpdateNameValueMap);
                    if (mBeanId.equals(updatedBeanId)) {
                        Log.v(LOG_TAG, "updated server successful");
                        serverUpdated = true;
                    } else{
                        serverUpdated = false;
                    }
                    //TODO: If the update fails when the network is ON, display the message
                }
            //}

            if(mCommand == R.id.insert){
                for (String key : mUpdateNameValueMap.keySet()) {
                    values.put(key, mUpdateNameValueMap.get(key));
                }
                //TODO: add mBeanId to the values map
                String[] fields = DatabaseHelper.getModuleProjections(mModuleName);
                values.put(fields[fields.length-1], mBeanId);
                
                //TODO: update in the DB
                if(mModuleName.equals("Contacts"))
                    mUri = Uri.withAppendedPath(mUri,"contact");
                
                Uri insertResultUri = mContext.getContentResolver().insert(mUri, values);
            } else if (mCommand == R.id.update) {
                for (String key : mUpdateNameValueMap.keySet()) {
                    values.put(key, mUpdateNameValueMap.get(key));
                }
                updatedRows = mContext.getContentResolver().update(mUri, values, null, null);
            }
            else if (mCommand == R.id.delete) {
                updatedRows = mContext.getContentResolver().delete(mUri, null, null);
            }
            // pass the success/failure msg to activity
            if (updatedRows > 0) {
                Log.v(LOG_TAG, "update successful");
            } else {
                Log.v(LOG_TAG, "update failed");
            }

            if (!serverUpdated) {
                // update the sync table
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
