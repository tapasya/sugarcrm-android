package com.imaginea.android.sugarcrm;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.imaginea.android.sugarcrm.util.RestUtil;

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
    }

    @Override
    protected Object doInBackground(Object... params) {
        try {

            if (mSessionId == null) {
                mSessionId = ((SugarCrmApp) SugarCrmApp.app).getSessionId();
            }

            String url = SugarCrmSettings.getSugarRestUrl(mContext);

            // nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
            String updatedBeanId = RestUtil.setEntry(url, mSessionId, mModuleName, mUpdateNameValueMap);

            int updatedRows = 0;
            if (updatedBeanId.equals(mBeanId)) {
                Log.v(LOG_TAG, "updated server successful");
                ContentValues values = new ContentValues();
                for (String key : mUpdateNameValueMap.keySet()) {
                    values.put(key, mUpdateNameValueMap.get(key));
                }
                updatedRows = mContext.getContentResolver().update(mUri, values, null, null);

            }
            // pass the success/failure msg to activity
            if (updatedRows > 0) {
                Log.v(LOG_TAG, "update successful");
            } else {
                Log.v(LOG_TAG, "update failed");
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
