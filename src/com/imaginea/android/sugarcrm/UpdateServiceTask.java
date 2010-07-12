package com.imaginea.android.sugarcrm;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.Util;

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

    private Map<String, String> mUpdateNameValueMap;

    private String mBeanId;

    private Uri mUri;

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
        mBeanId = extras.getString(RestUtilConstants.ID);
        mUpdateNameValueMap = (Map<String, String>) extras.getSerializable(RestUtilConstants.NAME_VALUE_LIST);
        mCommand = extras.getInt(Util.COMMAND);
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
            if (Util.isNetworkOn(mContext)) {
                updatedBeanId = RestUtil.setEntry(url, mSessionId, mModuleName, mUpdateNameValueMap);

                if (mBeanId.equals(updatedBeanId)) {
                    Log.v(LOG_TAG, "updated server successful");

                    serverUpdated = true;

                } else{
                    serverUpdated = false;
                }
                //TODO: If the update fails when the network is ON, display the message
            }

            if (mCommand == R.id.update) {
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
