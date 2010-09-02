package com.imaginea.android.sugarcrm.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * Utility class for commons views required by activities
 * 
 * @author Vasavi
 * @author chander
 */
public class ViewUtil {

    /**
     * handle to a progress dialog used by all the activities
     */
    private static ProgressDialog mProgressDialog;

    private static final String LOG_TAG = "ViewUtil";

    /**
     * show ProgressDialog
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param startMsg
     *            a {@link java.lang.String} object.
     */
    public static void showProgressDialog(Context context, String startMsg) {
        showProgressDialog(context, startMsg, true);
    }

    /**
     * show ProgressDialog
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param startMsg
     *            a {@link java.lang.String} object.
     * @param indeterminate
     *            a boolean.
     */
    public static void showProgressDialog(Context context, String startMsg, boolean indeterminate) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(indeterminate);
        // mProgressDialog.setMax(100);
        mProgressDialog.setMessage(startMsg);
        mProgressDialog.show();
    }

    /**
     * get ProgressDialog
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param startMsg
     *            a {@link java.lang.String} object.
     * @param indeterminate
     *            a boolean.
     * @return a {@link android.app.ProgressDialog} object.
     */
    public static ProgressDialog getProgressDialog(Context context, String startMsg,
                                    boolean indeterminate) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(indeterminate);
        // mProgressDialog.setMax(100);
        progressDialog.setMessage(startMsg);
        return progressDialog;
        // mProgressDialog.show();
    }

    /**
     * cancel ProgressBar
     */
    public static void cancelProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.cancel();
    }

    /**
     * helper method to display a toast specified by the resource id (strings.xml)
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param resid
     *            a int.
     */
    public static void makeToast(Context context, int resid) {
        Toast toast = Toast.makeText(context, resid, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * helper method to display a toast specified by the resource id (strings.xml)
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param str
     *            a {@link java.lang.CharSequence} object.
     */
    public static void makeToast(Context context, CharSequence str) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * showFormattedToast
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param id
     *            a int.
     * @param args
     *            a {@link java.lang.Object} object.
     */
    public static void showFormattedToast(Context context, int id, Object... args) {
        Toast.makeText(context, String.format(context.getString(id), args), Toast.LENGTH_LONG).show();
    }

    /**
     * dismissVirtualKeyboard
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param view
     *            a {@link android.view.View} object.
     */
    public static void dismissVirtualKeyboard(Context context, View view) {

        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // if(inputManager.isActive(view))
        inputManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        // inputManager.showSoftInput(ourCanvasInstance, 0);
        // inputManager.hideSoftInputFromInputMethod(this, 0);
        // && inputManager.isFullscreenMode()
        // if(inputManager.isActive())
        // {
        // inputManager.toggleSoftInput(0, 0);
        // }
    }

}
