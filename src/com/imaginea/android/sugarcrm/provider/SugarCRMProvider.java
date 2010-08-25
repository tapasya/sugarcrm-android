package com.imaginea.android.sugarcrm.provider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Accounts;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AccountsCasesColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AccountsColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AccountsContactsColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AccountsOpportunitiesColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Calls;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Campaigns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Cases;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.CasesColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Contacts;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ContactsColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ContactsOpportunitiesColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Leads;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.LeadsColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Meetings;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Opportunities;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.OpportunitiesColumns;
import com.imaginea.android.sugarcrm.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * SugarCRMProvider Provides access to a database of sugar modules, their data and relationships.
 */
public class SugarCRMProvider extends ContentProvider {

    public static final String AUTHORITY = "com.imaginea.sugarcrm.provider";

    private static final int ACCOUNT = 0;

    private static final int ACCOUNT_ID = 1;

    private static final int CONTACT = 2;

    private static final int CONTACT_ID = 3;

    private static final int LEAD = 4;

    private static final int LEAD_ID = 5;

    private static final int OPPORTUNITY = 6;

    private static final int OPPORTUNITY_ID = 7;

    private static final int MEETING = 8;

    private static final int MEETING_ID = 9;

    private static final int CASE = 10;

    private static final int CASE_ID = 11;

    private static final int CALL = 12;

    private static final int CALL_ID = 13;

    private static final int CAMPAIGN = 14;

    private static final int CAMPAIGN_ID = 15;

    private static final int ACCOUNT_CONTACT = 16;

    private static final int ACCOUNT_LEAD = 17;

    private static final int ACCOUNT_OPPORTUNITY = 18;

    private static final int ACCOUNT_CASE = 19;

    private static final int CONTACT_LEAD = 20;

    // TODO - is this required
    private static final int CONTACT_OPPORTUNITY = 21;

    private static final int CONTACT_CASE = 22;

    private static final int LEAD_OPPORTUNITY = 23;

    private static final int OPPORTUNITY_CONTACT = 24;

    private static final int USERS = 25;

    private static final int SEARCH = 26;

    private static final UriMatcher sUriMatcher;

