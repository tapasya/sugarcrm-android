package com.imaginea.android.sugarcrm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.util.ModuleField;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * AccountDetailsActivity
 * 
 * @author vasavi
 */
public class AccountDetailsActivity extends Activity {

    private Menu mMenu;
    
    private String mRowId;
    
    private String mSugarBeanId;
    
    private String mModuleName;

    private Cursor mCursor;    

    private String[] mSelectFields;

    private final String LOG_TAG = "AccountDetailsActivity";

    private TableLayout mDetailsTable;
    
    private String mAccountName = "";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.account_details);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        mRowId = (String) intent.getStringExtra(Util.ROW_ID);
        mSugarBeanId = (String) intent.getStringExtra(RestUtilConstants.BEAN_ID);
        mModuleName = "Contacts";
        if (extras != null)
            mModuleName = extras.getString(RestUtilConstants.MODULE_NAME);

        if (intent.getData() == null) {
            intent.setData(Uri.withAppendedPath(DatabaseHelper.getModuleUri(mModuleName), mRowId));
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
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuHelper.onPrepareOptionsMenu(this, menu, mModuleName);
        return super.onPrepareOptionsMenu(menu);
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Hold on to this
        mMenu = menu;

        // Inflate the currently selected menu XML resource.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu, menu);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.addItem:
            Intent myIntent = new Intent(AccountDetailsActivity.this, EditDetailsActivity.class);
            myIntent.putExtra(Util.ROW_ID, mRowId);
            myIntent.putExtra(RestUtilConstants.BEAN_ID, mSugarBeanId);
            myIntent.putExtra(RestUtilConstants.LINK_FIELD_NAME, "contacts");
            myIntent.putExtra(RestUtilConstants.MODULE_NAME, "Contacts");
            myIntent.putExtra(RestUtilConstants.PARENT_MODULE_NAME, "Accounts");
            myIntent.putExtra(ModuleFields.ACCOUNT_NAME, mAccountName);
            AccountDetailsActivity.this.startActivity(myIntent);
            return true;

        }
        return false;
    }

    private void setContents(String moduleName) {

        String[] detailsProjection = mSelectFields;

        mDetailsTable = (TableLayout) findViewById(R.id.accountDetalsTable);

        mCursor.moveToFirst();
        
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        for (int i = 2; i < detailsProjection.length-2; i++) {
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
            
            if(ModuleFields.ACCOUNT_NAME.equals(fieldName)){
                mAccountName = value;
            }
            
            if(moduleField.getType().equals("phone"))
                textViewForValue.setAutoLinkMask(Linkify.PHONE_NUMBERS);
            if (value != null && !value.equals("")) {
                textViewForValue.setText(value);             
            } else {
                textViewForValue.setText(R.string.notAvailable);
            }
            
            mDetailsTable.addView(tableRow);

        }
    }
    
}
