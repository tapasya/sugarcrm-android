package com.imaginea.android.sugarcrm.sync;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.R;
import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Contacts;
import com.imaginea.android.sugarcrm.util.Module;
import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.SugarBean;
import com.imaginea.android.sugarcrm.util.SugarCrmException;
import com.imaginea.android.sugarcrm.util.Util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class for managing sugar crm sync related mOperations. should be capable of updating the
 * SyncStats in SyncResult object.
 * 
 * //TODO - pushing changes to server, as well as handling statistics of SyncResult, Merge conflicts
 */
public class SugarSyncManager {

    private static DatabaseHelper databaseHelper;

    // TODO - change the BEAN_ID into sugarContetn
    static String mSelection = Contacts.BEAN_ID + "=?";

    static String mBeanIdField = Contacts.BEAN_ID;

    private static String mQuery = "";// new String[] {};

    // TODO - link field names
    private static Map<String, List<String>> mLinkNameToFieldsArray = new HashMap<String, List<String>>();

    /**
     * make the date formatter static as we are synchronized even though its not thread-safe
     */
    private static DateFormat mDateFormat;

    private static Calendar mCalendar;

    private static final String LOG_TAG = SugarSyncManager.class.getSimpleName();

    /**
     * Synchronize raw contacts
     * 
     * @param context
     *            The context of Authenticator Activity
     * @param account
     *            The username for the account
     * @param sessionId
     *            The session Id associated with sugarcrm session
     * @param moduleName
     *            The name of the module to sync
     */
    public static synchronized void syncModulesData(Context context, String account,
                                    String sessionId, String moduleName) throws SugarCrmException {
        long userId;
        long rawId = 0;
        final ContentResolver resolver = context.getContentResolver();
        final BatchOperation batchOperation = new BatchOperation(context, resolver);
        if (databaseHelper == null)
            databaseHelper = new DatabaseHelper(context);
        int offset = 0;
        int maxResults = 20;
        String deleted = "";
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        // TODO use a constant and remove this as we start from the login screen
        String url = pref.getString(Util.PREF_REST_URL, context.getString(R.string.defaultUrl));

        String[] projections = databaseHelper.getModuleProjections(moduleName);
        String orderBy = databaseHelper.getModuleSortOrder(moduleName);
        setLinkNameToFieldsArray(moduleName);
        while (true) {
            if (projections == null || projections.length == 0)
                break;

            // TODO - Fetching based on dates
            // Date startDate = new Date();
            // long duration = 3 * 30 * 24 * 60 * 60 * 1000;
            long duration = 24 * 60 * 60 * 1000;
            // Date endDate = new Date(startDate.getTime() - duration);
            if (mDateFormat == null)
                mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // mDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            mCalendar = new GregorianCalendar();
            mCalendar.setTimeInMillis(System.currentTimeMillis() - duration);
            String remoteDateStr = mDateFormat.format(mCalendar.getTime());
            // mQuery = moduleName + "." + ModuleFields.DATE_MODIFIED + " > " + "DATE('"
            // + remoteDateStr + "')";
            mQuery = moduleName + "." + ModuleFields.DATE_MODIFIED + " > "
                                            + mCalendar.getTimeInMillis();
            // + " AND " + ModuleFields.DATE_MODIFIED + "< " + startDate.toGMTString();
            // CalendarFormat:@"%Y-%m-%d %H:%M:%S" timeZone:[NSTimeZone timeZoneWithName:@"GMT"]
            // locale:nil];

            SugarBean[] sBeans = RestUtil.getEntryList(url, sessionId, moduleName, mQuery, orderBy, ""
                                            + offset, projections, mLinkNameToFieldsArray, ""
                                            + maxResults, deleted);
            if (Log.isLoggable(LOG_TAG, Log.DEBUG))
                Log.d(LOG_TAG, "fetching " + offset + "to " + (offset + maxResults));
            if (sBeans == null || sBeans.length == 0)
                break;
            if (Log.isLoggable(LOG_TAG, Log.DEBUG))
                Log.d(LOG_TAG, "In Syncmanager");
            for (SugarBean sBean : sBeans) {

                String beandIdValue = sBean.getFieldValue(mBeanIdField);
                // Check to see if the contact needs to be inserted or updated
                rawId = lookupRawId(resolver, moduleName, beandIdValue);
                if (Log.isLoggable(LOG_TAG, Log.VERBOSE))
                    Log.v(LOG_TAG, "beanId/rawid:" + beandIdValue + "/" + rawId);
                if (rawId != 0) {
                    if (!sBean.getFieldValue(ModuleFields.DELETED).equals(Util.DELETED_ITEM)) {
                        // update module Item

                        updateModuleItem(context, resolver, account, moduleName, sBean, rawId, batchOperation);
                    } else {
                        // delete module item - never delete the item here, just update the deleted
                        // flag
                        deleteModuleItem(context, rawId, moduleName, batchOperation);
                    }
                } else {
                    // add new moduleItem
                    // Log.v(LOG_TAG, "In addModuleItem");
                    if (!sBean.getFieldValue(ModuleFields.DELETED).equals(Util.DELETED_ITEM)) {
                        addModuleItem(context, account, sBean, moduleName, batchOperation);
                    }
                }
                // syncRelationships(context, account, sessionId, moduleName, sBean,
                // batchOperation);
                // A sync adapter should batch operations on multiple contacts,
                // because it will make a dramatic performance difference.
                if (batchOperation.size() >= 50) {
                    batchOperation.execute();
                }
            }
            batchOperation.execute();
            offset = offset + maxResults;
            for (SugarBean sBean : sBeans) {
                syncRelationshipsData(context, account, sessionId, moduleName, sBean, batchOperation);
                // A sync adapter should batch operations on multiple contacts,
                // because it will make a dramatic performance difference.
                if (batchOperation.size() >= 50) {
                    batchOperation.execute();
                }
            }
            batchOperation.execute();

           
        }
        mLinkNameToFieldsArray.clear();
        // syncRelationships(context, account, sessionId, moduleName);
        databaseHelper.close();
        databaseHelper = null;
    }

