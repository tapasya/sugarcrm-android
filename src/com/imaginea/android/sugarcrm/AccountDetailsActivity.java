package com.imaginea.android.sugarcrm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.util.ModuleField;
import com.imaginea.android.sugarcrm.util.Util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    private ViewGroup mDetailsTable;

    private String[] mRelationshipModules;

    private DatabaseHelper mDbHelper;

    private LoadContentTask mTask;

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

        mDbHelper = new DatabaseHelper(getBaseContext());
        if (intent.getData() == null) {
            intent.setData(Uri.withAppendedPath(mDbHelper.getModuleUri(mModuleName), mRowId));
        }
        mSelectFields = DatabaseHelper.getModuleProjections(mModuleName);
        // mCursor = getContentResolver().query(getIntent().getData(), mSelectFields, null, null,
        // mDbHelper.getModuleSortOrder(mModuleName));
        // startManagingCursor(mCursor);
        // setContents();

        mRelationshipModules = mDbHelper.getModuleRelationshipItems(mModuleName);

        // ListView listView = (ListView) findViewById(android.R.id.list);
        // listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        // @Override
        // public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
        // if (Log.isLoggable(LOG_TAG, Log.INFO)) {
        // Log.i(LOG_TAG, "clicked on " + mRelationshipModules[position]);
        // }
        // openListScreen(mRelationshipModules[position]);
        // }
        // });

        /*
         * RelationshipAdapter adapter = new RelationshipAdapter(this);
         * adapter.setRelationshipArray(mRelationshipModules); listView.setAdapter(adapter);
         */
        mTask = new LoadContentTask();
        mTask.execute(null);
    }

    protected void openListScreen(String moduleName) {
        // if (mModuleName.equals("Accounts")) {
        Intent detailIntent = new Intent(AccountDetailsActivity.this, ContactListActivity.class);
        if (mDbHelper == null)
            mDbHelper = new DatabaseHelper(getBaseContext());
        Uri uri = Uri.withAppendedPath(mDbHelper.getModuleUri(mModuleName), mRowId);
        uri = Uri.withAppendedPath(uri, moduleName);
        detailIntent.setData(uri);
        detailIntent.putExtra(RestUtilConstants.MODULE_NAME, moduleName);
        detailIntent.putExtra(RestUtilConstants.BEAN_ID, mSugarBeanId);
        startActivity(detailIntent);
        // } else {
        // Toast.makeText(this, "Not yet supported!", Toast.LENGTH_SHORT).show();
        // }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
            mTask.cancel(true);
        }
    }

    /*
     * @Override public boolean onPrepareOptionsMenu(Menu menu) {
     * MenuHelper.onPrepareOptionsMenu(this, menu, mModuleName); return
     * super.onPrepareOptionsMenu(menu); }
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Hold on to this
        mMenu = menu;

        // Inflate the currently selected menu XML resource.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_activity_menu, menu);

        SubMenu relationshipMenu = menu.addSubMenu(0, R.string.related, 0, getString(R.string.related));
        for (int i = 0; i < mRelationshipModules.length; i++) {
            relationshipMenu.add(0, Menu.FIRST + i, 0, mRelationshipModules[i]);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.home:
            Intent myIntent = new Intent(AccountDetailsActivity.this, DashboardActivity.class);
            AccountDetailsActivity.this.startActivity(myIntent);
            return true;
        case R.string.related:
            return true;
        default:
            if (Log.isLoggable(LOG_TAG, Log.INFO)) {
                Log.i(LOG_TAG, "item id : " + item.getItemId());
            }

            // if (mModuleName.equals("Accounts")) {
            openListScreen(mRelationshipModules[item.getItemId() - 1]);
            // } else {
            // Toast.makeText(this, "Not yet supported!", Toast.LENGTH_SHORT).show();
            // }
            return true;
        }
        // return false;
    }

    class LoadContentTask extends AsyncTask<Object, Object, Object> {

        int staticRowsCount;

        final int HEADER = 1;

        final int STATIC_ROW = 2;

        final int DYNAMIC_ROW = 3;

        LoadContentTask() {
            mDetailsTable = (ViewGroup) findViewById(R.id.accountDetalsTable);

            // as the last child is the SAVE button, count - 1 has to be done.
            staticRowsCount = mDetailsTable.getChildCount() - 1;
        }

        @Override
        protected void onPreExecute() {
            mCursor = getContentResolver().query(getIntent().getData(), mSelectFields, null, null, mDbHelper.getModuleSortOrder(mModuleName));
            super.onPreExecute();
            TextView tv = (TextView) findViewById(R.id.headerText);
            tv.setText(mModuleName + " Details");
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);

            switch ((Integer) values[0]) {

            case HEADER:
                TextView titleView = (TextView) values[1];
                titleView.setText((String) values[2]);
                break;

            case STATIC_ROW:
                // TODO: order
                View detailRow = (View) values[1];
                detailRow.setVisibility(View.VISIBLE);

                TextView labelView = (TextView) values[2];
                labelView.setText((String) values[3]);
                TextView valueView = (TextView) values[4];
                valueView.setText((String) values[5]);
                break;

            case DYNAMIC_ROW:
                detailRow = (View) values[1];
                detailRow.setVisibility(View.VISIBLE);

                labelView = (TextView) values[2];
                labelView.setText((String) values[3]);
                valueView = (TextView) values[4];
                valueView.setText((String) values[5]);
                mDetailsTable.addView(detailRow);
                break;
            }
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                setContents();
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                return Util.FETCH_FAILED;
            }

            return Util.FETCH_SUCCESS;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            // close the cursor irrespective of the result
            if (mCursor != null && !mCursor.isClosed())
                mCursor.close();

            if (isCancelled())
                return;
            int retVal = (Integer) result;
            switch (retVal) {
            case Util.FETCH_FAILED:
                break;
            case Util.FETCH_SUCCESS:
                break;
            default:

            }
        }

        private void setContents() {

            String[] detailsProjection = mSelectFields;

            if (mDbHelper == null)
                mDbHelper = new DatabaseHelper(getBaseContext());

            TextView textViewForTitle = (TextView) findViewById(R.id.accountName);
            String title = "";
            List<String> titleFields = Arrays.asList(mDbHelper.getModuleListSelections(mModuleName));

            mCursor.moveToFirst();

            LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // List<String> billingAddressGroup = mDbHelper.getBillingAddressGroup();

            // List<String> shippingAddressGroup = mDbHelper.getShippingAddressGroup();

            String value = "";
            Map<String, ModuleField> fieldNameVsModuleField = mDbHelper.getModuleFields(mModuleName);

            // LinearLayout tableRow = (LinearLayout)inflater.inflate(R.layout.table_row, null);

            // iterating from the 3rd element as the first two columns in the detail projection are
            // ROW_ID and BEAN_ID
            for (int i = 2; i < detailsProjection.length - 2; i++) {
                String fieldName = detailsProjection[i];
                int columnIndex = mCursor.getColumnIndex(fieldName);
                if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                    Log.d(LOG_TAG, "Col:" + columnIndex + " moduleName : " + mModuleName
                                                    + " fieldName : " + fieldName);
                }

                String tempValue = mCursor.getString(columnIndex);

                // get the attributes of the moduleField
                ModuleField moduleField = fieldNameVsModuleField.get(fieldName);

                ViewGroup tableRow;
                TextView textViewForLabel;
                TextView textViewForValue;
                // first two columns in the detail projection are ROW_ID and BEAN_ID
                if (staticRowsCount > i - 2) {
                    tableRow = (ViewGroup) mDetailsTable.getChildAt(i - 2);
                    textViewForLabel = (TextView) tableRow.getChildAt(0);
                    textViewForValue = (TextView) tableRow.getChildAt(1);
                } else {
                    tableRow = (ViewGroup) inflater.inflate(R.layout.table_row, null);
                    textViewForLabel = (TextView) tableRow.getChildAt(0);
                    textViewForValue = (TextView) tableRow.getChildAt(1);
                }

                // set the title
                if (titleFields.contains(fieldName)) {
                    title = title + tempValue + " ";
                    publishProgress(HEADER, textViewForTitle, title);
                    continue;
                }

                // group billing address n shopping address
                // if(moduleName.equals(getString(R.string.accounts))){
                // if(moduleName.equals("Accounts")){
                // if(billingAddressGroup.contains(fieldName)){
                // if(!fieldName.equals(ModuleFields.BILLING_ADDRESS_COUNTRY)){
                // continue;
                // } else{
                // textViewForLabel.setText("Billing Address:");
                // }
                // } else if(shippingAddressGroup.contains(fieldName)){
                // if(!fieldName.equals(ModuleFields.SHIPPING_ADDRESS_COUNTRY)){
                // continue;
                // } else{
                // textViewForLabel.setText("Shipping Address:");
                // textViewForValue.setMaxLines(3);
                // }
                // }else{
                // value = tempValue;
                // }
                // } else{
                // value = tempValue;
                // }

                value = tempValue;
                if (moduleField.getType().equals("phone"))
                    textViewForValue.setAutoLinkMask(Linkify.PHONE_NUMBERS);

                int command = STATIC_ROW;
                if (staticRowsCount < i - 2) {
                    command = DYNAMIC_ROW;
                }

                if (!TextUtils.isEmpty(value)) {
                    // textViewForValue.setText(value);
                    publishProgress(command, tableRow, textViewForLabel, moduleField.getLabel(), textViewForValue, value);
                } else {
                    // textViewForValue.setText(R.string.notAvailable);
                    publishProgress(command, tableRow, textViewForLabel, moduleField.getLabel(), textViewForValue, getString(R.string.notAvailable));
                }

                // mDetailsTable.addView(tableRow);

            }
        }
    }

    /*
     * private class RelationshipAdapter extends BaseAdapter {
     * 
     * private Context mContext;
     * 
     * private String[] relationships;
     * 
     * private LayoutInflater mInflater;
     * 
     * public RelationshipAdapter(Context context) { mContext = context; mInflater =
     * (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); }
     * 
     * public void setRelationshipArray(String[] relationshipArray) { relationships =
     * relationshipArray; }
     * 
     * @Override public int getCount() { return relationships.length; }
     * 
     * @Override public Object getItem(int position) { return relationships[position]; }
     * 
     * @Override public long getItemId(int position) { return position; }
     * 
     * @Override public View getView(int position, View convertView, ViewGroup parent) { View layout
     * = mInflater.inflate(R.layout.contact_listitem, null); TextView tv = ((TextView)
     * layout.findViewById(android.R.id.text1)); tv.setText(relationships[position]); // TODO:
     * either set the correct images or remove the image
     * tv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.contacts),
     * null, null, null); return layout; }
     * 
     * }
     */

}
