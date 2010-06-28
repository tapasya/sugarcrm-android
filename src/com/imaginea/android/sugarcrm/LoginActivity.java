package com.imaginea.android.sugarcrm;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends Activity {

    public final static String LOG_TAG = "LoginActivity";

    private TextView mMessage; // to display any message in the login screen

    private String mUsername; // to get the username value entered in the EditText

    private EditText mUsernameEdit;

    private String mPassword; // to get the password value entered in the EditText

    private EditText mPasswordEdit;

    // TODO : Remove the url from here
    private final String url = "http://192.168.1.83/sugarcrm/service/v2/rest.php";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);

        mMessage = (TextView) findViewById(R.id.message);
        mUsernameEdit = (EditText) findViewById(R.id.username_edit);
        mPasswordEdit = (EditText) findViewById(R.id.password_edit);
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
            //TODO: check the progressBar
            showProgress();
            // Start authenticating...
            String sessionId;
            try {
                try {
                    sessionId = RestUtil.loginToSugarCRM(url, mUsername, Util.MD5(mPassword));
                    Log.i(LOG_TAG, "SessionId - " + sessionId);
                    mMessage.setText("");

                    // save the sessionId in the application context after the succesful login
                    SugarCrmApp app = ((SugarCrmApp) getApplicationContext());
                    app.setSessionId(sessionId);
                    hideProgress();

                    // show the Dashboard if the login is succesful
                    Intent myIntent = new Intent(LoginActivity.this, DashboardActivity.class);
                    LoginActivity.this.startActivity(myIntent);

                } catch (SugarCrmException e) {
                    // If the login fails
                    mMessage.setText(e.getMessage());
                    hideProgress();
                }

            } catch (ClientProtocolException e) {
                Log.e(LOG_TAG, "Exception: ", e);
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Exception: ", e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Exception: ", e);
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                Log.e(LOG_TAG, "Exception: ", e);
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the message to be displayed at the top of the login dialog box.
     */
    private CharSequence getMessage() {
        getString(R.string.app_name);
        if (TextUtils.isEmpty(mUsername)) {
            // If the username is blank
            final CharSequence msg = getText(R.string.login_activity_blank_field) + " username";
            return msg;
        }
        if (TextUtils.isEmpty(mPassword)) {
            // If the password is blank
            return getText(R.string.login_activity_blank_field) + " password";
        }
        return null;
    }

    /*
     * {@inheritDoc}
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(R.string.ui_activity_authenticating));
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Log.i(LOG_TAG, "dialog cancel has been invoked");
            }
        });
        return dialog;
    }

    /**
     * Shows the progress UI for a lengthy operation.
     */
    protected void showProgress() {
        showDialog(0);
    }

    /**
     * Hides the progress UI for a lengthy operation.
     */
    protected void hideProgress() {
        dismissDialog(0);
    }
}
