package com.imaginea.android.sugarcrm;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.SugarBean;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * ContactDetailsActivity
 * 
 * @author chander
 * 
 */
public class ContactDetailsActivity extends Activity {

    private String mContactSugarBeanId;

    private SugarBean mSugarBean;

    private String mSessionId;

    private String[] mSelectFields = { ModuleFields.FIRST_NAME, ModuleFields.LAST_NAME,
            ModuleFields.ACCOUNT_NAME, ModuleFields.PHONE_MOBILE, ModuleFields.PHONE_WORK,
            ModuleFields.EMAIL1 };

    private String[] mLinkNameToFieldsArray = new String[] {};

    private Button mAccountButton;

    private Button mPhoneMobileButton;

    private Button mPhoneWorkButton;

    private Button mEmailButton;

    private Button mAddButton;

    private TextView mNameTextView;

    private ContactDetailsTask mTask;

    private static final String LOG_TAG = ContactDetailsActivity.class.getSimpleName();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.contact_details);

        mContactSugarBeanId = (String) getIntent().getStringExtra(RestUtilConstants.ID);

        findViews();
        setListeners();
        mTask = new ContactDetailsTask();
        mTask.execute(null);

    }

    private void findViews() {
        mNameTextView = (TextView) findViewById(R.id.contact_name);
        mAccountButton = (Button) findViewById(R.id.contact_account_button);
        mPhoneMobileButton = (Button) findViewById(R.id.contact_phone_mobile_button);
        mPhoneWorkButton = (Button) findViewById(R.id.contact_phone_work_button);
        mEmailButton = (Button) findViewById(R.id.contact_email_button);
        mAddButton = (Button) findViewById(R.id.contact_add_button);
    }

    /**
     * LoadContactsTask
     */
    class ContactDetailsTask extends AsyncTask<Object, Void, Object> {

        @Override
        protected Object doInBackground(Object... params) {
            try {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                // TODO use a constant and remove this as we start from the login screen
                String url = pref.getString("URL", getString(R.string.default_url));
                String userName = pref.getString("USER_NAME", getString(R.string.default_username));
                String password = pref.getString("PASSWORD", getString(R.string.default_password));

                // SugarCrmApp app =
                // mSessionId = ((SugarCrmApp) getApplication()).getSessionId();
                if (mSessionId == null) {
                    mSessionId = RestUtil.loginToSugarCRM(url, userName, Util.MD5(password));
                }

                mSugarBean = RestUtil.getEntry(url, mSessionId, RestUtilConstants.CONTACTS_MODULE, mContactSugarBeanId, mSelectFields, mLinkNameToFieldsArray);

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
            mNameTextView.setText(mSugarBean.getFieldValue(ModuleFields.FIRST_NAME));

            String accountName = mSugarBean.getFieldValue(ModuleFields.ACCOUNT_NAME);
            if (accountName != null && !accountName.equals("")) {
                mAccountButton.setText(accountName);
            } else {
                mAccountButton.setText(R.string.not_available);
                mAccountButton.setClickable(false);
            }

            String mobile = mSugarBean.getFieldValue(ModuleFields.PHONE_MOBILE);
            if (mobile != null && !mobile.equals("")) {
                mPhoneMobileButton.setText(mobile);
            } else {
                mPhoneMobileButton.setText(R.string.not_available);
                mPhoneMobileButton.setClickable(false);
            }

            String workPhone = mSugarBean.getFieldValue(ModuleFields.PHONE_WORK);
            if (workPhone != null && !workPhone.equals("")) {
                mPhoneWorkButton.setText(workPhone);
            } else {
                mPhoneWorkButton.setText(R.string.not_available);
                mPhoneWorkButton.setClickable(false);
            }

            String email = mSugarBean.getFieldValue(ModuleFields.EMAIL1);
            if (email != null && !email.equals("")) {
                mEmailButton.setText(email);
            } else {
                mEmailButton.setText(R.string.not_available);
                mEmailButton.setClickable(false);
            }
        }

    }

    private void setListeners() {

        if (mPhoneMobileButton.isClickable()) {
            mPhoneMobileButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // callNumber(contact.getPhoneMobile());
                }
            });
        }

        if (mPhoneWorkButton.isClickable()) {
            mPhoneWorkButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // callNumber(contact.getPhoneWork());
                }
            });
        }

        if (mAccountButton.isClickable()) {
            mAccountButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // executeOnGuiThreadAuthenticatedTask(getItemDetailsTask);
                }
            });
        }

        if (mEmailButton.isClickable()) {
            mEmailButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // sendMail(contact.getEmail1());
                }
            });
        }

        mAddButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // addContact(contact);
            }
        });

    }

    public void callNumber(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        startActivity(intent);
    }

    public void sendMail(String emailAddress) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + emailAddress));
        startActivity(intent);
    }

}
