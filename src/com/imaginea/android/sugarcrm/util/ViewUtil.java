package com.imaginea.android.sugarcrm.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

public class ViewUtil {

    private static final String LOG_TAG = "ViewUtil";

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

}