    /**
     * syncRelationships
     * 
     * @param context
     * @param account
     * @param sessionId
     * @param moduleName
     * @param bean
     * @param batchOperation
     */
    public static void syncRelationshipsData(Context context, String account, String sessionId,
                                    String moduleName, SugarBean bean, BatchOperation batchOperation) {
        String[] relationships = databaseHelper.getModuleRelationshipItems(moduleName);
        if (relationships == null) {
            Log.v(LOG_TAG, "relationships is null");
            return;
        }
        String beandIdValue = bean.getFieldValue(mBeanIdField);
        Log.v(LOG_TAG, "syncRelationshipsData: beanId:" + beandIdValue);
       
        long rawId = lookupRawId(context.getContentResolver(), moduleName, beandIdValue);
        Log.v(LOG_TAG, "syncRelationshipsData: RawId:" + rawId);
        for (String relation : relationships) {
            String linkFieldName = databaseHelper.getLinkfieldName(relation);
            // for a particular module-link field name
            SugarBean[] relationshipBeans = bean.getRelationshipBeans(linkFieldName);
            // Log.v(LOG_TAG, "linkFieldName:" + linkFieldName);
            if (relationshipBeans == null || relationshipBeans.length == 0) {
                // if (Log.isLoggable(LOG_TAG, Log.VERBOSE))
                Log.v(LOG_TAG, "relationship beans is null or empty");
                continue;
            }
            for (SugarBean relationbean : relationshipBeans) {
                // long relationRawId = lookupRawId(context.getContentResolver(), relation,
                // relationbean.getBeanId());
                String relationBeanId = relationbean.getFieldValue(mBeanIdField);

                if (relationBeanId == null)
                    continue;
                long relationRawId = lookupRawId(context.getContentResolver(), relation, relationBeanId);
                if (Log.isLoggable(LOG_TAG, Log.VERBOSE))
                    Log.v(LOG_TAG, "RelationBeanId/RelatedRawid:"
                                                    + relationRawId
                                                    + "/" + relationBeanId);
                if (relationRawId != 0) {
                    if (!relationbean.getFieldValue(ModuleFields.DELETED).equals(Util.DELETED_ITEM)) {
                        // update module Item

                        updateRelatedModuleItem(context, context.getContentResolver(), account, moduleName, relation, relationbean, relationRawId, batchOperation);
                    } else {
                        // delete module item
                        deleteRelatedModuleItem(context, rawId, relationRawId, moduleName, relation, batchOperation);
                    }
                } else {
                    // add new moduleItem
                    // Log.v(LOG_TAG, "In addModuleItem");
                    if (!relationbean.getFieldValue(ModuleFields.DELETED).equals(Util.DELETED_ITEM)) {
                        addRelatedModuleItem(context, account, rawId, bean, relationbean, moduleName, relation, batchOperation);
                    }
                }

            }
        }
    }

