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
import android.widget.TableLayout;
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
    
    private String mModuleName;

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
        mModuleName = "Contacts";
        if (extras != null)
            mModuleName = extras.getString(RestUtilConstants.MODULE_NAME);

        if (intent.getData() == null) {
            intent.setData(Uri.withAppendedPath(DatabaseHelper.getModuleUri(mModuleName), mAccountSugarBeanId));
        }
        mSelectFields = DatabaseHelper.getModuleProjections(mModuleName);
        mCursor = getContentResolver().query(getIntent().getData(), mSelectFields, null, null, DatabaseHelper.getModuleSortOrder(mModuleName));
        // startManagingCursor(mCursor);
        setContents(mModuleName);
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
        
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        for (int i = 2; i < detailsProjection.length; i++) {
            String fieldName = detailsProjection[i];
            int columnIndex = mCursor.getColumnIndex(fieldName);
            Log.d(LOG_TAG, "Col:" + columnIndex + " moduleName : " + moduleName + " fieldName : " + fieldName );
            
            //TODO: get the attributes of the moduleField
            ModuleField moduleField = DatabaseHelper.getModuleField(moduleName, fieldName);
        
            View tableRow = inflater.inflate(R.layout.table_row, null);
            TextView textViewForLabel = (TextView)tableRow.findViewById(R.id.detailRowLabel);
            TextView textViewForValue = (TextView)tableRow.findViewById(R.id.detailRowValue);
            
            textViewForLabel.setText(moduleField.getLabel());
            String value = mCursor.getString(columnIndex);

            if (value != null && !value.equals("")) {
                textViewForValue.setText(value);
            } else {
                textViewForValue.setText(R.string.notAvailable);
            }
            
            mDetailsTable.addView(tableRow);

        }
    }
    
}
