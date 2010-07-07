package com.imaginea.android.sugarcrm.provider;

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

import java.util.HashMap;

/**
 * SugarCRMProvider Provides access to a database of sugar beans. Each bean has a id, the bean
 * itself, a creation date and a modified data.
 */
public class SugarCRMProvider { //extends ContentProvider {

    /*
    public static final String AUTHORITY = "com.imaginea.sugarcrm.provider";

    private static HashMap<String, String> sSugarBeansProjectionMap;

    private static final int SUGAR_BEANS = 1;

    private static final int SUGAR_BEAN_ID = 2;

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
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(DatabaseHelper.ACCOUNTS_TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
        case SUGAR_BEANS:

            break;

        case SUGAR_BEAN_ID:

            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = SugarBeans.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        case SUGAR_BEANS:

        case SUGAR_BEAN_ID:
            return SugarBeans.CONTENT_ITEM_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != SUGAR_BEANS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        Long now = Long.valueOf(System.currentTimeMillis());

        // Make sure that the fields are all set

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(DatabaseHelper.ACCOUNTS_TABLE_NAME, SugarBeans.BEAN, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(SugarBeans.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case SUGAR_BEANS:
            count = db.delete(DatabaseHelper.ACCOUNTS_TABLE_NAME, where, whereArgs);
            break;

        case SUGAR_BEAN_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.delete(DatabaseHelper.ACCOUNTS_TABLE_NAME, SugarBeans._ID
                                            + "="
                                            + noteId
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
        case SUGAR_BEANS:
            count = db.update(DatabaseHelper.ACCOUNTS_TABLE_NAME, values, where, whereArgs);
            break;

        case SUGAR_BEAN_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.update(DatabaseHelper.ACCOUNTS_TABLE_NAME, values, SugarBeans._ID
                                            + "="
                                            + noteId
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
        sUriMatcher.addURI(SugarBeans.AUTHORITY, "sugarbeans", SUGAR_BEANS);
        sUriMatcher.addURI(SugarBeans.AUTHORITY, "sugarbeans/#", SUGAR_BEAN_ID);

    }*/
}
