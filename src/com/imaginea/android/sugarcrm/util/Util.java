package com.imaginea.android.sugarcrm.util;

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

    // constants for settings / Preferences
    public static final String PREF_REST_URL = "rest_url";

    public static final String PREF_USERNAME = "usr";

    public static final String PREF_PASSWORD = "pwd";
    
    public static final String PREF_REMEMBER_PASSWORD = "remember_pwd";

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
}
