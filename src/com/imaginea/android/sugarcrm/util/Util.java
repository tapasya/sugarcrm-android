package com.imaginea.android.sugarcrm.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Common utilties and constants required by everyone
 * 
 * @author Vasavi
 * @author chander
 */
public class Util {

    private static int mRequestId = 0;

    /**
     * Account type string.
     */
    public static final String ACCOUNT_TYPE = "com.imaginea.android.sugarcrm";

    /**
     * Authtoken type string.
     */
    public static final String AUTHTOKEN_TYPE = "com.imaginea.android.sugarcrm";

    public static final int FETCH_FAILED = 0;

    public static final int REFRESH_LIST = 1;

    public static final int FETCH_SUCCESS = 2;

    // WIZARD: states
    public static final int OFFLINE_MODE = 0;

    public static final int URL_NOT_AVAILABLE = 1;

    public static final int URL_AVAILABLE = 2;

    public static final int URL_USER_AVAILABLE = 3;

    public static final int URL_USER_PWD_AVAILABLE = 4;

    // View modes : EDIT / NEW

    public static final int EDIT_ORPHAN_MODE = 0;

    public static final int EDIT_RELATIONSHIP_MODE = 1;

    public static final int NEW_ORPHAN_MODE = 2;

    public static final int NEW_RELATIONSHIP_MODE = 3;

    public static final int ASSIGNED_ITEMS_MODE = 4;

    public static final int LIST_MODE = 5;

    public static final int CONTACT_IMPORT_FLAG = 1;

    // constants for settings / Preferences
    public static final String PREF_REST_URL = "restUrl";

    public static final String PREF_USERNAME = "usr";

    public static final String PREF_PASSWORD = "pwd";

    public static final String PREF_REMEMBER_PASSWORD = "rememberPwd";

    public static final String PROJECTION = "select";

    public static final String SORT_ORDER = "orderby";

    public static final String COMMAND = "cmd";

    public static final String ROW_ID = "row_id";

    public static final String EXCLUDE_DELETED_ITEMS = "0";

    public static final String INCLUDE_DELETED_ITEMS = "1";

    public static final String DELETED_ITEM = "1";

    public static final String NEW_ITEM = "0";

    public static final String CASE_ID = "case_id";

    // module names
    public static final String ACCOUNTS = "Accounts";

    public static final String CONTACTS = "Contacts";

    public static final String LEADS = "Leads";

    public static final String OPPORTUNITIES = "Opportunities";

    public static final String MEETINGS = "Meetings";

    public static final String CALLS = "Calls";

    public static final String CASES = "Cases";

    public static final String CAMPAIGNS = "Campaigns";

    public static final String USERS = "Users";

    public static final String ACLROLES = "ACLRoles";

    public static final String ACLACTIONS = "ACLActions";

    public static final String STATUS = "Status";

    public static final String RECENT = "Recent";

    public static final String IMPORT_FLAG = "importFlag";

    public static final String CONTACT_NAME = "name";

    public static final String CONTACT_EMAIL = "email";

    public static final String CONTACT_PHNO = "phno";

    // sql sort order contacts
    public static final String ASC = "ASC";

    public static final String DESC = "DESC";

    // CRUD constants
    public static final int GET = 0;

    public static final int INSERT = 1;

    public static final int UPDATE = 2;

    public static final int DELETE = 3;

    // sync status
    public static final int UNSYNCED = 0;

    public static final int SYNC_CONFLICTS = 1;

    // sync constants
    // RECORD_ID (_id) of the module that needs to be synced
    public static final String SYNC_ID = "sync_id";

    public static final String SYNC_RELATED_ID = "sync_related_id";

    // The sync command - INSERT, DELETE, UPDATE
    public static final String SYNC_COMMAND = "sync_cmd";

    public static final String RELATED_MODULE = "related_module";

    // Sync operations
    public static final String SYNC_TYPE = "sync_type";

    public static final String SYNC_METADATA_COMPLETED = "metadata";

