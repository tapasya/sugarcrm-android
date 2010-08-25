package com.imaginea.android.sugarcrm;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.imaginea.android.sugarcrm.util.Util;

import java.util.HashMap;
import java.util.Map;

public class SugarCrmApp extends Application {

    // easy ref to App instance for classes which do not have access to Activity/Service context
    public static Application app = null;

    /*
     * sessionId is obtained after successful login into the Sugar CRM instance Now, sessionId will
     * be available to the entire application Access the sessionId from any part of the application
     * as follows : SugarCrmApp app = ((SugarCrmApp) getApplication()); app.getSessionId();
     */
    private String mSessionId;

    private Map<String, Map<String, String>> moduleSortOrder = new HashMap<String, Map<String, String>>();

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

    /**
     * returns the Account associated with the current user name
     * 
     * @param userName
     * @return
     */
    public Account getAccount(String userName) {

        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType(Util.ACCOUNT_TYPE);
        Account userAccount = null;
        for (Account account : accounts) {
            // never print the password
            // Log.i(LOG_TAG, "user name is " + account.name);
            if (account.name.equals(userName)) {
                userAccount = account;
                break;
            }
        }
        return userAccount;
    }

    public void setSessionId(String mSessionId) {
        this.mSessionId = mSessionId;
    }

    public void setModuleSortOrder(String moduleName, String fieldName, String sortBy) {
        Map<String, String> fieldMap = new HashMap<String, String>();
        fieldMap.put(fieldName, sortBy);
        moduleSortOrder.put(moduleName, fieldMap);
    }

    public Map<String, String> getModuleSortOrder(String moduleName) {
        return moduleSortOrder.get(moduleName);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        setDefaultModuleSortOrders();
    }

    private void setDefaultModuleSortOrders() {
        Map<String, String> fieldMap = new HashMap<String, String>();
        fieldMap.put(ModuleFields.NAME, Util.ASC);
        moduleSortOrder.put(Util.ACCOUNTS, fieldMap);

        fieldMap = new HashMap<String, String>();
        fieldMap.put(ModuleFields.FIRST_NAME, Util.ASC);
        moduleSortOrder.put(Util.CONTACTS, fieldMap);

        fieldMap = new HashMap<String, String>();
        fieldMap.put(ModuleFields.FIRST_NAME, Util.ASC);
        moduleSortOrder.put(Util.LEADS, fieldMap);

        fieldMap = new HashMap<String, String>();
        fieldMap.put(ModuleFields.NAME, Util.ASC);
        moduleSortOrder.put(Util.OPPORTUNITIES, fieldMap);

        fieldMap = new HashMap<String, String>();
        fieldMap.put(ModuleFields.NAME, Util.ASC);
        moduleSortOrder.put(Util.CASES, fieldMap);

        fieldMap = new HashMap<String, String>();
        fieldMap.put(ModuleFields.NAME, Util.ASC);
        moduleSortOrder.put(Util.CALLS, fieldMap);

        fieldMap = new HashMap<String, String>();
        fieldMap.put(ModuleFields.NAME, Util.ASC);
        moduleSortOrder.put(Util.MEETINGS, fieldMap);

        fieldMap = new HashMap<String, String>();
        fieldMap.put(ModuleFields.NAME, Util.ASC);
        moduleSortOrder.put(Util.CAMPAIGNS, fieldMap);
    }

}
