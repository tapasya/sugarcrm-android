package com.imaginea.android.sugarcrm.restapi;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.imaginea.android.sugarcrm.provider.DatabaseHelper;

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

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        dbHelper.executeSQLFromFile(SQL_FILE);

    }
}
