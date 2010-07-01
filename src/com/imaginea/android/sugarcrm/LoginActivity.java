package com.imaginea.android.sugarcrm;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.SugarCrmException;
import com.imaginea.android.sugarcrm.util.Util;
import com.imaginea.android.sugarcrm.util.ViewUtil;

public class LoginActivity extends Activity {

    public final static String LOG_TAG = "LoginActivity";

    private boolean isSessionIdAvailable = false;

    private TextView mMessage; // to display any message in the login screen

    // TODO : Remove the url from here
    private String mUrl = "http://192.168.1.83/sugarcrm/service/v2/rest.php";

    private EditText mUrlEdit;

    private String mUsername; // to get the username value entered in the EditText

    private EditText mUsernameEdit;

    private String mPassword; // to get the password value entered in the EditText

    private EditText mPasswordEdit;

    private SugarCrmApp app;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = ((SugarCrmApp) getApplicationContext());

        /*
         * setContentView(R.layout.login_activity); mMessage = (TextView)
         * findViewById(R.id.login_message_bottom); mUrlEdit = (EditText)
         * findViewById(R.id.wizard_url_edit); mUsernameEdit = (EditText)
         * findViewById(R.id.login_edit_username); mPasswordEdit = (EditText)
         * findViewById(R.id.login_edit_password);
         */

        startActivity(WizardActivity.class);
        /*
         * String str = SugarCrmSettings.getSugarRestUrl(LoginActivity.this); str =
         * SugarCrmSettings.getUsername(LoginActivity.this).toString(); str =
         * SugarCrmSettings.getPassword(LoginActivity.this).toString();
         */

        /*
         * if (str != null && !str.equals("")) { Log.i(LOG_TAG, "SugarRestUrl - " + str); } else {
         * startActivity(WizardActivity.class); }
         */

        /*
         * if (str != null) { // mUsernameEdit.setVisibility(View.GONE); mUsernameEdit.setText(str);
         * mUsernameEdit.setEnabled(false); }
         */

        /*
         * if (isSessionIdAvailable) { // If sessionId is available, show the Dashboard
         * showDashboard();
         * 
         * } else { // check sugarCrmSettings mUrl =
         * SugarCrmSettings.getSugarRestUrl(LoginActivity.this); mUsername =
         * SugarCrmSettings.getUsername(LoginActivity.this); mPassword =
         * SugarCrmSettings.getPassword(LoginActivity.this); try { login(mUrl, mUsername,
         * mPassword); // show the Dashboard if the login is succesful showDashboard(); } catch
         * (SugarCrmException sce) { // If the login fails showSugarCrmSetting(); } }
         */
    }

    /**
     * Handles onClick event on the Submit button. Sends username/password to the server for
     * authentication.
     * 
     * @param view
     *            The Submit button for which this method is invoked
     */
    public void handleLogin(View view) {

        mUsername = mUsernameEdit.getText().toString();
        mPassword = mPasswordEdit.getText().toString();
        if (TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mPassword)) {
            mMessage.setText(getMessage());
        } else {
            // TODO: check the progressBar
            Dialog dialog = showProgress("Authenticating");

            // Start authenticating...
            try {
                login(mUrl, mUsername, mPassword);

                dialog.dismiss();
                // show the Dashboard if the login is successful
                startActivity(DashboardActivity.class);

            } catch (SugarCrmException sce) {
                // If the login fails
                mMessage.setText(sce.getMessage());
                dialog.dismiss();
            }
        }
    }

    private void login(String url, String username, String password) throws SugarCrmException {

        String sessionId = RestUtil.loginToSugarCRM(url, username, Util.MD5(password));
        Log.i(LOG_TAG, "SessionId - " + sessionId);

        // save the sessionId in the application context after the succesful login
        app.setSessionId(sessionId);

    }

    private void startActivity(Class _class) {
        Intent myIntent = new Intent(LoginActivity.this, _class);
        LoginActivity.this.startActivity(myIntent);
    }

    /**
     * Returns the message to be displayed at the top of the login dialog box.
     */
    private CharSequence getMessage() {
        getString(R.string.appName);
        if (TextUtils.isEmpty(mUsername)) {
            // If the username is blank
            final CharSequence msg = getText(R.string.validFieldMsg) + " username";
            return msg;
        }
        if (TextUtils.isEmpty(mPassword)) {
            // If the password is blank
            return getText(R.string.validFieldMsg) + " password";
        }
        return null;
    }

    /**
     * Shows the progress UI for a lengthy operation.
     */
    protected Dialog showProgress(String message) {
        return ViewUtil.getProgressBar(LoginActivity.this, message);
    }

}
