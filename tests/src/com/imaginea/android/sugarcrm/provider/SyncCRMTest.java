package com.imaginea.android.sugarcrm.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.Time;

import com.imaginea.android.sugarcrm.ModuleFields;

/**
 * Sugar CRM Sync instrumentation tests. Testing creation of new accounts, deleting accounts,
 * editing accounts.
 */
public class SyncCRMTest extends CRMSyncTestingBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testCreateNewAccount() throws Exception {
        int countBeforeNewAccount = getAccountsCount();
        insertAccount();
        assertTrue("No New account was added. ", getAccountsCount() > countBeforeNewAccount);
    }

    public void testEditAccountName() throws Exception {
        Cursor cursor;
        cursor = mResolver.query(mAccountsUri, null, null, null, null);

        int countBeforeNewAccount = cursor.getCount();
        cursor.moveToNext();
        Time time = new Time();
        time.setToNow();
        String newTitle = cursor.getString(cursor.getColumnIndex(ModuleFields.NAME))
                                        + time.toString();

        long accountId = cursor.getLong(cursor.getColumnIndex(SugarCRMContent.RECORD_ID));

        cursor.close();
        ContentValues values = new ContentValues();
        values.put(ModuleFields.NAME, newTitle);
        // values.put(ModuleFields., value)
        editAccount(accountId, values);
        cursor = mResolver.query(mAccountsUri, null, null, null, null);
        assertTrue("Events count should remain same.", getAccountsCount() == countBeforeNewAccount);

        while (cursor.moveToNext()) {
            if (cursor.getLong(cursor.getColumnIndex(SugarCRMContent.RECORD_ID)) == accountId) {
                assertEquals(cursor.getString(cursor.getColumnIndex(ModuleFields.NAME)), newTitle);
                break;
            }
        }
        cursor.close();
    }

    public void testCreateAndDeleteAccount() throws Exception {
        syncSugarCRMAccounts();
        int countBeforeNewAccount = getAccountsCount();
        Uri insertUri = insertAccount();

        assertTrue("A account should have been created.", getAccountsCount() > countBeforeNewAccount);
        deleteAccount(insertUri);
        assertEquals("Account should have been deleted.", countBeforeNewAccount, getAccountsCount());
    }
}