    private static final String TAG = SugarCRMProvider.class.getSimpleName();

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                                    String sortOrder) {
        Cursor c = null;
        String maxResultsLimit = null;
        String offset = null;

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        switch (sUriMatcher.match(uri)) {
        case SEARCH:
            String query = uri.getLastPathSegment().toLowerCase();
            // selection = ModuleFields.NAME + " LIKE '%" + query + "%'";
            selection = ModuleFields.NAME + " ='" + query + "'";
            c = db.query(DatabaseHelper.ACCOUNTS_TABLE_NAME, Accounts.SEARCH_PROJECTION, selection, selectionArgs, null, null, null);
            break;
        case ACCOUNT:
            c = db.query(DatabaseHelper.ACCOUNTS_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            break;

        case ACCOUNT_ID:

            // db.setProjectionMap(sNotesProjectionMap);
            selection = SugarCRMContent.RECORD_ID + " = ?";
            c = db.query(DatabaseHelper.ACCOUNTS_TABLE_NAME, projection, selection, new String[] { uri.getPathSegments().get(1) }, null, null, null);
            // qb.appendWhere(Notes._ID + "=" + uri.getPathSegments().get(1));
            break;

        case ACCOUNT_CONTACT:
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(DatabaseHelper.ACCOUNTS_TABLE_NAME + ","
                                            + DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME + ","
                                            + DatabaseHelper.CONTACTS_TABLE_NAME);

            selection = DatabaseHelper.ACCOUNTS_TABLE_NAME + "." + Accounts.ID + " = ?" + " AND "
                                            + DatabaseHelper.ACCOUNTS_TABLE_NAME + "."
                                            + Accounts.ID + "="
                                            + DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME + "."
                                            + AccountsContactsColumns.ACCOUNT_ID + " AND "
                                            + DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME + "."
                                            + AccountsContactsColumns.CONTACT_ID + "="
                                            + DatabaseHelper.CONTACTS_TABLE_NAME + "."
                                            + Contacts.ID + " AND "
                                            + DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME + "."
                                            + AccountsContactsColumns.DELETED + "=" + Util.NEW_ITEM;
            Map<String, String> contactsProjectionMap = getProjectionMap(DatabaseHelper.CONTACTS_TABLE_NAME, projection);
            qb.setProjectionMap(contactsProjectionMap);
            c = qb.query(db, projection, selection, new String[] { uri.getPathSegments().get(1) }, null, null, sortOrder, "");
            // c = db.query(DatabaseHelper.CONTACTS_TABLE_NAME, projection, selection, new String[]
            // { uri.getPathSegments().get(1) }, null, null, null);
            break;

        case ACCOUNT_LEAD:
            // TODO - whats the set relationship for this

            selection = LeadsColumns.ACCOUNT_ID + " = ?";
            c = db.query(DatabaseHelper.LEADS_TABLE_NAME, projection, selection, new String[] { uri.getPathSegments().get(1) }, null, null, null);
            break;

        case ACCOUNT_OPPORTUNITY:

            // c = db.query(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, projection, selection, new
            // String[] { uri.getPathSegments().get(1) }, null, null, null);

            qb = new SQLiteQueryBuilder();
            qb.setTables(DatabaseHelper.ACCOUNTS_TABLE_NAME + ","
                                            + DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME
                                            + "," + DatabaseHelper.OPPORTUNITIES_TABLE_NAME);

            selection = DatabaseHelper.ACCOUNTS_TABLE_NAME + "." + Accounts.ID + " = ?" + " AND "
                                            + DatabaseHelper.ACCOUNTS_TABLE_NAME + "."
                                            + Accounts.ID + "="
                                            + DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME
                                            + "." + AccountsOpportunitiesColumns.ACCOUNT_ID
                                            + " AND "
                                            + DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME
                                            + "." + AccountsOpportunitiesColumns.OPPORTUNITY_ID
                                            + "=" + DatabaseHelper.OPPORTUNITIES_TABLE_NAME + "."
                                            + Opportunities.ID;
            Map<String, String> opportunityProjectionMap = getProjectionMap(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, projection);
            qb.setProjectionMap(opportunityProjectionMap);

            sortOrder = DatabaseHelper.OPPORTUNITIES_TABLE_NAME + "." + OpportunitiesColumns.NAME
                                            + " ASC";
            c = qb.query(db, projection, selection, new String[] { uri.getPathSegments().get(1) }, null, null, sortOrder, "");

            break;

        case ACCOUNT_CASE:

            // c = db.query(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, projection, selection, new
            // String[] { uri.getPathSegments().get(1) }, null, null, null);

            qb = new SQLiteQueryBuilder();
            qb.setTables(DatabaseHelper.ACCOUNTS_TABLE_NAME + ","
                                            + DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME + ","
                                            + DatabaseHelper.CASES_TABLE_NAME);

            selection = DatabaseHelper.ACCOUNTS_TABLE_NAME + "." + Accounts.ID + " = ?" + " AND "
                                            + DatabaseHelper.ACCOUNTS_TABLE_NAME + "."
                                            + Accounts.ID + "="
                                            + DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME + "."
                                            + AccountsCasesColumns.ACCOUNT_ID + " AND "
                                            + DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME + "."
                                            + AccountsCasesColumns.CASE_ID + "="
                                            + DatabaseHelper.CASES_TABLE_NAME + "." + Cases.ID;
            Map<String, String> casesProjectionMap = getProjectionMap(DatabaseHelper.CASES_TABLE_NAME, projection);
            qb.setProjectionMap(casesProjectionMap);

            sortOrder = DatabaseHelper.CASES_TABLE_NAME + "." + CasesColumns.NAME + " ASC";
            c = qb.query(db, projection, selection, new String[] { uri.getPathSegments().get(1) }, null, null, sortOrder, "");

            break;

        case CONTACT:

            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Querying Contacts");
                Log.d(TAG, "Uri:->" + uri.toString());

                Log.d(TAG, "Offset" + offset);
                Log.d(TAG, "maxResultsLimit" + maxResultsLimit);
            }

            if (maxResultsLimit != null) {
                // maxResultsLimit = maxResultsLimit + "  OFFSET " + offset;
                if (selection == null) {
                    selection = SugarCRMContent.RECORD_ID + " > ?";
                    selectionArgs = new String[] { offset };
                }
            }
            c = db.query(DatabaseHelper.CONTACTS_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder, maxResultsLimit);

            break;

        case CONTACT_ID:

            selection = SugarCRMContent.RECORD_ID + " = ?";
            c = db.query(DatabaseHelper.CONTACTS_TABLE_NAME, projection, selection, new String[] { uri.getPathSegments().get(1) }, null, null, null);
            break;

        case CONTACT_LEAD:
            // TODO - this case is dubious - remove it later
            // Bug - contactId being used as accountId
            selection = LeadsColumns.ACCOUNT_ID + " = ?";
            c = db.query(DatabaseHelper.LEADS_TABLE_NAME, projection, selection, new String[] { uri.getPathSegments().get(1) }, null, null, null);
            break;

        case CONTACT_OPPORTUNITY:

            qb = new SQLiteQueryBuilder();
            qb.setTables(DatabaseHelper.CONTACTS_TABLE_NAME + ","
                                            + DatabaseHelper.CONTACTS_OPPORTUNITIES_TABLE_NAME
                                            + "," + DatabaseHelper.OPPORTUNITIES_TABLE_NAME);

            selection = DatabaseHelper.CONTACTS_TABLE_NAME + "." + Contacts.ID + " = ?" + " AND "
                                            + DatabaseHelper.CONTACTS_TABLE_NAME + "."
                                            + Contacts.ID + "="
                                            + DatabaseHelper.CONTACTS_OPPORTUNITIES_TABLE_NAME
                                            + "." + ContactsOpportunitiesColumns.CONTACT_ID
                                            + " AND "
                                            + DatabaseHelper.CONTACTS_OPPORTUNITIES_TABLE_NAME
                                            + "." + ContactsOpportunitiesColumns.OPPORTUNITY_ID
                                            + "=" + DatabaseHelper.OPPORTUNITIES_TABLE_NAME + "."
                                            + Opportunities.ID;
            opportunityProjectionMap = getProjectionMap(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, projection);
            qb.setProjectionMap(opportunityProjectionMap);
            c = qb.query(db, projection, selection, new String[] { uri.getPathSegments().get(1) }, null, null, sortOrder, "");

            break;

        case LEAD:
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Querying Leads");
                Log.d(TAG, "Uri:->" + uri.toString());
                Log.d(TAG, "Offset" + offset);
                Log.d(TAG, "maxResultsLimit" + maxResultsLimit);
            }
            if (maxResultsLimit != null) {
                // maxResultsLimit = maxResultsLimit + "  OFFSET " + offset;
                if (selection == null) {
                    selection = SugarCRMContent.RECORD_ID + " > ?";
                    selectionArgs = new String[] { offset };
                }
            }
            c = db.query(DatabaseHelper.LEADS_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder, maxResultsLimit);

            break;

        case LEAD_ID:
            // db.setProjectionMap(sNotesProjectionMap);
            selection = SugarCRMContent.RECORD_ID + " = ?";
            c = db.query(DatabaseHelper.LEADS_TABLE_NAME, projection, selection, new String[] { uri.getPathSegments().get(1) }, null, null, null);
            // qb.appendWhere(Notes._ID + "=" + uri.getPathSegments().get(1));
            break;

        case LEAD_OPPORTUNITY:
            // TODO - this case is dubious - remove it later
            // Bug - contactId being used as accountId
            selection = OpportunitiesColumns.ACCOUNT_ID + " = ?";
            c = db.query(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, projection, selection, new String[] { uri.getPathSegments().get(1) }, null, null, null);
            break;

        case OPPORTUNITY:

            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Querying OPPORTUNITIES");
                Log.d(TAG, "Uri:->" + uri.toString());

                // qb.setTables(DatabaseHelper.CONTACTS_TABLE_NAME);

                Log.d(TAG, "Offset" + offset);
                Log.d(TAG, "maxResultsLimit" + maxResultsLimit);
            }
            if (maxResultsLimit != null) {
                // maxResultsLimit = maxResultsLimit + "  OFFSET " + offset;
                if (selection == null) {
                    selection = SugarCRMContent.RECORD_ID + " > ?";
                    selectionArgs = new String[] { offset };
                }
            }
            c = db.query(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder, maxResultsLimit);

            break;

        case OPPORTUNITY_ID:

            selection = SugarCRMContent.RECORD_ID + " = ?";
            c = db.query(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, projection, selection, new String[] { uri.getPathSegments().get(1) }, null, null, null);
            break;

        case OPPORTUNITY_CONTACT:

            qb = new SQLiteQueryBuilder();
            qb.setTables(DatabaseHelper.CONTACTS_TABLE_NAME + ","
                                            + DatabaseHelper.CONTACTS_OPPORTUNITIES_TABLE_NAME
                                            + "," + DatabaseHelper.OPPORTUNITIES_TABLE_NAME);

            selection = DatabaseHelper.OPPORTUNITIES_TABLE_NAME + "." + Opportunities.ID + " = ?"
                                            + " AND " + DatabaseHelper.CONTACTS_TABLE_NAME + "."
                                            + Contacts.ID + "="
                                            + DatabaseHelper.CONTACTS_OPPORTUNITIES_TABLE_NAME
                                            + "." + ContactsOpportunitiesColumns.CONTACT_ID
                                            + " AND "
                                            + DatabaseHelper.CONTACTS_OPPORTUNITIES_TABLE_NAME
                                            + "." + ContactsOpportunitiesColumns.OPPORTUNITY_ID
                                            + "=" + DatabaseHelper.OPPORTUNITIES_TABLE_NAME + "."
                                            + Opportunities.ID;
            opportunityProjectionMap = getProjectionMap(DatabaseHelper.CONTACTS_TABLE_NAME, projection);
            qb.setProjectionMap(opportunityProjectionMap);

            sortOrder = DatabaseHelper.CONTACTS_TABLE_NAME + "." + ContactsColumns.FIRST_NAME
                                            + " ASC";
            c = qb.query(db, projection, selection, new String[] { uri.getPathSegments().get(1) }, null, null, sortOrder, "");

            break;

        case CASE:
            c = db.query(DatabaseHelper.CASES_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            break;

        case CASE_ID:
            selection = SugarCRMContent.RECORD_ID + " = ?";
            c = db.query(DatabaseHelper.CASES_TABLE_NAME, projection, selection, new String[] { uri.getPathSegments().get(1) }, null, null, null);
            break;

        case CALL:
            c = db.query(DatabaseHelper.CALLS_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            break;

        case CALL_ID:
            selection = SugarCRMContent.RECORD_ID + " = ?";
            c = db.query(DatabaseHelper.CALLS_TABLE_NAME, projection, selection, new String[] { uri.getPathSegments().get(1) }, null, null, null);
            break;

        case MEETING:
            c = db.query(DatabaseHelper.MEETINGS_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            break;

        case MEETING_ID:
            selection = SugarCRMContent.RECORD_ID + " = ?";
            c = db.query(DatabaseHelper.MEETINGS_TABLE_NAME, projection, selection, new String[] { uri.getPathSegments().get(1) }, null, null, null);
            break;

        case CAMPAIGN:
            c = db.query(DatabaseHelper.CAMPAIGNS_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            break;

        case CAMPAIGN_ID:
            selection = SugarCRMContent.RECORD_ID + " = ?";
            c = db.query(DatabaseHelper.CAMPAIGNS_TABLE_NAME, projection, selection, new String[] { uri.getPathSegments().get(1) }, null, null, null);
            break;

        case USERS:
            c = db.query(DatabaseHelper.USERS_TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (Log.isLoggable(TAG, Log.DEBUG))
            Log.d(TAG, "Count:" + c.getCount());
        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);

        // TODO - moce this code to sync and cache manager - database cache miss, start a rest api
        // call , package the params appropriately
        // if (c.getCount() == 0)
        // ServiceHelper.startService(getContext(), uri, module, projection, sortOrder);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        // TODO - add the remaining types
        case ACCOUNT:
            return "vnd.android.cursor.dir/accounts";
        case CONTACT:
            return "vnd.android.cursor.dir/contacts";

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        Long now = Long.valueOf(System.currentTimeMillis());
        // Make sure that the fields are all set

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        // Validate the requested uri
        // if (sUriMatcher.match(uri) != CONTACT) {
        // throw new IllegalArgumentException("Unknown URI " + uri);
        // }
        switch (sUriMatcher.match(uri)) {
        case ACCOUNT:
            long rowId = db.insert(DatabaseHelper.ACCOUNTS_TABLE_NAME, "", values);
            if (rowId > 0) {
                Uri accountUri = ContentUris.withAppendedId(Accounts.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(accountUri, null);
                return accountUri;
            }
            break;

        case ACCOUNT_CONTACT:
            String accountId = uri.getPathSegments().get(1);
            String selection = AccountsColumns.ID + "=" + accountId;

            Uri parentUri = mOpenHelper.getModuleUri(Util.ACCOUNTS);
            Cursor cursor = query(parentUri, Accounts.DETAILS_PROJECTION, selection, null, null);
            boolean rowsPresent = cursor.moveToFirst();
            if (Log.isLoggable(TAG, Log.VERBOSE))
                Log.v(TAG, "Uri to insert:" + uri.toString() + " rows present:" + rowsPresent);
            if (!rowsPresent) {
                cursor.close();
                return uri;
            }
            String accountName = cursor.getString(cursor.getColumnIndex(AccountsColumns.NAME));
            // values.put(Contacts.ACCOUNT_ID, accountId);
            values.put(Contacts.ACCOUNT_NAME, accountName);
            cursor.close();
            rowId = db.insert(DatabaseHelper.CONTACTS_TABLE_NAME, "", values);
            if (rowId > 0) {
                Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(contactUri, null);

                ContentValues val2 = new ContentValues();
                val2.put(AccountsContactsColumns.ACCOUNT_ID, accountId);
                val2.put(AccountsContactsColumns.CONTACT_ID, rowId);
                val2.put(AccountsContactsColumns.DELETED, Util.NEW_ITEM);
                // TODO - date_modified
                db.insert(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME, "", val2);

                return contactUri;
            }
            break;

        case ACCOUNT_LEAD:
            accountId = uri.getPathSegments().get(1);
            selection = AccountsColumns.ID + "=" + accountId;
            cursor = query(mOpenHelper.getModuleUri(Util.ACCOUNTS), Accounts.DETAILS_PROJECTION, selection, null, null);
            cursor.moveToFirst();
            accountName = cursor.getString(cursor.getColumnIndex(AccountsColumns.NAME));
            values.put(ModuleFields.ACCOUNT_ID, accountId);
            values.put(ModuleFields.ACCOUNT_NAME, accountName);

            rowId = db.insert(DatabaseHelper.LEADS_TABLE_NAME, "", values);
            if (rowId > 0) {
                Uri leadUri = ContentUris.withAppendedId(Leads.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(leadUri, null);
                cursor.close();
                return leadUri;
            }
            break;

        case ACCOUNT_OPPORTUNITY:
            accountId = uri.getPathSegments().get(1);
            selection = AccountsColumns.ID + "=" + accountId;

            cursor = query(mOpenHelper.getModuleUri(Util.ACCOUNTS), Accounts.DETAILS_PROJECTION, selection, null, null);
            cursor.moveToFirst();
            accountName = cursor.getString(cursor.getColumnIndex(AccountsColumns.NAME));
            // values.put(Contacts.ACCOUNT_ID, accountId);
            values.put(Contacts.ACCOUNT_NAME, accountName);
            cursor.close();
            rowId = db.insert(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, "", values);
            if (rowId > 0) {
                Uri opportunityUri = ContentUris.withAppendedId(Opportunities.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(opportunityUri, null);

                ContentValues val2 = new ContentValues();
                val2.put(AccountsOpportunitiesColumns.ACCOUNT_ID, accountId);
                val2.put(AccountsOpportunitiesColumns.OPPORTUNITY_ID, rowId);
                val2.put(AccountsContactsColumns.DELETED, Util.NEW_ITEM);
                // TODO - date_modified
                db.insert(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME, "", val2);

                return opportunityUri;
            }

            break;

        case ACCOUNT_CASE:
            accountId = uri.getPathSegments().get(1);
            selection = AccountsColumns.ID + "=" + accountId;

            // cursor = query(mOpenHelper.getModuleUri(Util.ACCOUNTS),
            // Accounts.DETAILS_PROJECTION, selection, null, null);
            // cursor.moveToFirst();
            // accountName = cursor.getString(cursor.getColumnIndex(AccountsColumns.NAME));
            // values.put(Contacts.ACCOUNT_ID, accountId);
            // values.put(Contacts.ACCOUNT_NAME, accountName);
            // cursor.close();
            rowId = db.insert(DatabaseHelper.CASES_TABLE_NAME, "", values);
            if (rowId > 0) {
                Uri caseUri = ContentUris.withAppendedId(Cases.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(caseUri, null);

                ContentValues val2 = new ContentValues();
                val2.put(AccountsCasesColumns.ACCOUNT_ID, accountId);
                val2.put(AccountsCasesColumns.CASE_ID, rowId);
                val2.put(AccountsContactsColumns.DELETED, Util.NEW_ITEM);
                // TODO - date_modified
                db.insert(DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME, "", val2);

                return caseUri;
            }

            break;

        case CONTACT:
            rowId = db.insert(DatabaseHelper.CONTACTS_TABLE_NAME, "", values);
            if (rowId > 0) {
                if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                    // get the account name from the name-value map
                    accountName = values.getAsString(ModuleFields.ACCOUNT_NAME);
                    if (!TextUtils.isEmpty(accountName)) {
                        // get the account id for the account name
                        selection = AccountsColumns.NAME + "='" + accountName + "'";
                        Cursor c = db.query(DatabaseHelper.ACCOUNTS_TABLE_NAME, Accounts.LIST_PROJECTION, selection, null, null, null, null);
                        c.moveToFirst();
                        String newAccountId = c.getString(0);
                        c.close();

                        // create a new relationship with the account
                        ContentValues val3 = new ContentValues();
                        val3.put(AccountsContactsColumns.ACCOUNT_ID, newAccountId);
                        val3.put(AccountsContactsColumns.CONTACT_ID, rowId);
                        val3.put(AccountsContactsColumns.DELETED, Util.NEW_ITEM);
                        long relationRowId = db.insert(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME, "", val3);
                        if (relationRowId > 0)
                            Log.i(TAG, "created relation: contactId - " + rowId + " accountId - "
                                                            + newAccountId);
                    }
                }

                Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(contactUri, null);
                return contactUri;
            }
            break;

        case CONTACT_LEAD:
            rowId = db.insert(DatabaseHelper.LEADS_TABLE_NAME, "", values);
            if (rowId > 0) {
                Uri leadUri = ContentUris.withAppendedId(Leads.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(leadUri, null);
                return leadUri;
            }
            break;

        case CONTACT_OPPORTUNITY:
            String contactId = uri.getPathSegments().get(1);
            selection = Contacts.ID + "=" + contactId;

            rowId = db.insert(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, "", values);
            if (rowId > 0) {
                Uri opportunityUri = ContentUris.withAppendedId(Opportunities.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(opportunityUri, null);

                ContentValues val2 = new ContentValues();
                val2.put(ContactsOpportunitiesColumns.CONTACT_ID, contactId);
                val2.put(ContactsOpportunitiesColumns.OPPORTUNITY_ID, rowId);
                val2.put(ContactsOpportunitiesColumns.DELETED, Util.NEW_ITEM);
                // TODO - date_modified
                long relationRowId = db.insert(DatabaseHelper.CONTACTS_OPPORTUNITIES_TABLE_NAME, "", val2);
                if (relationRowId > 0)
                    Log.i(TAG, "created relation: opportunityId - " + rowId + " contactId - "
                                                    + contactId);

                // accounts_opportunities relationship
                if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                    accountName = values.getAsString(ModuleFields.ACCOUNT_NAME);
                    if (!TextUtils.isEmpty(accountName)) {
                        // get the account id for the account name
                        selection = AccountsColumns.NAME + "='" + accountName + "'";
                        cursor = query(mOpenHelper.getModuleUri(Util.ACCOUNTS), Accounts.LIST_PROJECTION, selection, null, null);
                        String newAccountId = null;
                        if (cursor.moveToFirst())
                            newAccountId = cursor.getString(0);
                        cursor.close();

                        if (newAccountId != null) {
                            ContentValues val3 = new ContentValues();
                            val3.put(AccountsOpportunitiesColumns.ACCOUNT_ID, newAccountId);
                            val3.put(AccountsOpportunitiesColumns.OPPORTUNITY_ID, rowId);
                            val3.put(AccountsOpportunitiesColumns.DELETED, Util.NEW_ITEM);
                            // TODO - date_modified
                            relationRowId = db.insert(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME, "", val3);
                            if (relationRowId > 0)
                                Log.i(TAG, "created relation: opportunityId - " + rowId
                                                                + " accountId - " + newAccountId);
                        }
                    }
                }

                return opportunityUri;
            }

            break;

        case LEAD:
            accountName = values.getAsString(ModuleFields.ACCOUNT_NAME);
            if (!TextUtils.isEmpty(accountName)) {
                // get the account id for the account name
                selection = AccountsColumns.NAME + "='" + accountName + "'";
                Cursor c = db.query(DatabaseHelper.ACCOUNTS_TABLE_NAME, Accounts.LIST_PROJECTION, selection, null, null, null, null);
                String newAccountId = null;
                if (c.moveToFirst()) {
                    newAccountId = c.getString(0);
                    values.put(Leads.ACCOUNT_ID, newAccountId);
                }
                c.close();

            }

            rowId = db.insert(DatabaseHelper.LEADS_TABLE_NAME, "", values);
            if (rowId > 0) {
                Uri leadUri = ContentUris.withAppendedId(Leads.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(leadUri, null);
                return leadUri;
            }
            break;

        case LEAD_OPPORTUNITY:
            rowId = db.insert(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, "", values);
            if (rowId > 0) {
                Uri opportunityUri = ContentUris.withAppendedId(Leads.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(opportunityUri, null);
                return opportunityUri;
            }
            break;

        case OPPORTUNITY:
            rowId = db.insert(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, "", values);
            if (rowId > 0) {
                if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                    accountName = values.getAsString(ModuleFields.ACCOUNT_NAME);
                    if (!TextUtils.isEmpty(accountName)) {
                        // get the account id for the account name
                        selection = AccountsColumns.NAME + "='" + accountName + "'";
                        Cursor c = db.query(DatabaseHelper.ACCOUNTS_TABLE_NAME, Accounts.LIST_PROJECTION, selection, null, null, null, null);
                        String newAccountId = null;
                        if (c.moveToFirst()) {
                            newAccountId = c.getString(0);
                        }
                        c.close();

                        if (newAccountId != null) {
                            // create a new relationship with the new account
                            ContentValues val3 = new ContentValues();
                            val3.put(AccountsOpportunitiesColumns.ACCOUNT_ID, newAccountId);
                            val3.put(AccountsOpportunitiesColumns.OPPORTUNITY_ID, rowId);
                            val3.put(AccountsOpportunitiesColumns.DELETED, Util.NEW_ITEM);
                            long relationRowId = db.insert(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME, "", val3);
                            if (relationRowId > 0)
                                Log.i(TAG, "created relation: opportunityId - " + rowId
                                                                + " accountId - " + newAccountId);
                        }
                    }
                }

                Uri oppUri = ContentUris.withAppendedId(Opportunities.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(oppUri, null);
                return oppUri;
            }
            break;

        case OPPORTUNITY_CONTACT:
            String opportunityId = uri.getPathSegments().get(1);
            selection = Opportunities.ID + "=" + opportunityId;

            rowId = db.insert(DatabaseHelper.CONTACTS_TABLE_NAME, "", values);
            if (rowId > 0) {
                Uri contactsUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(contactsUri, null);

                ContentValues val2 = new ContentValues();
                val2.put(ContactsOpportunitiesColumns.CONTACT_ID, rowId);
                val2.put(ContactsOpportunitiesColumns.OPPORTUNITY_ID, opportunityId);
                val2.put(AccountsOpportunitiesColumns.DELETED, Util.NEW_ITEM);
                // TODO - date_modified
                db.insert(DatabaseHelper.CONTACTS_OPPORTUNITIES_TABLE_NAME, "", val2);

                // accounts_contacts relationship
                if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                    accountName = values.getAsString(ModuleFields.ACCOUNT_NAME);
                    if (!TextUtils.isEmpty(accountName)) {
                        // get the account id for the account name
                        selection = AccountsColumns.NAME + "='" + accountName + "'";
                        cursor = query(mOpenHelper.getModuleUri(Util.ACCOUNTS), Accounts.LIST_PROJECTION, selection, null, null);
                        String newAccountId = null;
                        if (cursor.moveToFirst()) {
                            newAccountId = cursor.getString(0);
                        }
                        cursor.close();

                        if (newAccountId != null) {
                            ContentValues val3 = new ContentValues();
                            val3.put(AccountsContactsColumns.ACCOUNT_ID, newAccountId);
                            val3.put(AccountsContactsColumns.CONTACT_ID, rowId);
                            val3.put(AccountsContactsColumns.DELETED, Util.NEW_ITEM);
                            // TODO - date_modified
                            long relationRowId = db.insert(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME, "", val3);
                            if (relationRowId > 0)
                                Log.i(TAG, "created relation: contactId - " + rowId
                                                                + " accountId - " + newAccountId);
                        }
                    }
                }

                return contactsUri;
            }

            break;

        case CASE:
            rowId = db.insert(DatabaseHelper.CASES_TABLE_NAME, "", values);
            if (rowId > 0) {
                if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                    accountName = values.getAsString(ModuleFields.ACCOUNT_NAME);
                    if (!TextUtils.isEmpty(accountName)) {
                        // get the account id for the account name
                        selection = AccountsColumns.NAME + "='" + accountName + "'";
                        Cursor c = db.query(DatabaseHelper.ACCOUNTS_TABLE_NAME, Accounts.LIST_PROJECTION, selection, null, null, null, null);
                        String newAccountId = null;
                        if (c.moveToFirst()) {
                            newAccountId = c.getString(0);
                        }
                        c.close();

                        if (newAccountId != null) {
                            // create a new relationship with the new account
                            ContentValues val3 = new ContentValues();
                            val3.put(AccountsCasesColumns.ACCOUNT_ID, newAccountId);
                            val3.put(AccountsCasesColumns.CASE_ID, rowId);
                            val3.put(AccountsCasesColumns.DELETED, Util.NEW_ITEM);
                            long relationRowId = db.insert(DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME, "", val3);
                            if (relationRowId > 0)
                                Log.i(TAG, "created relation: caseId - " + rowId + " accountId - "
                                                                + newAccountId);
                        }
                    }
                }
                Uri caseUri = ContentUris.withAppendedId(Cases.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(caseUri, null);
                return caseUri;
            }
            break;

        case CALL:
            rowId = db.insert(DatabaseHelper.CALLS_TABLE_NAME, "", values);
            if (rowId > 0) {
                Uri callUri = ContentUris.withAppendedId(Calls.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(callUri, null);
                return callUri;
            }
            break;

        case MEETING:
            rowId = db.insert(DatabaseHelper.MEETINGS_TABLE_NAME, "", values);
            if (rowId > 0) {
                Uri meetingUri = ContentUris.withAppendedId(Meetings.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(meetingUri, null);
                return meetingUri;
            }
            break;

        case CAMPAIGN:
            rowId = db.insert(DatabaseHelper.CAMPAIGNS_TABLE_NAME, "", values);
            if (rowId > 0) {
                Uri campaignUri = ContentUris.withAppendedId(Campaigns.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(campaignUri, null);
                return campaignUri;
            }
            break;

        default:
            // return uri;
            throw new IllegalArgumentException("Unknown URI " + uri);

        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;
        switch (sUriMatcher.match(uri)) {
        case ACCOUNT:
            count = db.delete(DatabaseHelper.ACCOUNTS_TABLE_NAME, where, whereArgs);
            break;

        case ACCOUNT_ID:
            String accountId = uri.getPathSegments().get(1);
            count = db.delete(DatabaseHelper.ACCOUNTS_TABLE_NAME, Accounts.ID
                                            + "="
                                            + accountId
                                            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                                                            : ""), whereArgs);
            break;

        case ACCOUNT_CONTACT:
            accountId = uri.getPathSegments().get(1);
            String contactId = uri.getPathSegments().get(3);
            count = db.delete(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME, AccountsContactsColumns.ACCOUNT_ID
                                            + "="
                                            + accountId
                                            + " AND "
                                            + AccountsContactsColumns.CONTACT_ID
                                            + "="
                                            + contactId
                                            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                                                            : ""), whereArgs);
            break;

        case ACCOUNT_LEAD:
            accountId = uri.getPathSegments().get(1);
            count = db.delete(DatabaseHelper.LEADS_TABLE_NAME, Accounts.ID
                                            + "="
                                            + accountId
                                            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                                                            : ""), whereArgs);
            break;

        case ACCOUNT_OPPORTUNITY:
            accountId = uri.getPathSegments().get(1);
            String opportunityId = uri.getPathSegments().get(3);
            count = db.delete(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME, AccountsOpportunitiesColumns.ACCOUNT_ID
                                            + "="
                                            + accountId
                                            + " AND "
                                            + AccountsOpportunitiesColumns.OPPORTUNITY_ID
                                            + "="
                                            + opportunityId
                                            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                                                            : ""), whereArgs);
            break;

        case CONTACT:
            count = db.delete(DatabaseHelper.CONTACTS_TABLE_NAME, where, whereArgs);
            break;

        case CONTACT_ID:
            contactId = uri.getPathSegments().get(1);
            count = db.delete(DatabaseHelper.CONTACTS_TABLE_NAME, Contacts.ID
                                            + "="
                                            + contactId
                                            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                                                            : ""), whereArgs);
            // delete all relationships
            String[] tableNames = mOpenHelper.getRelationshipTables(Util.CONTACTS);
            String whereClause = ModuleFields.CONTACT_ID + "=" + contactId;
            for (String tableName : tableNames) {
                db.delete(tableName, whereClause, null);
            }
            break;

        case LEAD:
            count = db.delete(DatabaseHelper.LEADS_TABLE_NAME, where, whereArgs);
            break;

        case LEAD_ID:
            String leadId = uri.getPathSegments().get(1);
            count = db.delete(DatabaseHelper.LEADS_TABLE_NAME, Leads.ID
                                            + "="
                                            + leadId
                                            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                                                            : ""), whereArgs);
            break;
        case OPPORTUNITY:
            count = db.delete(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, where, whereArgs);
            break;

        case OPPORTUNITY_ID:
            String oppId = uri.getPathSegments().get(1);
            count = db.delete(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, Opportunities.ID
                                            + "="
                                            + oppId
                                            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                                                            : ""), whereArgs);
            // delete all relationships
            tableNames = mOpenHelper.getRelationshipTables(Util.CONTACTS);
            whereClause = ModuleFields.OPPORTUNITY_ID + "=" + oppId;
            for (String tableName : tableNames) {
                db.delete(tableName, whereClause, null);
            }
            break;

        case CASE:
            count = db.delete(DatabaseHelper.CASES_TABLE_NAME, where, whereArgs);
            break;

        case CASE_ID:
            String caseId = uri.getPathSegments().get(1);
            count = db.delete(DatabaseHelper.CASES_TABLE_NAME, Opportunities.ID
                                            + "="
                                            + caseId
                                            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                                                            : ""), whereArgs);
            // delete all relationships
            tableNames = mOpenHelper.getRelationshipTables(Util.CONTACTS);
            whereClause = ModuleFields.CONTACT_ID + "=" + caseId;
            for (String tableName : tableNames) {
                db.delete(tableName, whereClause, null);
            }
            break;

        case CALL:
            count = db.delete(DatabaseHelper.CALLS_TABLE_NAME, where, whereArgs);
            break;

        case CALL_ID:
            String callId = uri.getPathSegments().get(1);
            count = db.delete(DatabaseHelper.CALLS_TABLE_NAME, Calls.ID
                                            + "="
                                            + callId
                                            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                                                            : ""), whereArgs);
            break;

        case MEETING:
            count = db.delete(DatabaseHelper.MEETINGS_TABLE_NAME, where, whereArgs);
            break;

        case MEETING_ID:
            String meetingId = uri.getPathSegments().get(1);
            count = db.delete(DatabaseHelper.MEETINGS_TABLE_NAME, Meetings.ID
                                            + "="
                                            + meetingId
                                            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                                                            : ""), whereArgs);
            break;

        case CAMPAIGN:
            count = db.delete(DatabaseHelper.CAMPAIGNS_TABLE_NAME, where, whereArgs);
            break;

        case CAMPAIGN_ID:
            String campaignId = uri.getPathSegments().get(1);
            count = db.delete(DatabaseHelper.CAMPAIGNS_TABLE_NAME, Campaigns.ID
                                            + "="
                                            + campaignId
                                            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                                                            : ""), whereArgs);
            break;
            
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case ACCOUNT:
            count = db.update(DatabaseHelper.ACCOUNTS_TABLE_NAME, values, where, whereArgs);
            break;

        case ACCOUNT_ID:
            String accountId = uri.getPathSegments().get(1);
            count = db.update(DatabaseHelper.ACCOUNTS_TABLE_NAME, values, Accounts.ID
                                            + "="
                                            + accountId
                                            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                                                            : ""), whereArgs);
            break;

        case ACCOUNT_CONTACT:
            accountId = uri.getPathSegments().get(1);
            String contactId = uri.getPathSegments().get(3);

            // update the contact
            String selection = ContactsColumns.ID + "=" + contactId;
            count = db.update(DatabaseHelper.CONTACTS_TABLE_NAME, values, selection, null);

            if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                String accountName = values.getAsString(ModuleFields.ACCOUNT_NAME);

                if (!TextUtils.isEmpty(accountName)) {
                    // get the account id for the account name
                    selection = AccountsColumns.NAME + "='" + accountName + "'";
                    Cursor cursor = query(mOpenHelper.getModuleUri(Util.ACCOUNTS), Accounts.LIST_PROJECTION, selection, null, null);
                    String newAccountId = null;
                    if (cursor.moveToFirst())
                        newAccountId = cursor.getString(0);
                    cursor.close();

                    if (newAccountId != null) {
                        if (!accountId.equals(newAccountId)) {
                            // the row Id of the new account is not the same as the old one

                            // update the delete flag to '1' for the old relationship
                            ContentValues val2 = new ContentValues();
                            selection = AccountsContactsColumns.ACCOUNT_ID + "=" + accountId
                                                            + " AND "
                                                            + AccountsContactsColumns.CONTACT_ID
                                                            + "=" + contactId;
                            val2.put(AccountsContactsColumns.DELETED, Util.DELETED_ITEM);
                            db.update(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME, val2, selection, null);
                            Log.i(TAG, "updated deleted flag for the relation: contactId - "
                                                            + contactId + " oldAccountId - "
                                                            + accountId);

                            // create a new relationship with the new account
                            ContentValues val3 = new ContentValues();
                            val3.put(AccountsContactsColumns.ACCOUNT_ID, newAccountId);
                            val3.put(AccountsContactsColumns.CONTACT_ID, contactId);
                            val3.put(AccountsContactsColumns.DELETED, Util.NEW_ITEM);
                            long rowId = db.insert(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME, "", val3);
                            if (rowId > 0)
                                Log.i(TAG, "created relation: contactId - " + contactId
                                                                + " accountId - " + newAccountId);
                        }
                    }

                } else {
                    // if the accountName is removed while updating, delete the relationship

                    // update the delete flag to '1' for the old relationship
                    ContentValues val2 = new ContentValues();
                    selection = AccountsContactsColumns.ACCOUNT_ID + "=" + accountId + " AND "
                                                    + AccountsContactsColumns.CONTACT_ID + "="
                                                    + contactId;
                    val2.put(AccountsContactsColumns.DELETED, Util.DELETED_ITEM);
                    db.update(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME, val2, selection, null);
                    Log.i(TAG, "updated deleted flag for the relation: contactId - " + contactId
                                                    + " oldAccountId - " + accountId);
                }
            }

            break;

        case ACCOUNT_LEAD:
            accountId = uri.getPathSegments().get(1);
            // TOOD: not handling this case as of now
            count = db.update(DatabaseHelper.LEADS_TABLE_NAME, values, Accounts.ID
                                            + "="
                                            + accountId
                                            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                                                            : ""), whereArgs);
            break;

        case ACCOUNT_OPPORTUNITY:
            accountId = uri.getPathSegments().get(1);
            String opportunityId = uri.getPathSegments().get(3);

            // update the opportunity
            selection = OpportunitiesColumns.ID + "=" + opportunityId;
            count = db.update(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, values, selection, null);

            if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                String accountName = values.getAsString(ModuleFields.ACCOUNT_NAME);

                if (!TextUtils.isEmpty(accountName)) {
                    // get the account id for the account name
                    selection = AccountsColumns.NAME + "='" + accountName + "'";
                    Cursor cursor = query(mOpenHelper.getModuleUri(Util.ACCOUNTS), Accounts.LIST_PROJECTION, selection, null, null);
                    String newAccountId = null;
                    if (cursor.moveToFirst())
                        newAccountId = cursor.getString(0);
                    cursor.close();

                    if (newAccountId != null) {
                        if (!accountId.equals(newAccountId)) {
                            // the row Id of the new account is not the same as the old one

                            // update the delete flag to '1' for the old relationship
                            ContentValues val2 = new ContentValues();
                            selection = AccountsOpportunitiesColumns.ACCOUNT_ID
                                                            + "="
                                                            + accountId
                                                            + " AND "
                                                            + AccountsOpportunitiesColumns.OPPORTUNITY_ID
                                                            + "=" + opportunityId;
                            val2.put(AccountsOpportunitiesColumns.DELETED, Util.DELETED_ITEM);
                            db.update(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME, val2, selection, null);
                            Log.i(TAG, "updated deleted flag for the relation: opportunityId - "
                                                            + opportunityId + " oldAccountId - "
                                                            + accountId);

                            // create a new relationship with the new account
                            ContentValues val3 = new ContentValues();
                            val3.put(AccountsOpportunitiesColumns.ACCOUNT_ID, newAccountId);
                            val3.put(AccountsOpportunitiesColumns.OPPORTUNITY_ID, opportunityId);
                            val3.put(AccountsOpportunitiesColumns.DELETED, Util.NEW_ITEM);
                            long rowId = db.insert(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME, "", val3);
                            if (rowId > 0)
                                Log.i(TAG, "created relation: opportunityId - " + opportunityId
                                                                + " accountId - " + newAccountId);
                        }
                    }

                } else {
                    // if the accountName is removed while updating, delete the relationship

                    // update the delete flag to '1' for the old relationship
                    ContentValues val2 = new ContentValues();
                    selection = AccountsOpportunitiesColumns.ACCOUNT_ID + "=" + accountId + " AND "
                                                    + AccountsOpportunitiesColumns.OPPORTUNITY_ID
                                                    + "=" + opportunityId;
                    val2.put(AccountsOpportunitiesColumns.DELETED, Util.DELETED_ITEM);
                    db.update(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME, val2, selection, null);
                    Log.i(TAG, "updated deleted flag for the relation: opportunityId - "
                                                    + opportunityId + " oldAccountId - "
                                                    + accountId);
                }
            }

            break;

        case ACCOUNT_CASE:
            accountId = uri.getPathSegments().get(1);
            String caseId = uri.getPathSegments().get(3);

            // update the case
            selection = CasesColumns.ID + "=" + caseId;
            count = db.update(DatabaseHelper.CASES_TABLE_NAME, values, selection, null);

            if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                String accountName = values.getAsString(ModuleFields.ACCOUNT_NAME);

                if (!TextUtils.isEmpty(accountName)) {
                    // get the account id for the account name
                    selection = AccountsColumns.NAME + "='" + accountName + "'";
                    Cursor cursor = query(mOpenHelper.getModuleUri(Util.ACCOUNTS), Accounts.LIST_PROJECTION, selection, null, null);
                    String newAccountId = null;
                    if (cursor.moveToFirst())
                        newAccountId = cursor.getString(0);
                    cursor.close();

                    if (newAccountId != null) {
                        if (!accountId.equals(newAccountId)) {
                            // the row Id of the new account is not the same as the old one

                            // update the delete flag to '1' for the old relationship
                            ContentValues val2 = new ContentValues();
                            selection = AccountsCasesColumns.ACCOUNT_ID + "=" + accountId + " AND "
                                                            + AccountsCasesColumns.CASE_ID + "="
                                                            + caseId;
                            val2.put(AccountsCasesColumns.DELETED, Util.DELETED_ITEM);
                            db.update(DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME, val2, selection, null);
                            Log.i(TAG, "updated deleted flag for the relation: caseId - " + caseId
                                                            + " oldAccountId - " + accountId);

                            // create a new relationship with the new account
                            ContentValues val3 = new ContentValues();
                            val3.put(AccountsCasesColumns.ACCOUNT_ID, newAccountId);
                            val3.put(AccountsCasesColumns.CASE_ID, caseId);
                            val3.put(AccountsCasesColumns.DELETED, Util.NEW_ITEM);
                            long rowId = db.insert(DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME, "", val3);
                            if (rowId > 0)
                                Log.i(TAG, "created relation: caseId - " + caseId + " accountId - "
                                                                + newAccountId);
                        }
                    }
                } else {
                    // if the accountName is removed while updating, delete the relationship

                    // update the delete flag to '1' for the old relationship
                    ContentValues val2 = new ContentValues();
                    selection = AccountsCasesColumns.ACCOUNT_ID + "=" + accountId + " AND "
                                                    + AccountsCasesColumns.CASE_ID + "=" + caseId;
                    val2.put(AccountsCasesColumns.DELETED, Util.DELETED_ITEM);
                    db.update(DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME, val2, selection, null);
                    Log.i(TAG, "updated deleted flag for the relation: caseId - " + caseId
                                                    + " oldAccountId - " + accountId);
                }
            }

            break;

        case CONTACT:
            count = db.update(DatabaseHelper.CONTACTS_TABLE_NAME, values, where, whereArgs);
            break;

        case CONTACT_ID:
            contactId = uri.getPathSegments().get(1);
            count = db.update(DatabaseHelper.CONTACTS_TABLE_NAME, values, Contacts.ID
                                            + "="
                                            + contactId
                                            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                                                            : ""), whereArgs);

            String deleted = values.getAsString(ModuleFields.DELETED);
            if (!TextUtils.isEmpty(deleted) && deleted.equals(Util.DELETED_ITEM)) {
                // update the delete flag of all relationships
                String[] tableNames = mOpenHelper.getRelationshipTables(Util.CONTACTS);
                String whereClause = ModuleFields.CONTACT_ID + "=" + contactId;
                for (String tableName : tableNames) {
                    db.update(tableName, values, whereClause, null);
                }
            } else {
                if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                    String accountName = (String) values.get(ModuleFields.ACCOUNT_NAME);
                    if (!TextUtils.isEmpty(accountName)) {
                        // get the account id for the account name
                        selection = AccountsColumns.NAME + "='" + accountName + "'";
                        Cursor cursor = query(mOpenHelper.getModuleUri(Util.ACCOUNTS), Accounts.LIST_PROJECTION, selection, null, null);
                        String newAccountId = null;
                        if (cursor.moveToFirst())
                            newAccountId = cursor.getString(0);
                        cursor.close();

                        // get the accountId to which this contact is related to
                        selection = AccountsContactsColumns.CONTACT_ID + "=" + contactId;
                        cursor = db.query(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME, new String[] { AccountsContactsColumns.ACCOUNT_ID }, selection, null, null, null, null);
                        accountId = null;
                        if (cursor.moveToFirst())
                            accountId = cursor.getString(0);
                        cursor.close();

                        if (newAccountId != null && accountId != null) {
                            if (!newAccountId.equals(accountId)) {

                                // the row Id of the new account is not the same as the old one

                                // update the delete flag to '1' for the old relationship
                                ContentValues val2 = new ContentValues();
                                selection = AccountsContactsColumns.ACCOUNT_ID
                                                                + "="
                                                                + accountId
                                                                + " AND "
                                                                + AccountsContactsColumns.CONTACT_ID
                                                                + "=" + contactId;
                                val2.put(AccountsContactsColumns.DELETED, Util.DELETED_ITEM);
                                db.update(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME, val2, selection, null);
                                Log.i(TAG, "updated deleted flag for the relation: contactId - "
                                                                + contactId + " oldAccountId - "
                                                                + accountId);

                                // create a new relationship with the new account
                                ContentValues val3 = new ContentValues();
                                val3.put(AccountsContactsColumns.ACCOUNT_ID, newAccountId);
                                val3.put(AccountsContactsColumns.CONTACT_ID, contactId);
                                val3.put(AccountsContactsColumns.DELETED, Util.NEW_ITEM);
                                long rowId = db.insert(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME, "", val3);
                                if (rowId > 0)
                                    Log.i(TAG, "created relation: contactId - " + contactId
                                                                    + " accountId - "
                                                                    + newAccountId);
                            }
                        }

                    } else {
                        // delete the relationship if there exists one

                        ContentValues relationValues = new ContentValues();
                        selection = AccountsContactsColumns.CONTACT_ID + "=" + contactId;
                        relationValues.put(AccountsContactsColumns.DELETED, Util.DELETED_ITEM);
                        db.update(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME, relationValues, selection, null);
                    }
                }
            }

            break;

        case CONTACT_OPPORTUNITY:
            contactId = uri.getPathSegments().get(1);
            opportunityId = uri.getPathSegments().get(3);

            // update the opportunity
            selection = OpportunitiesColumns.ID + "=" + opportunityId;
            count = db.update(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, values, selection, null);

            // update the relationship
            ContentValues relationValues = new ContentValues();
            relationValues.put(ContactsOpportunitiesColumns.CONTACT_ID, contactId);
            relationValues.put(ContactsOpportunitiesColumns.OPPORTUNITY_ID, opportunityId);
            relationValues.put(ContactsOpportunitiesColumns.DELETED, Util.NEW_ITEM);
            long rowId = db.insert(DatabaseHelper.CONTACTS_OPPORTUNITIES_TABLE_NAME, "", relationValues);
            if (rowId > 0)
                Log.i(TAG, "created relation: opportunityId - " + opportunityId + " contactId - "
                                                + contactId);

            if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                String accountName = values.getAsString(ModuleFields.ACCOUNT_NAME);

                if (!TextUtils.isEmpty(accountName)) {

                    // get the account id for the account name
                    selection = AccountsColumns.NAME + "='" + accountName + "'";
                    Cursor cursor = query(mOpenHelper.getModuleUri(Util.ACCOUNTS), Accounts.LIST_PROJECTION, selection, null, null);
                    String newAccountId = null;
                    if (cursor.moveToFirst())
                        newAccountId = cursor.getString(0);
                    cursor.close();

                    // get the accountId to which this opportunity is related to
                    selection = AccountsOpportunitiesColumns.OPPORTUNITY_ID + "=" + opportunityId;
                    cursor = db.query(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME, new String[] { AccountsOpportunitiesColumns.ACCOUNT_ID }, selection, null, null, null, null);
                    accountId = null;
                    if (cursor.moveToFirst())
                        accountId = cursor.getString(0);
                    cursor.close();

                    if (newAccountId != null && accountId != null) {
                        if (!newAccountId.equals(accountId)) {
                            // the row Id of the new account is not the same as the old one

                            // update the delete flag to '1' for the old relationship
                            ContentValues val2 = new ContentValues();
                            selection = AccountsOpportunitiesColumns.ACCOUNT_ID
                                                            + "="
                                                            + accountId
                                                            + " AND "
                                                            + AccountsOpportunitiesColumns.OPPORTUNITY_ID
                                                            + "=" + opportunityId;
                            val2.put(AccountsOpportunitiesColumns.DELETED, Util.DELETED_ITEM);
                            db.update(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME, val2, selection, null);
                            Log.i(TAG, "updated deleted flag for the relation: opportunityId - "
                                                            + opportunityId + " oldAccountId - "
                                                            + accountId);

                            // create a new relationship with the new account
                            ContentValues val3 = new ContentValues();
                            val3.put(AccountsOpportunitiesColumns.ACCOUNT_ID, newAccountId);
                            val3.put(AccountsOpportunitiesColumns.OPPORTUNITY_ID, opportunityId);
                            val3.put(AccountsOpportunitiesColumns.DELETED, Util.NEW_ITEM);
                            rowId = db.insert(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME, "", val3);
                            if (rowId > 0)
                                Log.i(TAG, "created relation: opportunityId - " + opportunityId
                                                                + " accountId - " + newAccountId);
                        }
                    }

                } else {
                    // if the accountName is removed while updating, delete the relationship

                    // update the delete flag to '1' for the old relationship
                    ContentValues val2 = new ContentValues();
                    selection = AccountsOpportunitiesColumns.OPPORTUNITY_ID + "=" + opportunityId;
                    val2.put(AccountsOpportunitiesColumns.DELETED, Util.DELETED_ITEM);
                    db.update(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME, val2, selection, null);
                    Log.i(TAG, "updated deleted flag for the relations in CONTACT_OPPORTUNITIES table with opportunityId - "
                                                    + opportunityId);

                }
            }

            break;

        case LEAD:
            count = db.update(DatabaseHelper.LEADS_TABLE_NAME, values, where, whereArgs);
            break;

        case LEAD_ID:
            String leadId = uri.getPathSegments().get(1);

            if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                String accountName = values.getAsString(ModuleFields.ACCOUNT_NAME);

                if (!TextUtils.isEmpty(accountName)) {

                    // get the account id for the account name
                    selection = AccountsColumns.NAME + "='" + accountName + "'";
                    Cursor cursor = query(mOpenHelper.getModuleUri(Util.ACCOUNTS), Accounts.LIST_PROJECTION, selection, null, null);
                    String newAccountId = null;
                    if (cursor.moveToFirst())
                        newAccountId = cursor.getString(0);
                    cursor.close();

                    // get the accountId to which this lead is related to
                    selection = Leads.ID + "=" + leadId;
                    cursor = db.query(DatabaseHelper.LEADS_TABLE_NAME, new String[] { Leads.ACCOUNT_ID }, selection, null, null, null, null);
                    accountId = null;
                    if (cursor.moveToFirst())
                        accountId = cursor.getString(0);
                    cursor.close();

                    if (newAccountId != null && accountId != null) {
                        if (!newAccountId.equals(accountId)) {
                            // the row Id of the new account is not the same as the old one

                            // update the field 'account_id'
                            values.put(ModuleFields.ACCOUNT_ID, newAccountId);
                        }
                    }

                }
            }

            count = db.update(DatabaseHelper.LEADS_TABLE_NAME, values, Leads.ID
                                            + "="
                                            + leadId
                                            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                                                            : ""), whereArgs);

            break;

        case OPPORTUNITY:
            count = db.update(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, values, where, whereArgs);
            break;

        case OPPORTUNITY_ID:
            opportunityId = uri.getPathSegments().get(1);
            count = db.update(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, values, Opportunities.ID
                                            + "="
                                            + opportunityId
                                            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                                                            : ""), whereArgs);

            deleted = values.getAsString(ModuleFields.DELETED);
            if (!TextUtils.isEmpty(deleted) && deleted.equals(Util.DELETED_ITEM)) {
                // update the delete flag of all relationships
                String[] tableNames = mOpenHelper.getRelationshipTables(Util.OPPORTUNITIES);
                String whereClause = ModuleFields.OPPORTUNITY_ID + "=" + opportunityId;
                for (String tableName : tableNames) {
                    db.update(tableName, values, whereClause, null);
                }
            } else {
                if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                    String accountName = (String) values.get(ModuleFields.ACCOUNT_NAME);
                    if (!TextUtils.isEmpty(accountName)) {
                        // get the account id for the account name
                        selection = AccountsColumns.NAME + "='" + accountName + "'";
                        Cursor cursor = query(mOpenHelper.getModuleUri(Util.ACCOUNTS), Accounts.LIST_PROJECTION, selection, null, null);
                        String newAccountId = null;
                        if (cursor.moveToFirst())
                            newAccountId = cursor.getString(0);
                        cursor.close();

                        // get the accountId to which this opportunity is related to
                        selection = AccountsOpportunitiesColumns.OPPORTUNITY_ID + "="
                                                        + opportunityId;
                        cursor = db.query(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME, new String[] { AccountsOpportunitiesColumns.ACCOUNT_ID }, selection, null, null, null, null);
                        accountId = null;
                        if (cursor.moveToFirst())
                            accountId = cursor.getString(0);
                        cursor.close();

                        if (newAccountId != null && accountId != null) {
                            if (!newAccountId.equals(accountId)) {
                                // the row Id of the new account is not the same as the old one

                                // update the delete flag to '1' for the old relationship
                                ContentValues val2 = new ContentValues();
                                selection = AccountsOpportunitiesColumns.ACCOUNT_ID
                                                                + "="
                                                                + accountId
                                                                + " AND "
                                                                + AccountsOpportunitiesColumns.OPPORTUNITY_ID
                                                                + "=" + opportunityId;
                                val2.put(AccountsOpportunitiesColumns.DELETED, Util.DELETED_ITEM);
                                db.update(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME, val2, selection, null);
                                Log.i(TAG, "updated deleted flag for the relation: opportunityId - "
                                                                + opportunityId
                                                                + " oldAccountId - " + accountId);

                                // create a new relationship with the new account
                                ContentValues val3 = new ContentValues();
                                val3.put(AccountsOpportunitiesColumns.ACCOUNT_ID, newAccountId);
                                val3.put(AccountsOpportunitiesColumns.OPPORTUNITY_ID, opportunityId);
                                val3.put(AccountsOpportunitiesColumns.DELETED, Util.NEW_ITEM);
                                rowId = db.insert(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME, "", val3);
                                if (rowId > 0)
                                    Log.i(TAG, "created relation: opportunityId - " + opportunityId
                                                                    + " accountId - "
                                                                    + newAccountId);
                            }
                        }

                    } else {
                        // delete the relationship if there exists one

                        relationValues = new ContentValues();
                        selection = AccountsOpportunitiesColumns.OPPORTUNITY_ID + "="
                                                        + opportunityId;
                        relationValues.put(AccountsOpportunitiesColumns.DELETED, Util.DELETED_ITEM);
                        db.update(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME, relationValues, selection, null);
                    }
                }
            }

            break;

        case OPPORTUNITY_CONTACT:
            opportunityId = uri.getPathSegments().get(1);
            contactId = uri.getPathSegments().get(3);

            // update the contact
            selection = ContactsColumns.ID + "=" + contactId;
            count = db.update(DatabaseHelper.CONTACTS_TABLE_NAME, values, selection, null);

            // update the relationship
            relationValues = new ContentValues();
            relationValues.put(ContactsOpportunitiesColumns.CONTACT_ID, contactId);
            relationValues.put(ContactsOpportunitiesColumns.OPPORTUNITY_ID, opportunityId);
            relationValues.put(ContactsOpportunitiesColumns.DELETED, Util.NEW_ITEM);
            rowId = db.insert(DatabaseHelper.CONTACTS_OPPORTUNITIES_TABLE_NAME, "", relationValues);
            if (rowId > 0)
                Log.i(TAG, "created relation: opportunityId - " + opportunityId + " contactId - "
                                                + contactId);

            if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                String accountName = values.getAsString(ModuleFields.ACCOUNT_NAME);

                if (!TextUtils.isEmpty(accountName)) {

                    // get the account id for the account name
                    selection = AccountsColumns.NAME + "='" + accountName + "'";
                    Cursor cursor = query(mOpenHelper.getModuleUri(Util.ACCOUNTS), Accounts.LIST_PROJECTION, selection, null, null);
                    String newAccountId = null;
                    if (cursor.moveToFirst())
                        newAccountId = cursor.getString(0);
                    cursor.close();

                    // get the accountId to which this opportunity is related to
                    selection = AccountsContactsColumns.CONTACT_ID + "=" + contactId;
                    cursor = db.query(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME, new String[] { AccountsContactsColumns.ACCOUNT_ID }, selection, null, null, null, null);
                    accountId = null;
                    if (cursor.moveToFirst())
                        accountId = cursor.getString(0);
                    cursor.close();

                    if (newAccountId != null && accountId != null) {
                        if (!newAccountId.equals(accountId)) {
                            // the row Id of the new account is not the same as the old one

                            // update the delete flag to '1' for the old relationship
                            ContentValues val2 = new ContentValues();
                            selection = AccountsContactsColumns.ACCOUNT_ID + "=" + accountId
                                                            + " AND "
                                                            + AccountsContactsColumns.CONTACT_ID
                                                            + "=" + contactId;
                            val2.put(AccountsContactsColumns.DELETED, Util.DELETED_ITEM);
                            db.update(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME, val2, selection, null);
                            Log.i(TAG, "updated deleted flag for the relation: contactId - "
                                                            + contactId + " oldAccountId - "
                                                            + accountId);

                            // create a new relationship with the new account
                            ContentValues val3 = new ContentValues();
                            val3.put(AccountsContactsColumns.ACCOUNT_ID, newAccountId);
                            val3.put(AccountsContactsColumns.CONTACT_ID, opportunityId);
                            val3.put(AccountsContactsColumns.DELETED, Util.NEW_ITEM);
                            rowId = db.insert(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME, "", val3);
                            if (rowId > 0)
                                Log.i(TAG, "created relation: contactId - " + contactId
                                                                + " accountId - " + newAccountId);
                        }
                    }

                } else {
                    // if the accountName is removed while updating, delete the relationship

                    // update the delete flag to '1' for the old relationship
                    ContentValues val2 = new ContentValues();
                    selection = AccountsContactsColumns.CONTACT_ID + "=" + contactId;
                    val2.put(AccountsContactsColumns.DELETED, Util.DELETED_ITEM);
                    db.update(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME, val2, selection, null);
                    Log.i(TAG, "updated deleted flag for the relations in ACCOUNT_CONTACTS table with contactId - "
                                                    + contactId);
                }
            }

            break;

        case CASE:
            count = db.update(DatabaseHelper.CASES_TABLE_NAME, values, where, whereArgs);
            break;

        case CASE_ID:
            caseId = uri.getPathSegments().get(1);
            count = db.update(DatabaseHelper.CASES_TABLE_NAME, values, Cases.ID
                                            + "="
                                            + caseId
                                            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                                                            : ""), whereArgs);

            deleted = values.getAsString(ModuleFields.DELETED);
            if (!TextUtils.isEmpty(deleted) && deleted.equals(Util.DELETED_ITEM)) {
                // update the delete flag of all relationships
                String[] tableNames = mOpenHelper.getRelationshipTables(Util.CASES);
                String whereClause = Util.CASE_ID + "=" + caseId;
                for (String tableName : tableNames) {
                    db.update(tableName, values, whereClause, null);
                }
            } else {
                if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                    String accountName = (String) values.get(ModuleFields.ACCOUNT_NAME);
                    if (!TextUtils.isEmpty(accountName)) {
                        // get the account id for the account name
                        selection = AccountsColumns.NAME + "='" + accountName + "'";
                        Cursor cursor = query(mOpenHelper.getModuleUri(Util.ACCOUNTS), Accounts.LIST_PROJECTION, selection, null, null);
                        String newAccountId = null;
                        if (cursor.moveToFirst())
                            newAccountId = cursor.getString(0);
                        cursor.close();

                        // get the accountId to which this opportunity is related to
                        selection = AccountsCasesColumns.CASE_ID + "=" + caseId;
                        cursor = db.query(DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME, new String[] { AccountsCasesColumns.ACCOUNT_ID }, selection, null, null, null, null);
                        accountId = null;
                        if (cursor.moveToFirst())
                            accountId = cursor.getString(0);
                        cursor.close();

                        if (newAccountId != null && accountId != null) {
                            if (!newAccountId.equals(accountId)) {
                                // the row Id of the new account is not the same as the old one

                                // update the delete flag to '1' for the old relationship
                                ContentValues val2 = new ContentValues();
                                selection = AccountsCasesColumns.ACCOUNT_ID + "=" + accountId
                                                                + " AND "
                                                                + AccountsCasesColumns.CASE_ID
                                                                + "=" + caseId;
                                val2.put(AccountsCasesColumns.DELETED, Util.DELETED_ITEM);
                                db.update(DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME, val2, selection, null);
                                Log.i(TAG, "updated deleted flag for the relation: caseId - "
                                                                + caseId + " oldAccountId - "
                                                                + accountId);

                                // create a new relationship with the new account
                                ContentValues val3 = new ContentValues();
                                val3.put(AccountsCasesColumns.ACCOUNT_ID, newAccountId);
                                val3.put(AccountsCasesColumns.CASE_ID, caseId);
                                val3.put(AccountsCasesColumns.DELETED, Util.NEW_ITEM);
                                rowId = db.insert(DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME, "", val3);
                                if (rowId > 0)
                                    Log.i(TAG, "created relation: caseId - " + caseId
                                                                    + " accountId - "
                                                                    + newAccountId);
                            }
                        }

                    } else {
                        // delete the relationship if there exists one

                        relationValues = new ContentValues();
                        selection = AccountsCasesColumns.CASE_ID + "=" + caseId;
                        relationValues.put(AccountsCasesColumns.DELETED, Util.DELETED_ITEM);
                        db.update(DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME, relationValues, selection, null);
                    }
                }
            }

            break;

        case CALL:
            count = db.update(DatabaseHelper.CALLS_TABLE_NAME, values, where, whereArgs);
            break;

        case CALL_ID:
            String callId = uri.getPathSegments().get(1);
            count = db.update(DatabaseHelper.CALLS_TABLE_NAME, values, Calls.ID
                                            + "="
                                            + callId
                                            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                                                            : ""), whereArgs);
            break;

        case MEETING:
            count = db.update(DatabaseHelper.MEETINGS_TABLE_NAME, values, where, whereArgs);
            break;

        case MEETING_ID:
            String meetingId = uri.getPathSegments().get(1);
            count = db.update(DatabaseHelper.MEETINGS_TABLE_NAME, values, Meetings.ID
                                            + "="
                                            + meetingId
                                            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                                                            : ""), whereArgs);
            break;
            
        case CAMPAIGN:
            count = db.update(DatabaseHelper.CAMPAIGNS_TABLE_NAME, values, where, whereArgs);
            break;

        case CAMPAIGN_ID:
            String campaignId = uri.getPathSegments().get(1);
            count = db.update(DatabaseHelper.CAMPAIGNS_TABLE_NAME, values, Campaigns.ID
                                            + "="
                                            + campaignId
                                            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                                                            : ""), whereArgs);
            break;
            
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static Map<String, String> getProjectionMap(String tableName, String[] projections) {
        Map<String, String> projectionMap = mProjectionMaps.get(tableName);
        if (projectionMap != null)
            return projectionMap;
        projectionMap = new HashMap<String, String>();
        for (String column : projections) {
            projectionMap.put(column, tableName + "." + column);
        }
        mProjectionMaps.put(tableName, projectionMap);
        return projectionMap;
    }

    static Map<String, Map> mProjectionMaps = new HashMap<String, Map>();

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, "Search" + "/"
                                        + SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH);

        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACCOUNTS, ACCOUNT);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACCOUNTS + "/#/#", ACCOUNT);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACCOUNTS + "/#", ACCOUNT_ID);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACCOUNTS + "/#/" + Util.CONTACTS, ACCOUNT_CONTACT);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACCOUNTS + "/#/" + Util.CONTACTS + "/#", ACCOUNT_CONTACT);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACCOUNTS + "/#/" + Util.LEADS, ACCOUNT_LEAD);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACCOUNTS + "/#/" + Util.OPPORTUNITIES, ACCOUNT_OPPORTUNITY);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACCOUNTS + "/#/" + Util.OPPORTUNITIES
                                        + "/#", ACCOUNT_OPPORTUNITY);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACCOUNTS + "/#/" + Util.CASES, ACCOUNT_CASE);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACCOUNTS + "/#/" + Util.CASES + "/#", ACCOUNT_CASE);

        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CONTACTS, CONTACT);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CONTACTS + "/#", CONTACT_ID);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CONTACTS + "/#/#", CONTACT);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CONTACTS + "/#/" + Util.OPPORTUNITIES, CONTACT_OPPORTUNITY);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CONTACTS + "/#/" + Util.OPPORTUNITIES
                                        + "/#", CONTACT_OPPORTUNITY);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CONTACTS + "/#/case", CONTACT_CASE);

        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.LEADS, LEAD);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.LEADS + "/#", LEAD_ID);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.LEADS + "/#/#", LEAD);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.LEADS + "/#/" + Util.OPPORTUNITIES, LEAD_OPPORTUNITY);

        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.OPPORTUNITIES, OPPORTUNITY);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.OPPORTUNITIES + "/#", OPPORTUNITY_ID);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.OPPORTUNITIES + "/#/#", OPPORTUNITY);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.OPPORTUNITIES + "/#/" + Util.CONTACTS, OPPORTUNITY_CONTACT);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.OPPORTUNITIES + "/#/" + Util.CONTACTS
                                        + "/#", OPPORTUNITY_CONTACT);

        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.MEETINGS, MEETING);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.MEETINGS + "/#", MEETING_ID);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.MEETINGS + "/#/#", MEETING);

        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CALLS, CALL);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CALLS + "/#", CALL_ID);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CALLS + "/#/#", CALL);

        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CASES, CASE);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CASES + "/#", CASE_ID);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CASES + "/#/#", CASE);

        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CAMPAIGNS, CAMPAIGN);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CAMPAIGNS + "/#", CAMPAIGN_ID);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CAMPAIGNS + "/#/#", CAMPAIGN);

        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.USERS, USERS);

        // sUriMatcher.addURI(SugarBeans.AUTHORITY, "sugarbeans/#", SUGAR_BEAN_ID);

    }
}
