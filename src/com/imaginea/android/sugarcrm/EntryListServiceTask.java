package com.imaginea.android.sugarcrm;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.SugarBean;
import com.imaginea.android.sugarcrm.util.Util;

import java.util.HashMap;
import java.util.List;

/**
 * EntryListServiceTask
 * 
 * @author chander
 * 
 */
public class EntryListServiceTask extends AsyncServiceTask<Object, Void, Object> {

    private Context mContext;

    // TODO - remove this
    private String mSessionId;

    private String mModuleName;

    private String[] mSelectFields;

    private HashMap<String, List<String>> mLinkNameToFieldsArray = new HashMap<String, List<String>>();

    private String mMaxResults = "0";

    private Uri mUri;

    private String mQuery = "", mOrderBy = "";

    private String mOffset = "0";

    private String mDeleted = "0";

    public static final String LOG_TAG = "EntryListTask";

    public EntryListServiceTask(Context context, Intent intent) {
        super(context);
        mContext = context;

        Bundle extras = intent.getExtras();
        mUri = intent.getData();
        int count = mUri.getPathSegments().size();
        if (count == 3) {
            mOffset = mUri.getPathSegments().get(1);
        }
        mModuleName = extras.getString(RestUtilConstants.MODULE_NAME);
        mSelectFields = extras.getStringArray(Util.PROJECTION);
        mOrderBy = extras.getString(Util.SORT_ORDER);
    }

    @Override
    protected Object doInBackground(Object... params) {
        try {

            if (mSessionId == null) {
                mSessionId = ((SugarCrmApp) SugarCrmApp.app).getSessionId();
            }
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
            // TODO use a constant and remove this as we start from the login screen
            String url = pref.getString(Util.PREF_REST_URL, mContext.getString(R.string.defaultUrl));

            SugarBean[] sBeans = RestUtil.getEntryList(url, mSessionId, mModuleName, mQuery, mOrderBy, mOffset, mSelectFields, mLinkNameToFieldsArray, mMaxResults, mDeleted);
            // mAdapter.setSugarBeanArray(sBeans);
            // We can stop loading once we do not get the
            // if (sBeans.length < mMaxResults)
            // mStopLoading = true;

            // TODO - do a bulk insert in the content provider instead
            for (SugarBean sBean : sBeans) {
                ContentValues values = new ContentValues();

                for (int i = 0; i < mSelectFields.length; i++) {
                    String fieldValue = sBean.getFieldValue(mSelectFields[i]);
                    Log.i(LOG_TAG, "FieldName:|Field value " + mSelectFields[i] + ":" + fieldValue);
                    values.put(mSelectFields[i], fieldValue);
                }
                mContext.getContentResolver().insert(mUri, values);
                // mContext.getContentResolver().update(mUri, values, null, null);
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
