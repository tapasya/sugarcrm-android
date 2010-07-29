package com.imaginea.android.sugarcrm.restapi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.imaginea.android.sugarcrm.provider.DatabaseHelper;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

public class ModuleFieldsOrderInsertTest extends AndroidTestCase {

    private final String TAG = ModuleFieldsOrderInsertTest.class.getSimpleName();

    private final String SQL_FILE = "sortOrderAndGroup.sql";

    /*
     * Trying to insert the module fields sort oder and grouping. Open the SQL_FILE, i.e.
     * 'sortOrderAndGroup.sql' which has to be there in the assets folder to read each SQL insert
     * statement and execute
     */
    @SmallTest
    public void testSortOrderInsertion() throws Exception {
        InputStream is = getContext().getAssets().open(SQL_FILE);

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        /*
         * Use the openFileInput() method the ActivityContext provides. Again for security reasons
         * with openFileInput(...)
         */
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String sql;
        while ((sql = br.readLine()) != null) {
            db.execSQL(sql);
            if (Log.isLoggable(TAG, Log.DEBUG))
                Log.d(TAG, "read from file: " + sql);
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }
}
