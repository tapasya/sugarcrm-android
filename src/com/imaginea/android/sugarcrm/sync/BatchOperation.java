package com.imaginea.android.sugarcrm.sync;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

import com.imaginea.android.sugarcrm.provider.SugarCRMProvider;

import java.util.ArrayList;

/**
 * This class handles execution of batch mOperations on SugarCRM provider.
 */
public class BatchOperation {

    private final ContentResolver mResolver;

    // List for storing the batch mOperations
    ArrayList<ContentProviderOperation> mOperations;

    private final String LOG_TAG = BatchOperation.class.getSimpleName();

    /**
     * <p>
     * Constructor for BatchOperation.
     * </p>
     * 
     * @param resolver
     *            a {@link android.content.ContentResolver} object.
     */
    public BatchOperation(ContentResolver resolver) {
        mResolver = resolver;
        mOperations = new ArrayList<ContentProviderOperation>();
    }

    /**
     * <p>
     * size
     * </p>
     * 
     * @return a int.
     */
    public int size() {
        return mOperations.size();
    }

    /**
     * <p>
     * add
     * </p>
     * 
     * @param cpo
     *            a {@link android.content.ContentProviderOperation} object.
     */
    public void add(ContentProviderOperation cpo) {
        mOperations.add(cpo);
    }

    /**
     * <p>
     * execute
     * </p>
     */
    public void execute() {
        if (mOperations.size() == 0) {
            Log.v(LOG_TAG, "No Batch Operations found to execute");
            return;
        }
        // Apply the mOperations to the content provider
        try {
            Log.v(LOG_TAG, " Batch Operations Size:" + mOperations.size());
            ContentProviderResult[] result = mResolver.applyBatch(SugarCRMProvider.AUTHORITY, mOperations);
            Log.v(LOG_TAG, " result.length" + result.length);
        } catch (final OperationApplicationException e1) {
            Log.e(LOG_TAG, "storing Module data failed", e1);
        } catch (final RemoteException e2) {
            Log.e(LOG_TAG, "storing Module data failed", e2);
        }
        mOperations.clear();
    }
}
