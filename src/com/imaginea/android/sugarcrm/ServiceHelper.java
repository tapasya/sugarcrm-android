package com.imaginea.android.sugarcrm;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.imaginea.android.sugarcrm.util.Util;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ServiceHelper
 * 
 * @author chander
 */
public class ServiceHelper {

    /**
     * <p>
     * startService
     * </p>
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param uri
     *            a {@link android.net.Uri} object.
     * @param module
     *            a {@link java.lang.String} object.
     * @param projection
     *            an array of {@link java.lang.String} objects.
     * @param sortOrder
     *            a {@link java.lang.String} object.
     */
    public static void startService(Context context, Uri uri, String module, String[] projection,
                                    String sortOrder) {
        // send a notify command to the service
        Intent serviceIntent = new Intent(context, SugarService.class);
        serviceIntent.setData(uri);
        serviceIntent.putExtra(Util.COMMAND, Util.GET);
        serviceIntent.putExtra(RestUtilConstants.MODULE_NAME, module);
        serviceIntent.putExtra(Util.PROJECTION, projection);
        serviceIntent.putExtra(Util.SORT_ORDER, sortOrder);
        context.startService(serviceIntent);
    }

    /**
     * <p>
     * startServiceForDelete
     * </p>
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param uri
     *            a {@link android.net.Uri} object.
     * @param module
     *            a {@link java.lang.String} object.
     * @param beanId
     *            a {@link java.lang.String} object.
     */
    public static void startServiceForDelete(Context context, Uri uri, String module, String beanId) {
        Intent serviceIntent = new Intent(context, SugarService.class);
        serviceIntent.setData(uri);
        Map<String, String> nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put(RestUtilConstants.BEAN_ID, beanId);
        nameValuePairs.put(ModuleFields.DELETED, Util.DELETED_ITEM);

        serviceIntent.putExtra(Util.COMMAND, Util.DELETE);
        serviceIntent.putExtra(RestUtilConstants.MODULE_NAME, module);
        serviceIntent.putExtra(RestUtilConstants.BEAN_ID, beanId);
        serviceIntent.putExtra(RestUtilConstants.NAME_VALUE_LIST, (Serializable) nameValuePairs);
        context.startService(serviceIntent);
    }

    /**
     * <p>
     * startServiceForUpdate
     * </p>
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param uri
     *            a {@link android.net.Uri} object.
     * @param module
     *            a {@link java.lang.String} object.
     * @param beanId
     *            a {@link java.lang.String} object.
     * @param nameValueList
     *            a {@link java.util.Map} object.
     */
    public static void startServiceForUpdate(Context context, Uri uri, String module,
                                    String beanId, Map<String, String> nameValueList) {
        Intent serviceIntent = new Intent(context, SugarService.class);
        serviceIntent.setData(uri);

        serviceIntent.putExtra(Util.COMMAND, Util.UPDATE);
        serviceIntent.putExtra(RestUtilConstants.BEAN_ID, beanId);
        serviceIntent.putExtra(RestUtilConstants.MODULE_NAME, module);
        serviceIntent.putExtra(RestUtilConstants.NAME_VALUE_LIST, (Serializable) nameValueList);
        context.startService(serviceIntent);
    }

    /**
     * <p>
     * startServiceForInsert
     * </p>
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param uri
     *            a {@link android.net.Uri} object.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param nameValueList
     *            a {@link java.util.Map} object.
     */
    public static void startServiceForInsert(Context context, Uri uri, String moduleName,
                                    Map<String, String> nameValueList) {
        Intent serviceIntent = new Intent(context, SugarService.class);
        serviceIntent.setData(uri);

        serviceIntent.putExtra(Util.COMMAND, Util.INSERT);
        serviceIntent.putExtra(RestUtilConstants.MODULE_NAME, moduleName);
        serviceIntent.putExtra(RestUtilConstants.NAME_VALUE_LIST, (Serializable) nameValueList);
        context.startService(serviceIntent);
    }
}
