package com.imaginea.android.sugarcrm.provider;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.test.SyncBaseInstrumentation;
import android.util.Log;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.util.Util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * 
 * CRMSyncTestingBase
 * 
 * @author chander
 * 
 */
public class CRMSyncTestingBase extends SyncBaseInstrumentation {
    protected AccountManager mAccountManager;

    protected Context mTargetContext;

    protected Account mAccount;

    protected ContentResolver mResolver;

    protected Uri mAccountsUri = SugarCRMContent.Accounts.CONTENT_URI;

    private Random mRandom = new Random();

    private static final String TAG = CRMSyncTestingBase.class.getSimpleName();

    static final Set<String> ACCOUNT_COLUMNS_TO_SKIP = new HashSet<String>();

    static {

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mTargetContext = getInstrumentation().getTargetContext();

        mAccountManager = AccountManager.get(mTargetContext);
        mAccount = getAccount();
        mResolver = mTargetContext.getContentResolver();
    }

    /**
     * A simple method that syncs the sugar crm provider.
     * 
     * @throws Exception
     */
    protected void syncSugarCRMAccounts() throws Exception {
        cancelSyncsandDisableAutoSync();
        syncProvider(mAccountsUri, mAccount.name, SugarCRMContent.AUTHORITY);
    }

    /**
     * Creates a new account.
     * 
     * @param account
     *            Account to be created.
     * @return Uri of the account created.
     * @throws Exception
     */
    protected Uri insertAccount() throws Exception {
        ContentValues m = new ContentValues();
        m.put(ModuleFields.ID, getBeanId());
        m.put(ModuleFields.NAME, getMockAccountName());
        m.put(ModuleFields.DELETED, Util.NEW_ITEM);
        // TODO - date modified - long or string
        Uri url = mResolver.insert(mAccountsUri, m);

        syncSugarCRMAccounts();
        return url;
    }

    /**
     * dummy implementation to return a random beanId
     * 
     * @return
     */
    private String getBeanId() {
        return UUID.randomUUID().toString();
    }

    /**
     * return mock account name
     * 
     * @return
     */
    private String getMockAccountName() {
        return "1_AccountMock" + mRandom.nextInt();
    }

    /**
     * Edits the given account.
     * 
     * @param accountId
     *            accountId of the account to be edited.
     * @throws Exception
     */
    protected void editAccount(long accountId, ContentValues values) throws Exception {

        Uri uri = ContentUris.withAppendedId(SugarCRMContent.Accounts.CONTENT_URI, accountId);
        mResolver.update(uri, values, null, null);
        syncSugarCRMAccounts();
    }

    /**
     * Deletes a given account.
     * 
     * @param uri
     * @throws Exception
     */
    protected void deleteAccount(Uri uri) throws Exception {
        mResolver.delete(uri, null, null);
        syncSugarCRMAccounts();
    }

    /**
     * Returns a count of accounts.
     * 
     * @return
     */
    protected int getAccountsCount() {
        Cursor cursor;
        cursor = mResolver.query(mAccountsUri, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     * Returns the ID of the default account.
     * 
     * @return
     */
    protected int getDefaultAccountId() {
        Cursor accountCursor;
        accountCursor = mResolver.query(mAccountsUri, null, null, null, null);
        accountCursor.moveToNext();
        int accountId = accountCursor.getInt(accountCursor.getColumnIndex(SugarCRMContent.RECORD_ID));
        accountCursor.close();
        return accountId;
    }

    /**
     * Returns the default sugar crm account on the device.
     * 
     * @return
     */
    protected String getAccountName() {
        Account[] accounts = mAccountManager.getAccountsByType(Util.ACCOUNT_TYPE);

        assertTrue("Didn't find any sugar crm accounts", accounts.length > 0);

        Account account = accounts[accounts.length - 1];
        Log.v(TAG, "Found " + accounts.length + " accounts; using the last one, " + account.name);
        return account.name;
    }
    
    /**
     * Returns the default sugar crm account on the device.
     * 
     * @return an Account obj
     */
    protected Account getAccount() {
        Account[] accounts = mAccountManager.getAccountsByType(Util.ACCOUNT_TYPE);

        assertTrue("Didn't find any sugar crm accounts", accounts.length > 0);

        Account account = accounts[accounts.length - 1];
        Log.v(TAG, "Found " + accounts.length + " accounts; using the last one, " + account.name);
        return account;
    }

    /**
     * Compares two cursors and skips the READ-ONLY columns that do not change
     */
    protected void compareCursors(Cursor cursor1, Cursor cursor2, Set<String> columnsToSkip,
                                    String tableName) {
        String[] cols = cursor1.getColumnNames();
        int length = cols.length;

        assertEquals(tableName + " count failed to match", cursor1.getCount(), cursor2.getCount());
        Map<String, String> row = new HashMap<String, String>();
        while (cursor1.moveToNext() && cursor2.moveToNext()) {
            for (int i = 0; i < length; i++) {
                String col = cols[i];
                if (columnsToSkip != null && columnsToSkip.contains(col)) {
                    continue;
                }
                row.put(col, cursor1.getString(i));

                assertEquals("Row: " + row + " Table: " + tableName + ": " + cols[i]
                                                + " failed to match", cursor1.getString(i), cursor2.getString(i));
            }
        }
    }
}
