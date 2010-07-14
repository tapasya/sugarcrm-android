package com.imaginea.android.sugarcrm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AccountsColumns;
import com.imaginea.android.sugarcrm.util.ModuleField;
import com.imaginea.android.sugarcrm.util.Util;

import java.util.LinkedHashMap;
import java.util.Map;

public class EditDetailsActivity extends Activity {

    private final String TAG = "EditDetailsActivity";

    private int MODE = -1;

    private TableLayout mDetailsTable;

    private Cursor mCursor;

    private String mSugarBeanId;
    
    private String mModuleName;
    
    private String mParentModuleName;
    
    private String mParentId;

    private String mRowId;

    private String[] mSelectFields;

    private String mLinkFieldName;
    
    private String mAccountName;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.account_details);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();        

        mModuleName = "Contacts";
        if (extras != null){
            mModuleName = extras.getString(RestUtilConstants.MODULE_NAME);
            mRowId = (String) intent.getStringExtra(Util.ROW_ID);
            mLinkFieldName = extras.getString(RestUtilConstants.LINK_FIELD_NAME);
        }

        if (mLinkFieldName != null) {
            MODE = Util.NEW_MODE;
            if (extras != null){
                mLinkFieldName = extras.getString(RestUtilConstants.LINK_FIELD_NAME);
                mParentModuleName = extras.getString(RestUtilConstants.PARENT_MODULE_NAME);
                mParentId = extras.getString(RestUtilConstants.BEAN_ID);
                mAccountName = extras.getString(ModuleFields.ACCOUNT_NAME);
            }
        } else {
            MODE = Util.EDIT_MODE;
        }

        if (intent.getData() == null && MODE != Util.NEW_MODE) {
            intent.setData(Uri.withAppendedPath(DatabaseHelper.getModuleUri(mModuleName), mRowId));
        } else if (intent.getData() == null && MODE == Util.EDIT_MODE) {
            intent.setData(DatabaseHelper.getModuleUri(mModuleName));
        }

        mSelectFields = DatabaseHelper.getModuleProjections(mModuleName);

        if (MODE == Util.EDIT_MODE) {
            mCursor = getContentResolver().query(getIntent().getData(), mSelectFields, null, null, DatabaseHelper.getModuleSortOrder(mModuleName));
        }
        // startManagingCursor(mCursor);
        setContents(mModuleName);
    }

    private void setContents(final String moduleName) {

        String[] detailsProjection = mSelectFields;

        mDetailsTable = (TableLayout) findViewById(R.id.accountDetalsTable);

        if (MODE == Util.EDIT_MODE) {
            mCursor.moveToFirst();
        }

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 2; i < detailsProjection.length-2; i++) {
            String fieldName = detailsProjection[i];

            // TODO: get the attributes of the moduleField
            ModuleField moduleField = DatabaseHelper.getModuleField(moduleName, fieldName);

            if(ModuleFields.ACCOUNT_NAME.equals(fieldName))
                continue;
            View tableRow = inflater.inflate(R.layout.edit_table_row, null);
            TextView textViewForLabel = (TextView) tableRow.findViewById(R.id.editRowLabel);
            EditText editTextForValue = (EditText) tableRow.findViewById(R.id.editRowValue);

            if (moduleField.isRequired()) {
                textViewForLabel.setText(moduleField.getLabel() + " *");
            } else {
                textViewForLabel.setText(moduleField.getLabel());
            }

            editTextForValue.setId(i);
            int columnIndex;
            if (MODE == Util.EDIT_MODE) {
                columnIndex = mCursor.getColumnIndex(fieldName);
                Log.d(TAG, "Col:" + columnIndex);

                mSugarBeanId = mCursor.getString(mCursor.getColumnIndex(AccountsColumns.BEAN_ID));
                
                Log.i(TAG, "mSugarBeanId - " + mSugarBeanId);
                String value = mCursor.getString(columnIndex);

                if (value != null && !value.equals("")) {
                    editTextForValue.setText(value);
                }
            } 
            mDetailsTable.addView(tableRow);
        }

        Button submit = new Button(EditDetailsActivity.this);
        submit.setText("Save");
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] detailsProjection = mSelectFields;

                Map<String, String> modifiedValues = new LinkedHashMap<String, String>();
                if (MODE == Util.EDIT_MODE)
                    modifiedValues.put(RestUtilConstants.ID, mSugarBeanId);

                for (int i = 2; i < detailsProjection.length-2; i++) {
                    if(ModuleFields.ACCOUNT_NAME.equals(detailsProjection[i])){
                        modifiedValues.put(detailsProjection[i], mAccountName);
                        continue;
                    }
                    
                    EditText editText = (EditText) mDetailsTable.findViewById(i);
                    Log.i(TAG, detailsProjection[i] + " : " + editText.getText().toString());

                    // TODO: validation

                    // add the fieldName : fieldValue in the ContentValues
                    modifiedValues.put(detailsProjection[i], editText.getText().toString());
                }

                // save the changes in the DB
                /*
                 * getContentResolver().update(Uri.withAppendedPath(DatabaseHelper.getModuleUri(moduleName
                 * ), mSugarBeanId), modifiedValues, null, null);
                 */

                // TODO: REST call : update();

                if (MODE == Util.EDIT_MODE) {
                    ServiceHelper.startServiceForUpdate(getBaseContext(), Uri.withAppendedPath(DatabaseHelper.getModuleUri(moduleName), mRowId), moduleName, mSugarBeanId, modifiedValues);
                } else if(MODE == Util.NEW_MODE){
                    ServiceHelper.startServiceForInsert(getBaseContext(), Uri.withAppendedPath(DatabaseHelper.getModuleUri(mParentModuleName), mRowId), mParentModuleName, mParentId, mModuleName, mLinkFieldName, modifiedValues);
                }

                finish();
            }
        });
        mDetailsTable.addView(submit);

    }
}
