package com.imaginea.android.sugarcrm;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.imaginea.android.sugarcrm.util.Util;

public class SugarCrmSettings extends PreferenceActivity {

    private static final String LOG_TAG = "SugarCrmSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.sugarcrm_settings);
    }

    // Static getters (extracting data from context)
    public static String getUsername(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(Util.PREF_USERNAME, context.getString(R.string.default_username));
    }

    public static String getPassword(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(Util.PREF_PASSWORD, context.getString(R.string.default_password));
    }

    public static boolean isPasswordSaved(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Util.PREF_REMEMBER_PASSWORD, false);
    }

    public static String getSugarRestUrl(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(Util.PREF_REST_URL, context.getString(R.string.default_url));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // TODO: onChange of settings, session has to be invalidated
        Log.i(LOG_TAG, "url - " + getSugarRestUrl(SugarCrmSettings.this));
        Log.i(LOG_TAG, "username - " + getUsername(SugarCrmSettings.this));
        Log.i(LOG_TAG, "password - " + getPassword(SugarCrmSettings.this));
        Log.i(LOG_TAG, "pwdSaved - " + isPasswordSaved(SugarCrmSettings.this));
    }
}
