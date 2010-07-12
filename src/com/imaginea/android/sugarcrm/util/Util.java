package com.imaginea.android.sugarcrm.util;

import android.content.Context;
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
 * 
 */
public class Util {

    public static final int FETCH_FAILED = 0;

    public static final int REFRESH_LIST = 1;

    public static final int FETCH_SUCCESS = 2;

    // WIZARD: states
    public static final int URL_NOT_AVAILABLE = 0;

    public static final int URL_AVAILABLE = 1;

    public static final int URL_USER_AVAILABLE = 2;

    public static final int URL_USER_PWD_AVAILABLE = 3;

    // constants for settings / Preferences
    public static final String PREF_REST_URL = "restUrl";

    public static final String PREF_USERNAME = "usr";

    public static final String PREF_PASSWORD = "pwd";

    public static final String PREF_REMEMBER_PASSWORD = "rememberPwd";

    public static final String PROJECTION = "select";

    public static final String SORT_ORDER = "orderby";

    public static final String COMMAND = "cmd";

    private static int mRequestId = 0;

    private static final String LOG_TAG = Util.class.getSimpleName();

    // calculate the MD5 hash of a string
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
     * @return
     */
    public static synchronized int getId() {
        mRequestId += 1;
        return mRequestId;
    }

    /**
     * is Network On
     * 
     * @param context
     * @return
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

}
