package com.imaginea.android.sugarcrm;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ServiceHelper {

    public static void startService(Context context, Uri uri) {
        // send a notify command to the service
        Intent serviceIntent = new Intent(context, SugarService.class);
        serviceIntent.setData(uri);
        context.startService(serviceIntent);
    }
}
