package com.imaginea.android.sugarcrm.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.imaginea.android.sugarcrm.ServiceHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Accounts;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Contacts;

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

    private static final UriMatcher sUriMatcher;

    private static final String TAG = "SugarCRMProvider";

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
        int size = uri.getPathSegments().size();
        String maxResultsLimit = null;
        String offset = null;
        if (size == 3) {
            offset = uri.getPathSegments().get(1);
            maxResultsLimit = uri.getPathSegments().get(2);
        }
        // SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        switch (sUriMatcher.match(uri)) {
        case ACCOUNT:
            c = db.query(DatabaseHelper.ACCOUNTS_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            break;

        case CONTACT:
            Log.d(TAG, "Querying Contacts");
            Log.d(TAG, "Uri:->" + uri.toString());

            // qb.setTables(DatabaseHelper.CONTACTS_TABLE_NAME);
            Log.d(TAG, "Offset" + offset);
            Log.d(TAG, "maxResultsLimit" + maxResultsLimit);
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
            // db.setProjectionMap(sNotesProjectionMap);
            selection = SugarCRMContent.RECORD_ID + " = ?";
            c = db.query(DatabaseHelper.CONTACTS_TABLE_NAME, projection, selection, new String[] { uri.getPathSegments().get(1) }, null, null, null);
            // qb.appendWhere(Notes._ID + "=" + uri.getPathSegments().get(1));
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Log.d(TAG, "Count:" + c.getCount());
        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);

        // database cache miss, start a rest api call , package the params appropriately
        if (c.getCount() == 0)
            ServiceHelper.startService(getContext(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
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
                Uri accountUri = ContentUris.withAppendedId(SugarCRMContent.Contacts.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(accountUri, null);
                return accountUri;
            }
            break;

        case CONTACT:
            rowId = db.insert(DatabaseHelper.CONTACTS_TABLE_NAME, "", values);
            if (rowId > 0) {
                Uri contactUri = ContentUris.withAppendedId(SugarCRMContent.Contacts.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(contactUri, null);
                return contactUri;
            }
            break;
        default:
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

        case CONTACT:
            count = db.delete(DatabaseHelper.CONTACTS_TABLE_NAME, where, whereArgs);
            break;

        case CONTACT_ID:
            String contactId = uri.getPathSegments().get(1);
            count = db.delete(DatabaseHelper.CONTACTS_TABLE_NAME, Contacts.ID
                                            + "="
                                            + contactId
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

        case CONTACT:
            count = db.update(DatabaseHelper.CONTACTS_TABLE_NAME, values, where, whereArgs);
            break;

        case CONTACT_ID:
            String contactId = uri.getPathSegments().get(1);
            count = db.update(DatabaseHelper.CONTACTS_TABLE_NAME, values, Contacts.ID
                                            + "="
                                            + contactId
                                            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
                                                                            : ""), whereArgs);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, "account", ACCOUNT);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, "account/#/#", ACCOUNT);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, "account/#", ACCOUNT_ID);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, "contact", CONTACT);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, "contact/#", CONTACT_ID);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, "contact/#/#", CONTACT);
        // sUriMatcher.addURI(SugarBeans.AUTHORITY, "sugarbeans/#", SUGAR_BEAN_ID);

    }
}
