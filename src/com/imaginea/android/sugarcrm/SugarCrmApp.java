package com.imaginea.android.sugarcrm;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class SugarCrmApp extends Application {

    // easy ref to App instance for classes which do not have access to Activity/Service context
    public static Application app = null;

    /*
     * sessionId is obtained after successful login into the Sugar CRM instance Now, sessionId will
     * be available to the entire application Access the sessionId from any part of the application
     * as follows : SugarCrmApp app = ((SugarCrmApp) getApplicationContext()); app.getSessionId();
     */
    private String mSessionId;

    public String getSessionId() {
        // TODO - remove this
        if (mSessionId == null) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            // TODO use a constant and remove this as we start from the login screen
            String url = pref.getString("URL", getBaseContext().getString(R.string.defaultUrl));
            String userName = pref.getString("USER_NAME", getBaseContext().getString(R.string.defaultUser));
            String password = pref.getString("PASSWORD", getBaseContext().getString(R.string.defaultPwd));
            Log.i("SugarAPP", url + userName + password);
            try {
                // mSessionId = RestUtil.loginToSugarCRM(url, userName, password);
            } catch (Exception e) {
                Log.e("SugarApp", e.getMessage(), e);
            }
        }
        return mSessionId;
    }

    public void setSessionId(String mSessionId) {
        this.mSessionId = mSessionId;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

}
