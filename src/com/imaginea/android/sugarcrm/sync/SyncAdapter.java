package com.imaginea.android.sugarcrm.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.imaginea.android.sugarcrm.util.Util;

import org.apache.http.ParseException;

import java.io.IOException;
import java.util.Date;

/**
 * SyncAdapter implementation for syncing sample SyncAdapter contacts to the platform
 * ContactOperations provider.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private final AccountManager mAccountManager;

    private final Context mContext;

    private Date mLastUpdated;

    private static final String LOG_TAG = "SyncAdapter";

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                                    ContentProviderClient provider, SyncResult syncResult) {      
        Log.d(LOG_TAG, "onPerformSync");
        String authtoken = null;
        try {
            // use the account manager to request the credentials
            authtoken = mAccountManager.blockingGetAuthToken(account, Util.AUTHTOKEN_TYPE, true /* notifyAuthFailure */);

        } catch (final ParseException e) {
            syncResult.stats.numParseExceptions++;
            Log.e(LOG_TAG, "ParseException", e);
        } catch (OperationCanceledException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (AuthenticatorException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