    public static void setLinkNameToFieldsArray(String moduleName) throws SugarCrmException {
        String[] relationships = databaseHelper.getModuleRelationshipItems(moduleName);
        if (relationships == null)
            return;
        for (String relation : relationships) {
            String linkFieldName = databaseHelper.getLinkfieldName(relation);
            String[] relationProj = databaseHelper.getModuleProjections(relation);
            mLinkNameToFieldsArray.put(linkFieldName, Arrays.asList(relationProj));
        }
    }

    /**
     * Adds a single sugar bean to the sugar crm provider.
     * 
     * @param context
     *            the Authenticator Activity context
     * @param accountName
     *            the account the contact belongs to
     * @param sBean
     *            the SyncAdapter SugarBean object
     */
    private static void addModuleItem(Context context, String accountName, SugarBean sBean,
                                    String moduleName, BatchOperation batchOperation) {
        // Put the data in the contacts provider
        if (Log.isLoggable(LOG_TAG, Log.VERBOSE))
            Log.v(LOG_TAG, "In addModuleItem");
        final SugarCRMOperations moduleItemOp = SugarCRMOperations.createNewModuleItem(context, moduleName, accountName, sBean, batchOperation);
        moduleItemOp.addSugarBean(sBean);

    }

    /**
     * Adds a single sugar bean to the sugar crm provider.
     * 
     * @param context
     *            the Authenticator Activity context
     * @param accountName
     *            the account the contact belongs to
     * @param sBean
     *            the SyncAdapter SugarBean object
     */
    private static void addRelatedModuleItem(Context context, String accountName, long rawId,
                                    SugarBean sBean, SugarBean relatedBean, String moduleName,
                                    String relationModuleName, BatchOperation batchOperation) {
        // Put the data in the contacts provider
        if (Log.isLoggable(LOG_TAG, Log.VERBOSE))
            Log.v(LOG_TAG, "In addRelatedModuleItem");
        Log.v(LOG_TAG, " addRelatedModuleItem Rawid:" + rawId);
        final SugarCRMOperations moduleItemOp = SugarCRMOperations.createNewRelatedModuleItem(context, moduleName, relationModuleName, accountName, rawId, sBean, relatedBean, batchOperation);
        moduleItemOp.addRelatedSugarBean(sBean, relatedBean);

    }

    /**
     * Updates a single module item to the sugar crm content provider.
     * 
     * @param context
     *            the Authenticator Activity context
     * @param resolver
     *            the ContentResolver to use
     * @param accountName
     *            the account the module item belongs to
     * @param moduleName
     *            the name of the module being synced
     * @param sBean
     *            the sugar crm sync adapter object.
     * @param rawId
     *            the unique Id for this raw module item in sugar crm content provider
     */
    private static void updateModuleItem(Context context, ContentResolver resolver,
                                    String accountName, String moduleName, SugarBean sBean,
                                    long rawId, BatchOperation batchOperation) {
        if (Log.isLoggable(LOG_TAG, Log.VERBOSE))
            Log.v(LOG_TAG, "In updateModuleItem");
        Uri contentUri = databaseHelper.getModuleUri(moduleName);
        String[] projections = DatabaseHelper.getModuleProjections(moduleName);
        Uri uri = ContentUris.withAppendedId(contentUri, rawId);
        // TODO - is this query resolver needed to query here
        final Cursor c = resolver.query(contentUri, projections, mSelection, new String[] { String.valueOf(rawId) }, null);
        // TODO - do something here with cursor
        c.close();
        final SugarCRMOperations moduleItemOp = SugarCRMOperations.updateExistingModuleItem(context, moduleName, sBean, rawId, batchOperation);
        moduleItemOp.updateSugarBean(sBean, uri);

    }

