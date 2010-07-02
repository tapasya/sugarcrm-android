package com.imaginea.android.sugarcrm;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.SugarBean;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * AccountDetailsActivity
 * 
 * @author vasavi
 */
public class AccountDetailsActivity extends Activity {

    private String mAccountSugarBeanId;

    private SugarBean mSugarBean;

    private String mSessionId;

    private String[] mSelectFields = { ModuleFields.NAME, ModuleFields.PARENT_NAME,
            ModuleFields.PHONE_OFFICE, ModuleFields.PHONE_FAX, ModuleFields.EMAIL1 };

    private String[] mLinkNameToFieldsArray = new String[] {};

    private final String LOG_TAG = "AccountDetailsActivity";

    private TableLayout mDetailsTable;

    private AccountDetailsTask mTask;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.account_details);

        mAccountSugarBeanId = (String) getIntent().getStringExtra(RestUtilConstants.ID);

        mTask = new AccountDetailsTask();
        mTask.execute(null);

    }

    /**
     * LoadAccountDetailsTask
     */
    class AccountDetailsTask extends AsyncTask<Object, Void, Object> {

        @Override
        protected Object doInBackground(Object... arg0) {
            try {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                // TODO use a constant and remove this as we start from the login screen
                String url = pref.getString("URL", getString(R.string.defaultUrl));
                String userName = pref.getString("USER_NAME", getString(R.string.defaultUser));
                String password = pref.getString("PASSWORD", getString(R.string.defaultPwd));

                // SugarCrmApp app =
                // mSessionId = ((SugarCrmApp) getApplication()).getSessionId();
                if (mSessionId == null) {
                    mSessionId = RestUtil.loginToSugarCRM(url, userName, password);
                }

                mSugarBean = RestUtil.getEntry(url, mSessionId, RestUtilConstants.ACCOUNTS_MODULE, mAccountSugarBeanId, mSelectFields, mLinkNameToFieldsArray);

            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                return Util.FETCH_FAILED;
            }

            return Util.FETCH_SUCCESS;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            super.onPostExecute(result);
            if (isCancelled())
                return;
            int retVal = (Integer) result;
            switch (retVal) {
            case Util.FETCH_FAILED:
                break;
            case Util.FETCH_SUCCESS:
                setContents();
                break;
            default:

            }
        }

        private void setContents() {

            mDetailsTable = (TableLayout) findViewById(R.id.accountDetalsTable);
            for (String fieldName : mSelectFields) {
                TextView textViewForLabel = new TextView(AccountDetailsActivity.this);
                textViewForLabel.setText(fieldName);
                TextView textViewForValue = new TextView(AccountDetailsActivity.this);

                String value = mSugarBean.getFieldValue(fieldName);
                if (value != null && !value.equals("")) {
                    textViewForValue.setText(value);
                } else {
                    textViewForValue.setText(R.string.notAvailable);
                }

                TableRow tableRow = new TableRow(AccountDetailsActivity.this);
                tableRow.addView(textViewForLabel);
                tableRow.addView(textViewForValue);

                mDetailsTable.addView(tableRow);
            }
        }

    }
}
