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

        ServiceHelper.startService(getContext(), uri);

        Cursor c = null;
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
            int size = uri.getPathSegments().size();
            String maxResultsLimit = null;
            if (size == 3)
                maxResultsLimit = uri.getPathSegments().get(2);
            // qb.setTables(DatabaseHelper.CONTACTS_TABLE_NAME);
            Log.d(TAG, "maxResultsLimit" + maxResultsLimit);
            c = db.query(DatabaseHelper.CONTACTS_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder, maxResultsLimit);

            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
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
                Uri noteUri = ContentUris.withAppendedId(SugarCRMContent.Contacts.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(noteUri, null);
                return noteUri;
            }
            break;

        case CONTACT:
            rowId = db.insert(DatabaseHelper.CONTACTS_TABLE_NAME, "", values);
            if (rowId > 0) {
                Uri noteUri = ContentUris.withAppendedId(SugarCRMContent.Contacts.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(noteUri, null);
                return noteUri;
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
        // sUriMatcher.addURI(SugarBeans.AUTHORITY, "sugarbeans/#", SUGAR_BEAN_ID);

    }
}
