package com.imaginea.android.sugarcrm;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.util.ModuleField;

/**
 * AccountDetailsActivity
 * 
 * @author vasavi
 */
public class AccountDetailsActivity extends Activity {

    private String mAccountSugarBeanId;

    private Cursor mCursor;    

    private String[] mSelectFields;

    private final String LOG_TAG = "AccountDetailsActivity";

    private TableLayout mDetailsTable;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.account_details);

        mAccountSugarBeanId = (String) getIntent().getStringExtra(RestUtilConstants.ID);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String moduleName = "Contacts";
        if (extras != null)
            moduleName = extras.getString(RestUtilConstants.MODULE_NAME);

        if (intent.getData() == null) {
            intent.setData(Uri.withAppendedPath(DatabaseHelper.getModuleUri(moduleName), mAccountSugarBeanId));
        }
        mSelectFields = DatabaseHelper.getModuleProjections(moduleName);
        mCursor = getContentResolver().query(getIntent().getData(), mSelectFields, null, null, DatabaseHelper.getModuleSortOrder(moduleName));
        // startManagingCursor(mCursor);
        setContents(moduleName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        if(mCursor != null && !mCursor.isClosed())
            mCursor.close();
        
    }

    private void setContents(String moduleName) {

        String[] detailsProjection = mSelectFields;

        mDetailsTable = (TableLayout) findViewById(R.id.accountDetalsTable);

        mCursor.moveToFirst();
        for (int i = 1; i < detailsProjection.length; i++) {
            String fieldName = detailsProjection[i];
            int columnIndex = mCursor.getColumnIndex(fieldName);
            Log.d(LOG_TAG, "Col:" + columnIndex + " moduleName : " + moduleName + " fieldName : " + fieldName );
            
            //TODO: get the attributes of the moduleField
            ModuleField moduleField = DatabaseHelper.getModuleField(moduleName, fieldName);
            
            TextView textViewForLabel = new TextView(AccountDetailsActivity.this);
            textViewForLabel.setText(moduleField.getLabel());
            
            TextView textViewForValue = new TextView(AccountDetailsActivity.this);
            String value = mCursor.getString(columnIndex);

            if (value != null && !value.equals("")) {
                textViewForValue.setText(value);
            } else {
                textViewForValue.setText(R.string.notAvailable);
            }

            TableRow tableRow = new TableRow(AccountDetailsActivity.this);
            tableRow.addView(textViewForLabel);
            tableRow.addView(textViewForValue);

            mDetailsTable.addView(tableRow);
        }
    }
}
