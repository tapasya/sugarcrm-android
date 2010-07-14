package com.imaginea.android.sugarcrm;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.imaginea.android.sugarcrm.util.Util;

import java.util.HashMap;
import java.util.Map;

public class SugarCrmSettings extends PreferenceActivity {

    private static final String LOG_TAG = "SugarCrmSettings";

    private static final Map<String, String> savedSettings = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //TODO fix the settings screen for android 2.0 and above
        findViewById(android.R.id.list).setBackgroundResource(R.drawable.bg);
        addPreferencesFromResource(R.xml.sugarcrm_settings);
    }

    // Static getters (extracting data from context)
    public static String getUsername(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(Util.PREF_USERNAME, context.getString(R.string.defaultUser));
    }

    public static String getPassword(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(Util.PREF_PASSWORD, context.getString(R.string.defaultPwd));
    }

    public static boolean isPasswordSaved(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Util.PREF_REMEMBER_PASSWORD, false);
    }

    /**
     * gets SugarCRM RestUrl, on production it returns empty url, if debuggable is set to "false" in
     * the manifest file.
     * 
     * // TODO - optimize once loaded instead of calling package manager everytime
     * 
     * @param context
     * @return
     */
    public static String getSugarRestUrl(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo("com.imaginea.android.sugarcrm", ApplicationInfo.FLAG_DEBUGGABLE);
            if ((ApplicationInfo.FLAG_DEBUGGABLE & appInfo.flags) == ApplicationInfo.FLAG_DEBUGGABLE)
                return PreferenceManager.getDefaultSharedPreferences(context).getString(Util.PREF_REST_URL, context.getString(R.string.defaultUrl));
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return PreferenceManager.getDefaultSharedPreferences(context).getString(Util.PREF_REST_URL, "");
    }

    /**
     * This methods saves the current settings, to be able to check later if settings changed
     */
    public static void saveCurrentSettings(Context context) {
        savedSettings.clear();
        savedSettings.put(Util.PREF_REST_URL, getSugarRestUrl(context));
        savedSettings.put(Util.PREF_USERNAME, getUsername(context));
        savedSettings.put(Util.PREF_PASSWORD, getPassword(context));
    }

    /**
     * This methods tells if the settings have changed.
     */
    public static boolean currentSettingsChanged(Context context) {

        if (savedSettings.isEmpty()) {
            return false;
        }

        try {
            if (!getSugarRestUrl(context).equals(savedSettings.get(Util.PREF_REST_URL))) {
                return true;
            }

            if (!getUsername(context).equals(savedSettings.get(Util.PREF_USERNAME))) {
                return true;
            }

            if (!getPassword(context).equals(savedSettings.get(Util.PREF_PASSWORD))) {
                return true;
            }

            return false;
        } finally {
            savedSettings.clear();
        }
    }

    /**
     * new method for back presses in android 2.0, instead of the standard mechanism defined in the
     * docs to handle legacy applications we use version code to handle back button... implement
     * onKeyDown for older versions and use Override on that.
     */
    public void onBackPressed() {
        // / super.onBackPressed();
        // TODO: onChange of settings, session has to be invalidated
        Log.i(LOG_TAG, "url - " + getSugarRestUrl(SugarCrmSettings.this));
        Log.i(LOG_TAG, "username - " + getUsername(SugarCrmSettings.this));
        Log.i(LOG_TAG, "password - " + getPassword(SugarCrmSettings.this));
        Log.i(LOG_TAG, "pwdSaved - " + isPasswordSaved(SugarCrmSettings.this));

        // invalidate the session if the current settings changes
        if (currentSettingsChanged(SugarCrmSettings.this)) {
            SugarCrmApp app = ((SugarCrmApp) getApplicationContext());
            app.setSessionId(null);
        }

        finish();
    }
}
