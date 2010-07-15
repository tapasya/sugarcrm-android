package com.imaginea.android.sugarcrm.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Accounts;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AccountsColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Contacts;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ContactsColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Leads;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.LeadsColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Opportunities;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.OpportunitiesColumns;
import com.imaginea.android.sugarcrm.util.ModuleField;

import java.util.HashMap;
import java.util.List;

/**
 * This class helps open, create, and upgrade the database file.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sugar_crm.db";

    // TODO:
    private static final int DATABASE_VERSION = 8;

    public static final String ACCOUNTS_TABLE_NAME = "accounts";

    public static final String CONTACTS_TABLE_NAME = "contacts";

    public static final String LEADS_TABLE_NAME = "leads";

    public static final String OPPORTUNITIES_TABLE_NAME = "opportunities";

    public static final String MEETINGS_TABLE_NAME = "meetings";

    public static final String CALLS_TABLE_NAME = "calls";

    private static final String TAG = "DatabaseHelper";

    // TODO - replace with database calls - dynamic module generation
    public static List<String> modulesList ;
    public static final HashMap<String, String> modules = new HashMap<String, String>();

    public static final HashMap<String, String[]> moduleProjections = new HashMap<String, String[]>();

    public static final HashMap<String, String[]> moduleListSelections = new HashMap<String, String[]>();

    public static final HashMap<String, String> moduleSortOrder = new HashMap<String, String>();

    public static final HashMap<String, Uri> moduleUris = new HashMap<String, Uri>();

    public static final HashMap<String, String> moduleSelections = new HashMap<String, String>();

    public static HashMap<String, HashMap<String, ModuleField>> moduleFields ;// new HashMap<String, HashMap<String, ModuleField>>();
    
    public static final HashMap<String, String[]> moduleMenuItems = new HashMap<String, String[]>();
    
    public static final HashMap<String, String> pathForRelationship = new HashMap<String, String>();

    static {
        // modules.put(0, "Accounts");
        // modules.put(1, "Contacts");
        // modules.put(2, "Leads");
        // modules.put(3, "Opportunity");
        // modules.put(4, "Meetings");
        // modules.put(5, "Calls");

        moduleProjections.put("Accounts", Accounts.DETAILS_PROJECTION);
        moduleProjections.put("Contacts", Contacts.DETAILS_PROJECTION);
        moduleProjections.put("Leads", Leads.DETAILS_PROJECTION);
        moduleProjections.put("Opportunities", Opportunities.DETAILS_PROJECTION);
        // moduleProjections.put(4, Meetings.DETAILS_PROJECTION );

        moduleListSelections.put("Accounts", Accounts.LIST_VIEW_PROJECTION);
        moduleListSelections.put("Contacts", Contacts.LIST_VIEW_PROJECTION);
        moduleListSelections.put("Leads", Leads.LIST_VIEW_PROJECTION);
        moduleListSelections.put("Opportunities", Opportunities.LIST_VIEW_PROJECTION);

        moduleSortOrder.put("Accounts", Accounts.DEFAULT_SORT_ORDER);
        moduleSortOrder.put("Contacts", Contacts.DEFAULT_SORT_ORDER);

        moduleUris.put("Accounts", Accounts.CONTENT_URI);
        moduleUris.put("Contacts", Contacts.CONTENT_URI);
        moduleUris.put("Leads", Leads.CONTENT_URI);
        moduleUris.put("Opportunities", Opportunities.CONTENT_URI);
        
        moduleMenuItems.put("Accounts", new String[]{"Contacts", "Leads", "Opportunities"});
        moduleMenuItems.put("Contacts", new String[]{"Leads", "Opportunities"});
        moduleMenuItems.put("Leads", new String[]{"Opportunities", "Contacts"});
        moduleMenuItems.put("Opportunities", new String[]{"Leads", "Opportunities"});
        
        pathForRelationship.put("Contacts", "contact");
        pathForRelationship.put("Leads", "lead");
        pathForRelationship.put("Opportunities", "opportunity");
    }

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createAccountsTable(db);
        createContactsTable(db);
        createLeadsTable(db);
        createOpportunitiesTable(db);

    }

    void dropAccountsTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNTS_TABLE_NAME);
    }

    void dropContactsTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
    }

    void dropLeadsTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + LEADS_TABLE_NAME);
    }

    void dropOpportunitiesTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + OPPORTUNITIES_TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                                        + ", which will destroy all old data");
        dropAccountsTable(db);
        dropContactsTable(db);
        dropLeadsTable(db);
        dropOpportunitiesTable(db);
        onCreate(db);
    }

    private static void createAccountsTable(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + ACCOUNTS_TABLE_NAME + " (" 
                                        + AccountsColumns.ID + " INTEGER PRIMARY KEY," 
                                        + AccountsColumns.BEAN_ID + " TEXT," 
                                        + AccountsColumns.NAME + " TEXT,"
                                        + AccountsColumns.EMAIL1 + " TEXT,"
                                        + AccountsColumns.PARENT_NAME + " TEXT,"
                                        + AccountsColumns.PHONE_OFFICE + " TEXT,"
                                        + AccountsColumns.PHONE_FAX + " TEXT,"
                                        + AccountsColumns.DELETED + " INTEGER," 
                                        + " UNIQUE(" + AccountsColumns.BEAN_ID + ")" + ");");
    }

    private static void createContactsTable(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + CONTACTS_TABLE_NAME + " (" 
                                        + ContactsColumns.ID + " INTEGER PRIMARY KEY," 
                                        + ContactsColumns.BEAN_ID + " TEXT," 
                                        + ContactsColumns.FIRST_NAME + " TEXT,"
                                        + ContactsColumns.LAST_NAME + " TEXT,"
                                        + ContactsColumns.ACCOUNT_NAME + " TEXT,"
                                        + ContactsColumns.PHONE_MOBILE + " TEXT,"
                                        + ContactsColumns.PHONE_WORK + " TEXT,"
                                        + ContactsColumns.EMAIL1 + " TEXT,"
                                        + ContactsColumns.CREATED_BY + " TEXT,"
                                        + ContactsColumns.MODIFIED_BY_NAME + " TEXT," 
                                        + ContactsColumns.DELETED + " INTEGER,"
                                        + ContactsColumns.ACCOUNT_ID + " INTEGER,"
                                        + " UNIQUE(" + ContactsColumns.BEAN_ID + ")" + ");");
    }

    private static void createLeadsTable(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + LEADS_TABLE_NAME + " (" 
                                        + LeadsColumns.ID + " INTEGER PRIMARY KEY," 
                                        + LeadsColumns.BEAN_ID + " TEXT,"
                                        + LeadsColumns.FIRST_NAME + " TEXT,"
                                        + LeadsColumns.LAST_NAME + " TEXT," 
                                        + LeadsColumns.EMAIL1 + " TEXT," 
                                        + LeadsColumns.PHONE_WORK + " TEXT,"
                                        + LeadsColumns.PHONE_FAX + " TEXT,"
                                        + LeadsColumns.DELETED + " INTEGER," 
                                        + LeadsColumns.ACCOUNT_ID + " INTEGER,"
                                        + " UNIQUE(" + LeadsColumns.BEAN_ID + ")" + ");");
    }

    private static void createOpportunitiesTable(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + OPPORTUNITIES_TABLE_NAME + " (" 
                                        + OpportunitiesColumns.ID + " INTEGER PRIMARY KEY," 
                                        + OpportunitiesColumns.BEAN_ID + " TEXT," 
                                        + OpportunitiesColumns.NAME + " TEXT,"
                                        + OpportunitiesColumns.ACCOUNT_NAME + " TEXT,"
                                        + OpportunitiesColumns.AMOUNT + " TEXT,"
                                        + OpportunitiesColumns.AMOUNT_USDOLLAR + " TEXT,"
                                        + OpportunitiesColumns.ASSIGNED_USER_ID + " TEXT,"
                                        + OpportunitiesColumns.ASSIGNED_USER_NAME + " TEXT,"
                                        + OpportunitiesColumns.CAMPAIGN_NAME + " TEXT,"
                                        + OpportunitiesColumns.CREATED_BY + " TEXT,"
                                        + OpportunitiesColumns.CREATED_BY_NAME + " TEXT,"
                                        + OpportunitiesColumns.CURRENCY_ID + " TEXT,"
                                        + OpportunitiesColumns.CURRENCY_NAME + " TEXT,"
                                        + OpportunitiesColumns.CURRENCY_SYMBOL + " TEXT,"
                                        + OpportunitiesColumns.DATE_CLOSED + " TEXT,"
                                        + OpportunitiesColumns.DATE_ENTERED + " TEXT,"
                                        + OpportunitiesColumns.DATE_MODIFIED + " TEXT,"
                                        + OpportunitiesColumns.DESCRIPTION + " TEXT,"
                                        + OpportunitiesColumns.LEAD_SOURCE + " TEXT,"
                                        + OpportunitiesColumns.MODIFIED_BY_NAME + " TEXT,"
                                        + OpportunitiesColumns.MODIFIED_USER_ID + " TEXT,"
                                        + OpportunitiesColumns.NEXT_STEP + " TEXT,"
                                        + OpportunitiesColumns.OPPORTUNITY_TYPE + " TEXT,"
                                        + OpportunitiesColumns.PROBABILITY + " TEXT,"
                                        + OpportunitiesColumns.SALES_STAGE + " TEXT,"
                                        + OpportunitiesColumns.DELETED + " INTEGER,"
                                        + OpportunitiesColumns.ACCOUNT_ID + " INTEGER,"
                                        + " UNIQUE(" + OpportunitiesColumns.BEAN_ID + ")" + ");");
    }

    public static String[] getModuleProjections(String moduleName) {
        return moduleProjections.get(moduleName);
    }

    public static String[] getModuleListSelections(String moduleName) {
        return moduleListSelections.get(moduleName);
    }

    public static String getModuleSortOrder(String moduleName) {
        return moduleSortOrder.get(moduleName);
    }

    public static Uri getModuleUri(String moduleName) {
        return moduleUris.get(moduleName);
    }

    public static ModuleField getModuleField(String moduleName, String fieldName) {
        return moduleFields.get(moduleName).get(fieldName);
    }

    public static String getModuleSelection(String moduleName, String searchString) {
        if (moduleName.equals("Accounts")) {
            return AccountsColumns.NAME + " LIKE '%" + searchString + "%'";
        } else if (moduleName.equals("Contacts")) {
            return "(" + ContactsColumns.FIRST_NAME + " LIKE '%" + searchString + "%' OR "
                                            + ContactsColumns.LAST_NAME + " LIKE '%" + searchString
                                            + "%'" + ")";
        }
        return "";
    }
    
    public static String[] getModuleMenuItems(String moduleName){
        return moduleMenuItems.get(moduleName);
    }

    public static String getPathForRelationship(String moduleName) {
        return pathForRelationship.get(moduleName);
    }

    public static List<String> getModuleList(){
        return  modulesList;
    }
}