    private static void updateRelatedModuleItem(Context context, ContentResolver resolver,
                                    String accountName, String moduleName, String relatedModuleName,
                                    SugarBean relatedBean, long relationRawId,
                                    BatchOperation batchOperation) {
        if (Log.isLoggable(LOG_TAG, Log.VERBOSE))
            Log.v(LOG_TAG, "In updateRelatedModuleItem");
        Uri contentUri = databaseHelper.getModuleUri(relatedModuleName);
        String[] projections = DatabaseHelper.getModuleProjections(relatedModuleName);
        Uri uri = ContentUris.withAppendedId(contentUri, relationRawId);
        Log.v(LOG_TAG, "updateRelatedModuleItem URI:" + uri.toString());
        // TODO - is this query resolver needed to query here
        final Cursor c = resolver.query(contentUri, projections, mSelection, new String[] { String.valueOf(relationRawId) }, null);
        // TODO - do something here with cursor
        c.close();
        final SugarCRMOperations moduleItemOp = SugarCRMOperations.updateExistingModuleItem(context, relatedModuleName, relatedBean, relationRawId, batchOperation);
        moduleItemOp.updateSugarBean(relatedBean, uri);

    }

    /**
     * Deletes a module item from the sugar crm provider.
     * 
     * @param context
     *            the Authenticator Activity context
     * @param rawId
     *            the unique Id for this rawId in the sugar crm provider
     */
    private static void deleteModuleItem(Context context, long rawId, String moduleName,
                                    BatchOperation batchOperation) {
        Uri contentUri = databaseHelper.getModuleUri(moduleName);

        batchOperation.add(SugarCRMOperations.newDeleteCpo(ContentUris.withAppendedId(contentUri, rawId), true).build());
    }

    /**
     * Deletes a module item from the sugar crm provider.
     * 
     * @param context
     *            the Authenticator Activity context
     * @param rawId
     *            the unique Id for this rawId in the sugar crm provider
     */
    private static void deleteRelatedModuleItem(Context context, long rawId, long relatedRawId,
                                    String moduleName, String relaledModuleName,
                                    BatchOperation batchOperation) {
        Uri contentUri = databaseHelper.getModuleUri(moduleName);
        Uri parentUri = ContentUris.withAppendedId(contentUri, rawId);
        Uri relatedUri = Uri.withAppendedPath(parentUri, relaledModuleName);
        Uri deleteUri = ContentUris.withAppendedId(relatedUri, relatedRawId);
        batchOperation.add(SugarCRMOperations.newDeleteCpo(deleteUri, true).build());
    }

    /**
     * Returns the Raw Module item id for a sugar crm SyncAdapter , or 0 if the item is not found.
     * 
     * @param context
     *            the Authenticator Activity context
     * @param userId
     *            the SyncAdapter bean ID to lookup
     * @return the Raw item id, or 0 if not found
     */
    private static long lookupRawId(ContentResolver resolver, String moduleName, String beanId) {
        long rawId = 0;
        Uri contentUri = databaseHelper.getModuleUri(moduleName);
        String[] projection = new String[] { SugarCRMContent.RECORD_ID };

        final Cursor c = resolver.query(contentUri, projection, mSelection, new String[] { beanId }, null);
        try {
            if (c.moveToFirst()) {
                rawId = c.getLong(0);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return rawId;
    }

    /**
     * syncModules, syncs the changes to any modules that are associated with the user
     * 
     * @param context
     * @param account
     * @param sessionId
     */
    public static synchronized void syncModules(Context context, String account, String sessionId) {
        if (databaseHelper == null)
            databaseHelper = new DatabaseHelper(context);
        List<String> userModules = databaseHelper.getModuleList();

        Set<Module> moduleFieldsInfo = new HashSet<Module>();
        for (String moduleName : userModules) {
            String[] fields = {};
            try {
                // TODO: check if the module is already there in the db. make the rest call
                // only if it isn't
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                // TODO use a constant and remove this as we start from the login screen
                String url = pref.getString(Util.PREF_REST_URL, context.getString(R.string.defaultUrl));
                Module module = RestUtil.getModuleFields(url, sessionId, moduleName, fields);
                moduleFieldsInfo.add(module);
                Log.i(LOG_TAG, "loaded module fields for : " + moduleName);
            } catch (SugarCrmException sce) {
                Log.e(LOG_TAG, "failed to load module fields for : " + moduleName);
            }
        }
        try {
            databaseHelper.setModuleFieldsInfo(moduleFieldsInfo);
        } catch (SugarCrmException sce) {
            Log.e(LOG_TAG, sce.getMessage(), sce);
            // TODO
        }
        databaseHelper.close();
        databaseHelper = null;
    }
}
