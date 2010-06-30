package com.imaginea.android.sugarcrm;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.imaginea.android.sugarcrm.util.Util;

import java.util.HashMap;
import java.util.Map;

public class SugarCrmSettings extends PreferenceActivity {

    private static final Map<String, String> savedSettings = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.sugarcrm_settings);
    }

    /**
     * This methods saves the current settings, to be able to check later if settings changed
     * 
     * @param context
     */
    public static void saveCurrentSettings(Context context) {
        savedSettings.clear();
        savedSettings.put(Util.PREF_REST_URL, getSugarRestUrl(context));
        savedSettings.put(Util.PREF_USERNAME, getUsername(context));
        savedSettings.put(Util.PREF_PASSWORD, getPassword(context));
    }

    /**
     * This methods tells is the settings changed. It is used by a callback called when resuming
     * from the ConnectionSettingsActivity
     * 
     * @param context
     *            an activity
     * @return
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

    // Static getters (extracting data from context)

    public static String getUsername(Context context) {

        return PreferenceManager.getDefaultSharedPreferences(context).getString(Util.PREF_USERNAME, context.getString(R.string.default_username));
    }

    public static String getPassword(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(Util.PREF_PASSWORD, context.getString(R.string.default_password));
    }

    public static String getSugarRestUrl(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(Util.PREF_REST_URL, context.getString(R.string.default_url));
    }
}
