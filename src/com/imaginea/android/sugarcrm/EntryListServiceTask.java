package com.imaginea.android.sugarcrm;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Contacts;
import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.SugarBean;

/**
 * EntryListServiceTask
 * 
 * @author chander
 * 
 */
public class EntryListServiceTask extends AsyncServiceTask<Object, Void, Object> {

    private Context mContext;

    private String mSessionId;

    private String[] fields = new String[] {};

    private String[] mSelectFields = Contacts.LIST_PROJECTION;

    private String[] mLinkNameToFieldsArray;

    private int mMaxResults;

    Uri mUri;

    // RestUtil.getModuleFields(url, mSessionId, moduleName, fields);
    private String query = "", orderBy = ModuleFields.FIRST_NAME;

    private int offset = 0;

    private int deleted = 0;

    public static final String LOG_TAG = "EntryListTask";

    public EntryListServiceTask(Context context, Intent intent) {
        super(context);
        mContext = context;

        mUri = intent.getData();

    }

    @Override
    protected Object doInBackground(Object... params) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
            // TODO use a constant and remove this as we start from the login screen
            String url = pref.getString("URL", mContext.getString(R.string.defaultUrl));
            String userName = pref.getString("USER_NAME", mContext.getString(R.string.defaultUser));
            String password = pref.getString("PASSWORD", mContext.getString(R.string.defaultPwd));
            Log.i(LOG_TAG, url + userName + password);
            // SugarCrmApp app =
            // mSessionId = ((SugarCrmApp) getApplication()).getSessionId();
            if (mSessionId == null) {
                mSessionId = RestUtil.loginToSugarCRM(url, userName, password);
            }

            SugarBean[] sBeans = RestUtil.getEntryList(url, mSessionId, RestUtilConstants.CONTACTS_MODULE, query, orderBy, offset, mSelectFields, mLinkNameToFieldsArray, mMaxResults, deleted);
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
