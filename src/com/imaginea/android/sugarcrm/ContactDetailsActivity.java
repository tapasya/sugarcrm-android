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

    private String mAccountName;

    private String mPhoneMobile;

    private String mPhoneWork;

    private String mEmail;

    private TextView mAccountButton;

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
        mNameTextView = (TextView) findViewById(R.id.contactName);
        mAccountButton = (TextView) findViewById(R.id.contactAccountButton);
        mPhoneMobileButton = (Button) findViewById(R.id.contactPhoneMobileButton);
        mPhoneWorkButton = (Button) findViewById(R.id.contactPhoneWorkButton);
        mEmailButton = (Button) findViewById(R.id.contactEmailButton);
        mAddButton = (Button) findViewById(R.id.contactAddButton);
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
                String url = pref.getString("URL", getString(R.string.defaultUrl));
                String userName = pref.getString("USER_NAME", getString(R.string.defaultUser));
                String password = pref.getString("PASSWORD", getString(R.string.defaultPwd));

                // SugarCrmApp app =
                // mSessionId = ((SugarCrmApp) getApplication()).getSessionId();
                if (mSessionId == null) {
                    mSessionId = RestUtil.loginToSugarCRM(url, userName, password);
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

            mAccountName = mSugarBean.getFieldValue(ModuleFields.ACCOUNT_NAME);
            if (mAccountName != null && !mAccountName.equals("")) {
                mAccountButton.setText(mAccountName);
            } else {
                mAccountButton.setText(R.string.notAvailable);
                mAccountButton.setClickable(false);
            }

            mPhoneMobile = mSugarBean.getFieldValue(ModuleFields.PHONE_MOBILE);
            if (mPhoneMobile != null && !mPhoneMobile.equals("")) {
                mPhoneMobileButton.setText(mPhoneMobile);
            } else {
                mPhoneMobileButton.setText(R.string.notAvailable);
                mPhoneMobileButton.setClickable(false);
            }

            mPhoneWork = mSugarBean.getFieldValue(ModuleFields.PHONE_WORK);
            if (mPhoneWork != null && !mPhoneWork.equals("")) {
                mPhoneWorkButton.setText(mPhoneWork);
            } else {
                mPhoneWorkButton.setText(R.string.notAvailable);
                mPhoneWorkButton.setClickable(false);
            }

            mEmail = mSugarBean.getFieldValue(ModuleFields.EMAIL1);
            if (mEmail != null && !mEmail.equals("")) {
                mEmailButton.setText(mEmail);
            } else {
                mEmailButton.setText(R.string.notAvailable);
                mEmailButton.setClickable(false);
            }
        }

    }

    private void setListeners() {

        if (mPhoneMobileButton.isClickable()) {
            mPhoneMobileButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    callNumber(mPhoneMobile);
                }
            });
        }

        if (mPhoneWorkButton.isClickable()) {
            mPhoneWorkButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    callNumber(mPhoneWork);
                }
            });
        }

        if (mAccountButton.isClickable()) {
            mAccountButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        if (mEmailButton.isClickable()) {
            mEmailButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMail(mEmail);
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