    public static final int SYNC_MODULE_META_DATA = 0;

    public static final int SYNC_ACL_ACCESS_META_DATA = 1;

    public static final int SYNC_MODULES_DATA = 2;

    public static final int SYNC_MODULE_DATA = 3;

    public static final int SYNC_ALL_META_DATA = 4;

    public static final int SYNC_ALL = 5;

    public static final String PREF_SYNC_START_TIME = "syncStart";

    public static final String PREF_SYNC_END_TIME = "syncEnd";

    // sub Activity request codes
    public static final int LOGIN_REQUEST_CODE = 0;

    public static final int SYNC_DATA_REQUEST_CODE = 1;

    public static final int IMPORT_CONTACTS_REQUEST_CODE = 1;

    public static final String SQL_FILE = "sortOrderAndGroup.sql";

    private static final String LOG_TAG = Util.class.getSimpleName();

    /**
     * <p>
     * MD5, calculate the MD5 hash of a string
     * </p>
     * 
     * @param text
     *            a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public static String MD5(String text) throws SugarCrmException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5hash = new byte[32];
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            md5hash = md.digest();
            return convertToHex(md5hash);
        } catch (UnsupportedEncodingException ue) {
            throw new SugarCrmException(ue.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new SugarCrmException(e.getMessage());
        }

    }

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    /**
     * changed from private to public so that anyone requiring unique requestIds for Pending Intents
     * can get it
     * 
     * @return a int.
     */
    public static synchronized int getId() {
        mRequestId += 1;
        return mRequestId;
    }

    /**
     * is Network On
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @return a boolean.
     */
    public static boolean isNetworkOn(Context context) {
        boolean networkOn = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                Log.d(LOG_TAG, "network state name:" + networkInfo.getState().name());
                Log.d(LOG_TAG, "NetworkInfo.State.CONNECTED name:"
                                                + NetworkInfo.State.CONNECTED.name());
            }
            if (networkInfo.getState().name().equals(NetworkInfo.State.CONNECTED.name())) {
                networkOn = true;
            }
        }
        return networkOn;
    }

    /**
     * Post notification, the package name of the context should be same as that of any activity you
     * want to start.
     * 
     * @param context
     *            - context of the component posting notification, can be an activity/service etc
     * @param clazz
     *            class of the optional activity that can be started
     * @param message
     *            a {@link java.lang.String} object.
     * @return ID of notification so it can be cancelled/updated
     * @param tickerTextRes
     *            a int.
     * @param titleRes
     *            a int.
     */
    public static synchronized int notify(Context context, Class<Activity> clazz,
                                    int tickerTextRes, int titleRes, String message) {

        // ComponentName comp = new ComponentName(context, clazz);
        return notify(context, context.getPackageName(), clazz, tickerTextRes, titleRes, message);

    }

    /**
     * Post notification, the package name should be same as that of any activity clazz you want to
     * start and the context passed can be in any other package
     * 
     * @param context
     *            - context of the component posting notification, can be an activity/service etc
     * @param clazz
     *            class of the optional activity that can be started
     * @param packageName
     *            a {@link java.lang.String} object.
     * @param tickerTextRes
     *            a int.
     * @param titleRes
     *            a int.
     * @param message
     *            a {@link java.lang.String} object.
     * 
     * @return ID of notification so it can be cancelled/updated
     */
    public static synchronized int notify(Context context, String packageName, Class clazz,
                                    int tickerTextRes, int titleRes, String message) {
        CharSequence tickerText = context.getResources().getText(tickerTextRes);
        CharSequence title = context.getResources().getText(titleRes);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        ComponentName comp = new ComponentName(packageName, clazz.getSimpleName());
        Intent intent = new Intent().setComponent(comp);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        Notification n = new Notification(android.R.drawable.stat_notify_sync_noanim, tickerText, System.currentTimeMillis());
        n.setLatestEventInfo(context, title, message, pendingIntent);
        int id = getId();
        nm.notify(id, n);
        return id;
    }
}
