package com.imaginea.android.sugarcrm.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

/**
 * Utility class for commons views required by activities
 * 
 * @author Vasavi
 * @author chander
 * 
 */
public class ViewUtil {

    private static final String LOG_TAG = "ViewUtil";

    /**
     * get ProgressBar
     * 
     * @param context
     * @param message
     * @return
     */
    public static Dialog getProgressBar(Context context, String message) {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Log.i(LOG_TAG, "dialog cancel has been invoked");
            }
        });
        return dialog;
    }

    /**
     * helper method to display a toast specified by the resource id (strings.xml)
     * 
     * @param context
     * @param resid
     */
    public static void makeToast(Context context, int resid) {
        Toast toast = Toast.makeText(context, resid, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * showFormattedToast
     * 
     * @param context
     * @param id
     * @param args
     */
    public static void showFormattedToast(Context context, int id, Object... args) {
        Toast.makeText(context, String.format(context.getString(id), args), Toast.LENGTH_LONG).show();
    }

}
