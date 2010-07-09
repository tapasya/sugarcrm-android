package com.imaginea.android.sugarcrm;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.imaginea.android.sugarcrm.util.Util;

/**
 * ServiceHelper
 * 
 * @author chander
 */
public class ServiceHelper {

    public static void startService(Context context, Uri uri, String module, String[] projection,
                                    String sortOrder) {
        // send a notify command to the service
        Intent serviceIntent = new Intent(context, SugarService.class);
        serviceIntent.setData(uri);
        serviceIntent.putExtra(RestUtilConstants.MODULE_NAME, module);
        serviceIntent.putExtra(Util.PROJECTION, projection);
        serviceIntent.putExtra(Util.SORT_ORDER, sortOrder);
        context.startService(serviceIntent);
    }
}
