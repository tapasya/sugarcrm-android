package com.imaginea.android.sugarcrm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.SugarCrmException;
import com.imaginea.android.sugarcrm.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class WizardActivity extends Activity {

    private final String LOG_TAG = "WizardActivity";

    // In-order list of wizard steps to present to user. These are layout resource ids.
    public final static int[] STEPS = new int[] { R.layout.url_config_wizard,
            R.layout.login_activity };

    protected ViewFlipper flipper = null;

    protected Button next, prev;

    private SugarCrmApp app;

    private boolean isValidUrl = false;

    private UrlValidationTask mUrlTask;

    private AuthenticationTask mAuthTask;

    private LayoutInflater mInflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sugar_wizard);

        app = ((SugarCrmApp) getApplicationContext());

        this.flipper = (ViewFlipper) this.findViewById(R.id.wizard_flipper);
        prev = (Button) this.findViewById(R.id.action_prev);
        next = (Button) this.findViewById(R.id.action_next);

        final String restUrl = SugarCrmSettings.getSugarRestUrl(WizardActivity.this);
        final String usr = SugarCrmSettings.getUsername(WizardActivity.this).toString();
        final String pwd = SugarCrmSettings.getPassword(WizardActivity.this).toString();
        final boolean rememberedPwd = SugarCrmSettings.isPasswordSaved(WizardActivity.this);
        Log.i(LOG_TAG, "restUrl - " + restUrl + "\n usr - " + usr + "\n pwd - " + pwd
                                        + "\n rememberedPwd - " + rememberedPwd);
        mInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (TextUtils.isEmpty(restUrl)) {
            Log.i(LOG_TAG, "REST URL is not available!");
            // inflate both url layout and username_password layout

            for (int layout : STEPS) {
                View step = mInflater.inflate(layout, this.flipper, false);
                this.flipper.addView(step);
            }
            //TODO updateButtons();
        } else if (TextUtils.isEmpty(usr)) {
            Log.i(LOG_TAG, "REST URL is available but not the username!");
            // inflate only the username_password layout
            View loginView = mInflater.inflate(STEPS[1], this.flipper, false);
            this.flipper.addView(loginView);

            Button loginBtn = (Button) loginView.findViewById(R.id.login_button_ok);
            loginBtn.setVisibility(View.VISIBLE);

        } else {
            Log.i(LOG_TAG, "REST URL and username are available!");
            // if the password is already saved
            if (rememberedPwd) {
                Log.i(LOG_TAG, "Password is remembered!");
                mAuthTask = new AuthenticationTask();
                mAuthTask.execute(usr, pwd, rememberedPwd);

            } else {
                Log.i(LOG_TAG, "prompt the user for password!");
                // show the dialog to enter password
                final View loginView = mInflater.inflate(R.layout.login_activity, this.flipper, false);
                EditText editTextUser = (EditText) loginView.findViewById(R.id.login_edit_username);
                editTextUser.setText(usr);
                editTextUser.setEnabled(false);

                final AlertDialog loginDialog = new AlertDialog.Builder(WizardActivity.this).setIcon(R.drawable.alert_dialog_icon).setTitle(R.string.login_activity_password_label).setView(loginView).setPositiveButton(R.string.signIn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /* User clicked OK so do some stuff */
                        EditText etPwd = ((EditText) loginView.findViewById(R.id.login_edit_password));
                        boolean rememberPwd = ((CheckBox) loginView.findViewById(R.id.login_remember_password)).isChecked();

                        mAuthTask = new AuthenticationTask();
                        mAuthTask.execute(usr, etPwd.getText().toString(), rememberPwd);

                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /* User clicked cancel so do some stuff */
                        WizardActivity.this.finish();
                    }
                }).create();

                loginDialog.show();
            }
        }

        next.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                if (isFirstDisplayed()) {

                    String url = ((EditText) flipper.findViewById(R.id.wizard_url_edit)).getText().toString();
                    TextView tv = (TextView) flipper.findViewById(R.id.wizard_url_status_message);
                    if (TextUtils.isEmpty(url)) {
                        tv.setText(getString(R.string.login_activity_blank_field)
                                                        + " REST url \n\n"
                                                        + getBaseContext().getString(R.string.wizard_sample_url));
                    } else {
                        mUrlTask = new UrlValidationTask();
                        mUrlTask.execute(url);
                    }

                } else if (isLastDisplayed()) {
                    handleLogin(v);
                } else {
                    // show next step and update buttons
                    flipper.showNext();
                    updateButtons();
                }
            }
        });

        prev.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (isFirstDisplayed()) {
                    // user walked past beginning of wizard, so return that they cancelled
                    WizardActivity.this.setResult(Activity.RESULT_CANCELED);
                    WizardActivity.this.finish();
                } else {
                    // show previous step and update buttons
                    flipper.showPrevious();
                    updateButtons();
                }
            }
        });

        this.updateButtons();

    }

    public void handleLogin(View view) {
        String usr = ((EditText) flipper.findViewById(R.id.login_edit_username)).getText().toString();
        String pwd = ((EditText) flipper.findViewById(R.id.login_edit_password)).getText().toString();
        boolean rememberPwd = ((CheckBox) flipper.findViewById(R.id.login_remember_password)).isChecked();

        TextView tv = (TextView) flipper.findViewById(R.id.login_message_bottom);
        String msg = "";
        if (TextUtils.isEmpty(usr) || TextUtils.isEmpty(pwd)) {
            msg = getString(R.string.login_activity_blank_field) + "username and password.\n";
            tv.setText(msg);
        } else {
            mAuthTask = new AuthenticationTask();
            mAuthTask.execute(usr, pwd, rememberPwd);
        }

    }

    protected void showActivity(Class _class) {
        Intent myIntent = new Intent(WizardActivity.this, _class);
        WizardActivity.this.startActivity(myIntent);
    }

    protected boolean isFirstDisplayed() {
        return (flipper.getDisplayedChild() == 0);
    }

    protected boolean isLastDisplayed() {
        return (flipper.getDisplayedChild() == flipper.getChildCount() - 1);
    }

    protected void updateButtons() {
        if (isFirstDisplayed()) {
            prev.setVisibility(View.INVISIBLE);
            next.setText("Next");
            next.setVisibility(View.VISIBLE);
        } else if (isLastDisplayed()) {
            next.setText("Finish");
            prev.setVisibility(View.VISIBLE);
        } else {
            prev.setVisibility(View.VISIBLE);
            next.setText("Next");
        }
    }

    // Task to validate the REST URL
    class UrlValidationTask extends AsyncTask<Object, Void, Object> {

        private boolean hasExceptions = false;

        private String sceDesc;

        @Override
        protected Object doInBackground(Object... urls) {
            try {
                isValidUrl = isValidUrl(urls[0].toString());
            } catch (SugarCrmException sce) {
                hasExceptions = true;
                sceDesc = sce.getDescription();
                Log.e(LOG_TAG, sce.getDescription(), sce);
            }
            return urls[0].toString();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Object restUrl) {
            super.onPostExecute(restUrl);
            if (isCancelled())
                return;

            TextView tv = (TextView) flipper.findViewById(R.id.wizard_url_status_message);

            if (hasExceptions) {
                tv.setText("Invalid Url : "
                                                + sceDesc
                                                + "\n\n Please check the url you have entered! \n\n"
                                                + getBaseContext().getString(R.string.wizard_sample_url));
            } else {
                if (isValidUrl) {
                    tv.setText("VALID URL");
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WizardActivity.this);
                    Editor editor = sp.edit();
                    editor.putString(Util.PREF_REST_URL, restUrl.toString());
                    editor.commit();

                    // show next step and update buttons
                    flipper.showNext();
                    updateButtons();
                } else {
                    tv.setText("Invalid Url : "
                                                    + "\n\n Please check the url you have entered! \n\n"
                                                    + getBaseContext().getString(R.string.wizard_sample_url));
                }
            }

        }

        protected boolean isValidUrl(String restUrl) throws SugarCrmException {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet reqUrl = new HttpGet(restUrl);
                HttpResponse response = httpClient.execute(reqUrl);
                int statusCode = response.getStatusLine().getStatusCode();
                return statusCode == 200 ? true : false;
            } catch (IllegalStateException ise) {
                throw new SugarCrmException(ise.getMessage());
            } catch (ClientProtocolException cpe) {
                throw new SugarCrmException(cpe.getMessage());
            } catch (IOException ioe) {
                throw new SugarCrmException(ioe.getMessage());
            } catch (Exception e) {
                throw new SugarCrmException(e.getMessage());
            }
        }
    }

    // Task to authenticate
    class AuthenticationTask extends AsyncTask<Object, Void, Object> {
        private String usr;

        private String pwd;

        private boolean rememberPwd;

        boolean hasExceptions = false;

        private String sceDesc;

        @Override
        protected Object doInBackground(Object... args) {
            /*
             * arg[0] : String - username arg[1] : String - password arg[2] : boolean -
             * rememberPassword
             */
            usr = args[0].toString();
            pwd = args[1].toString();
            rememberPwd = Boolean.valueOf(args[2].toString());
            String url = SugarCrmSettings.getSugarRestUrl(getBaseContext());

            String sessionId = null;

            try {
                sessionId = RestUtil.loginToSugarCRM(url, usr, Util.MD5(pwd));
                Log.i(LOG_TAG, "SessionId - " + sessionId);
            } catch (SugarCrmException sce) {
                hasExceptions = true;
                sceDesc = sce.getDescription();
            }

            return sessionId;

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Object sessionId) {
            super.onPostExecute(sessionId);
            if (isCancelled())
                return;

            TextView tv = (TextView) flipper.findViewById(R.id.login_message_bottom);
            if (hasExceptions) {
                // TODO: description isn't coming. have to check this!
                tv.setText(sceDesc);               
            } else {

                // save the sessionId in the application context after the succesful login
                app.setSessionId(sessionId.toString());

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WizardActivity.this);
                Editor editor = sp.edit();
                editor.putString(Util.PREF_USERNAME, usr);
                if (rememberPwd) {
                    editor.putString(Util.PREF_PASSWORD, pwd);
                    editor.putBoolean(Util.PREF_REMEMBER_PASSWORD, true);
                }
                editor.commit();

                showActivity(DashboardActivity.class);
                // user walked past end of wizard, so return okay
                /*
                 * WizardActivity.this.setResult(Activity.RESULT_OK); WizardActivity.this.finish();
                 */

            }

        }
    }

}
