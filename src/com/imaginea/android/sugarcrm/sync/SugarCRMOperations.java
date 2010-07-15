package com.imaginea.android.sugarcrm.sync;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent;
import com.imaginea.android.sugarcrm.util.SugarBean;

import java.util.Map;

/**
 * Helper class for storing data in the platform content providers.
 */
public class SugarCRMOperations {

    private String mModuleName;

    private final ContentValues mValues;

    private ContentProviderOperation.Builder mBuilder;

    private final BatchOperation mBatchOperation;

    private final Context mContext;

    private boolean mYield;

    private long mRawId;

    private int mBackReference;

    private boolean mIsNewId;

    /**
     * Returns an instance of SugarCRMOperations instance for adding new module item to the sugar
     * crm provider.
     * 
     * @param context
     *            the Authenticator Activity context
     * @param userId
     *            the userId of the SyncAdapter user object
     * @param accountName
     *            the username of the current login
     * @return instance of ContactOperations
     */
    public static SugarCRMOperations createNewModuleItem(Context context, String moduleName,
                                    String accountName, SugarBean sBean,
                                    BatchOperation batchOperation) {
        return new SugarCRMOperations(context, moduleName, accountName, batchOperation);
    }

    /**
     * Returns an instance of SugarCRMOperations for updating existing module item in the sugarcrm
     * provider.
     * 
     * @param context
     *            the Authenticator Activity context
     * @param rawId
     *            the unique Id of the existing rawId
     * @return instance of ContactOperations
     */
    public static SugarCRMOperations updateExistingModuleItem(Context context, String moduleName,
                                    SugarBean sBean, long rawId, BatchOperation batchOperation) {
        return new SugarCRMOperations(context, moduleName, sBean, rawId, batchOperation);
    }

    public SugarCRMOperations(Context context, BatchOperation batchOperation) {
        mValues = new ContentValues();
        mModuleName = "";
        mYield = true;
        mContext = context;
        mBatchOperation = batchOperation;
    }

    public SugarCRMOperations(Context context, String moduleName, String accountName,
                                    BatchOperation batchOperation) {
        this(context, batchOperation);
        mBackReference = mBatchOperation.size();
        mModuleName = moduleName;
        mIsNewId = true;
        // mBuilder = newInsertCpo(contentUri, true).withValues(mValues);
        // mBatchOperation.add(mBuilder.build());
    }

    public SugarCRMOperations(Context context, String moduleName, SugarBean sBean, long rawId,
                                    BatchOperation batchOperation) {
        this(context, batchOperation);
        mModuleName = moduleName;
        mIsNewId = false;
        mRawId = rawId;
    }

    public SugarCRMOperations addSugarBean(SugarBean sBean) {
        Map<String, String> map = sBean.getEntryList();
        for (String fieldName : map.keySet()) {
            String fieldValue = map.get(fieldName);
            mValues.put(fieldName, fieldValue);
        }
        if (mValues.size() > 0) {
            addInsertOp();
        }
        return this;
    }

    public SugarCRMOperations updateSugarBean(SugarBean sBean, Uri uri) {
        Map<String, String> map = sBean.getEntryList();
        for (String fieldName : map.keySet()) {
            String fieldValue = map.get(fieldName);
            mValues.put(fieldName, fieldValue);
        }
        if (mValues.size() > 0) {
            addUpdateOp(uri);
        }
        return this;
    }

    /**
     * Adds an insert operation into the batch
     */
    private void addInsertOp() {
        if (!mIsNewId) {
            mValues.put(SugarCRMContent.RECORD_ID, mRawId);
        }
        Uri contentUri = DatabaseHelper.getModuleUri(mModuleName);
        mBuilder = newInsertCpo(contentUri, mYield);
        mBuilder.withValues(mValues);
        // TODO - check out the undocumented Value backreferences
        // if (mIsNewId) {
        // mBuilder.withValueBackReference(SugarCRMContent.RECORD_ID, mBackReference);
        // }
        mYield = false;
        mBatchOperation.add(mBuilder.build());
    }

    /**
     * Adds an update operation into the batch
     */
    private void addUpdateOp(Uri uri) {
        mBuilder = newUpdateCpo(uri, mYield).withValues(mValues);
        mYield = false;
        mBatchOperation.add(mBuilder.build());
    }

    public static ContentProviderOperation.Builder newInsertCpo(Uri uri, boolean yield) {
        return ContentProviderOperation.newInsert(uri).withYieldAllowed(yield);
    }

    public static ContentProviderOperation.Builder newUpdateCpo(Uri uri, boolean yield) {
        return ContentProviderOperation.newUpdate(uri).withYieldAllowed(yield);
    }

    public static ContentProviderOperation.Builder newDeleteCpo(Uri uri, boolean yield) {
        return ContentProviderOperation.newDelete(uri).withYieldAllowed(yield);

    }
}
