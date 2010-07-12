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
import android.widget.TableRow;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AccountsColumns;
import com.imaginea.android.sugarcrm.util.ModuleField;

import java.util.LinkedHashMap;
import java.util.Map;

public class EditDetailsActivity extends Activity {

    private final String TAG = "EditDetailsActivity";

    private TableLayout mDetailsTable;

    private Cursor mCursor;
    
    private String mSugarBeanId;

    private String mRowId;

    private String[] mSelectFields;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.account_details);

        mRowId = (String) getIntent().getStringExtra(RestUtilConstants.ID);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String moduleName = "Contacts";
        if (extras != null)
            moduleName = extras.getString(RestUtilConstants.MODULE_NAME);

        if (intent.getData() == null) {
            intent.setData(Uri.withAppendedPath(DatabaseHelper.getModuleUri(moduleName), mRowId));
        }
        mSelectFields = DatabaseHelper.getModuleProjections(moduleName);
        mCursor = getContentResolver().query(getIntent().getData(), mSelectFields, null, null, DatabaseHelper.getModuleSortOrder(moduleName));
        // startManagingCursor(mCursor);
        setContents(moduleName);
    }

    private void setContents(final String moduleName) {

        String[] detailsProjection = mSelectFields;

        mDetailsTable = (TableLayout) findViewById(R.id.accountDetalsTable);

        mCursor.moveToFirst();
        mSugarBeanId = mCursor.getString(mCursor.getColumnIndex(AccountsColumns.BEAN_ID));
        Log.i(TAG, "mSugarBeanId - " + mSugarBeanId);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        for (int i = 2; i < detailsProjection.length; i++) {
            String fieldName = detailsProjection[i];
            int columnIndex = mCursor.getColumnIndex(fieldName);
            Log.d(TAG, "Col:" + columnIndex);

            // TODO: get the attributes of the moduleField
            ModuleField moduleField = DatabaseHelper.getModuleField(moduleName, fieldName);
            
            View tableRow = inflater.inflate(R.layout.edit_table_row, null);
            TextView textViewForLabel = (TextView)tableRow.findViewById(R.id.editRowLabel);
            EditText editTextForValue = (EditText)tableRow.findViewById(R.id.editRowValue);

            if (moduleField.isRequired()) {
                textViewForLabel.setText(moduleField.getLabel() + " *");
            } else {
                textViewForLabel.setText(moduleField.getLabel());
            }

            editTextForValue.setId(i);
            String value = mCursor.getString(columnIndex);

            if (value != null && !value.equals("")) {
                editTextForValue.setText(value);
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
                modifiedValues.put(RestUtilConstants.ID, mSugarBeanId);
                
                for (int i = 2; i < detailsProjection.length; i++) {
                    EditText editText = (EditText) mDetailsTable.findViewById(i);
                    Log.i(TAG, detailsProjection[i] + " : " + editText.getText().toString());

                    // what all fields can be updated ?

                    // TODO: validation

                    // add the fieldName : fieldValue in the ContentValues
                    modifiedValues.put(detailsProjection[i], editText.getText().toString());
                }

                // save the changes in the DB
                /*getContentResolver().update(Uri.withAppendedPath(DatabaseHelper.getModuleUri(moduleName), 
                                                mSugarBeanId), modifiedValues, null, null);*/

                //TODO: REST call : update();
                
                ServiceHelper.startServiceForUpdate(getBaseContext(), Uri.withAppendedPath(DatabaseHelper.getModuleUri(moduleName), 
                                                mRowId), moduleName, mSugarBeanId, modifiedValues);
                finish();
            }
        });
        mDetailsTable.addView(submit);

    }
}
