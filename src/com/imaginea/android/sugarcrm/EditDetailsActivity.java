package com.imaginea.android.sugarcrm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.util.ModuleField;
import com.imaginea.android.sugarcrm.util.Util;

import java.util.LinkedHashMap;
import java.util.Map;

public class EditDetailsActivity extends Activity {

    private final String TAG = "EditDetailsActivity";

    private int MODE = -1;

    private ViewGroup mDetailsTable;

    private Cursor mCursor;

    private String mSugarBeanId;

    private String mModuleName;

    private String mRowId;

    private String[] mSelectFields;

    private Uri mIntentUri;

    private DatabaseHelper mDbHelper;

    private LoadContentTask mTask;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_details);

        mDbHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        mModuleName = "Contacts";
        if (extras != null) {
            // i always get the module name
            mModuleName = extras.getString(RestUtilConstants.MODULE_NAME);

            mRowId = intent.getStringExtra(Util.ROW_ID);
            mSugarBeanId = intent.getStringExtra(RestUtilConstants.BEAN_ID);
        }
        // when the user comes from the relationships, intent.getData() won't be null
        mIntentUri = intent.getData();

        if (intent.getData() != null && mRowId != null) {
            // TODO: this case is intentionally left as of now
            // this case comes into picture only if the user can change the accountName to which it
            // is associated
            MODE = Util.EDIT_RELATIONSHIP_MODE;
        } else if (mRowId != null) {
            MODE = Util.EDIT_ORPHAN_MODE;
        } else if (intent.getData() != null) {
            MODE = Util.NEW_RELATIONSHIP_MODE;
        } else {
            MODE = Util.NEW_ORPHAN_MODE;
        }

        if (intent.getData() == null && MODE == Util.EDIT_ORPHAN_MODE) {
            intent.setData(Uri.withAppendedPath(mDbHelper.getModuleUri(mModuleName), mRowId));
        } else if (intent.getData() == null && MODE == Util.NEW_ORPHAN_MODE) {
            intent.setData(mDbHelper.getModuleUri(mModuleName));
        }

        mSelectFields = mDbHelper.getModuleProjections(mModuleName);

        /*
         * if (MODE == Util.EDIT_ORPHAN_MODE || MODE == Util.EDIT_RELATIONSHIP_MODE) { mCursor =
         * getContentResolver().query(getIntent().getData(), mSelectFields, null, null,
         * mDbHelper.getModuleSortOrder(mModuleName)); }
         */
        // startManagingCursor(mCursor);
        // setContents();

        mTask = new LoadContentTask();
        mTask.execute(null);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
            mTask.cancel(true);
        }
    }

    class LoadContentTask extends AsyncTask<Object, Object, Object> {

        int staticRowsCount;

        final int STATIC_ROW = 1;

        final int DYNAMIC_ROW = 2;

        final int SAVE_BUTTON = 3;

        final int INPUT_TYPE = 4;

        LoadContentTask() {
            mDetailsTable = (ViewGroup) findViewById(R.id.accountDetalsTable);

            // as the last child is the SAVE button, count - 1 has to be done.
            staticRowsCount = mDetailsTable.getChildCount() - 1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            TextView tv = (TextView) findViewById(R.id.headerText);
            if (MODE == Util.EDIT_ORPHAN_MODE || MODE == Util.EDIT_RELATIONSHIP_MODE) {
                tv.setText(String.format(getString(R.string.editDetailsHeader), mModuleName));
            } else if (MODE == Util.NEW_ORPHAN_MODE || MODE == Util.NEW_RELATIONSHIP_MODE) {
                tv.setText(String.format(getString(R.string.newDetailsHeader), mModuleName));
            }
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);

            switch ((Integer) values[0]) {

            case STATIC_ROW:
                View editRow = (View) values[1];
                editRow.setVisibility(View.VISIBLE);

                TextView labelView = (TextView) values[2];
                labelView.setText((String) values[3]);
                EditText valueView = (EditText) values[4];
                valueView.setText((String) values[5]);
                break;

            case DYNAMIC_ROW:
                editRow = (View) values[1];
                editRow.setVisibility(View.VISIBLE);

                labelView = (TextView) values[2];
                labelView.setText((String) values[3]);
                valueView = (EditText) values[4];
                valueView.setText((String) values[5]);
                mDetailsTable.addView(editRow);
                break;

            case INPUT_TYPE:
                valueView = (EditText) values[1];
                valueView.setInputType((Integer) values[2]);
                break;

            }
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                if (MODE == Util.EDIT_ORPHAN_MODE || MODE == Util.EDIT_RELATIONSHIP_MODE) {
                    mCursor = getContentResolver().query(getIntent().getData(), mSelectFields, null, null, mDbHelper.getModuleSortOrder(mModuleName));
                }
                setContents();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
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
                // set visibility for the SAVE button
                findViewById(R.id.save).setVisibility(View.VISIBLE);
                break;
            default:

            }
        }

        private void setContents() {

            String[] detailsProjection = mSelectFields;

            if (mDbHelper == null)
                mDbHelper = new DatabaseHelper(getBaseContext());

            if (MODE == Util.EDIT_ORPHAN_MODE || MODE == Util.EDIT_RELATIONSHIP_MODE) {
                if (!isCancelled()) {
                    mCursor.moveToFirst();
                    mSugarBeanId = mCursor.getString(1); // beanId has columnIndex 1
                }
            }

            LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            Map<String, ModuleField> fieldNameVsModuleField = mDbHelper.getModuleFields(mModuleName);

            // iterating from the 3rd element as the first two columns in the detail projection are
            // ROW_ID and BEAN_ID
            for (int i = 2; i < detailsProjection.length - 2; i++) {
                // if the task gets cancelled
                if (isCancelled())
                    break;

                String fieldName = detailsProjection[i];

                // get the attributes of the moduleField
                ModuleField moduleField = fieldNameVsModuleField.get(fieldName);

                // do not display account_name field, i.e. user cannot modify the account name
                if (!mModuleName.equals(getString(R.string.accounts))
                                                && ModuleFields.ACCOUNT_NAME.equals(fieldName))
                    continue;

                ViewGroup tableRow;
                TextView textViewForLabel;
                EditText editTextForValue;
                // first two columns in the detail projection are ROW_ID and BEAN_ID
                if (staticRowsCount > i - 2) {
                    tableRow = (ViewGroup) mDetailsTable.getChildAt(i - 2);
                    textViewForLabel = (TextView) tableRow.getChildAt(0);
                    editTextForValue = (EditText) tableRow.getChildAt(1);
                } else {
                    tableRow = (ViewGroup) inflater.inflate(R.layout.edit_table_row, null);
                    textViewForLabel = (TextView) tableRow.getChildAt(0);
                    editTextForValue = (EditText) tableRow.getChildAt(1);
                }

                String label;
                if (moduleField.isRequired()) {
                    label = moduleField.getLabel() + " *";
                } else {
                    label = moduleField.getLabel();
                }

                int command = STATIC_ROW;
                if (staticRowsCount < i - 2) {
                    command = DYNAMIC_ROW;
                }

                if (MODE == Util.EDIT_ORPHAN_MODE || MODE == Util.EDIT_RELATIONSHIP_MODE) {
                    String value = mCursor.getString(mCursor.getColumnIndex(fieldName));
                    if (!TextUtils.isEmpty(value)) {
                        publishProgress(command, tableRow, textViewForLabel, label, editTextForValue, value);
                    } else {
                        publishProgress(command, tableRow, textViewForLabel, label, editTextForValue, "");
                    }
                    setInputType(editTextForValue, moduleField);

                } else {
                    publishProgress(command, tableRow, textViewForLabel, label, editTextForValue, "");
                }
            }

        }

        /*
         * takes care of basic validation automatically for some fields
         */
        private void setInputType(TextView editTextForValue, ModuleField moduleField) {
            if (Log.isLoggable(TAG, Log.VERBOSE))
                Log.v(TAG, "ModuleField type:" + moduleField.getType());
            if (moduleField.getType().equals("phone")) {
                // editTextForValue.setInputType(InputType.TYPE_CLASS_PHONE);
                publishProgress(INPUT_TYPE, editTextForValue, InputType.TYPE_CLASS_PHONE);
            }
        }
    }

    /**
     * on click listener for saving a module item
     * 
     * @param v
     */
    public void saveModuleItem(View v) {
        String[] detailsProjection = mSelectFields;

        Map<String, String> modifiedValues = new LinkedHashMap<String, String>();
        if (MODE == Util.EDIT_ORPHAN_MODE || MODE == Util.EDIT_RELATIONSHIP_MODE)
            modifiedValues.put(RestUtilConstants.ID, mSugarBeanId);

        for (int i = 2; i < detailsProjection.length - 2; i++) {

            // do not display account_name field, i.e. user cannot modify the account name
            if (!mModuleName.equals(getString(R.string.accounts))
                                            && ModuleFields.ACCOUNT_NAME.equals(detailsProjection[i]))
                continue;

            // EditText editText = (EditText) mDetailsTable.findViewById(i);
            EditText editText = (EditText) ((ViewGroup) mDetailsTable.getChildAt(i - 2)).getChildAt(1);
            Log.i(TAG, detailsProjection[i] + " : " + editText.getText().toString());

            // TODO: validation

            // add the fieldName : fieldValue in the ContentValues
            modifiedValues.put(detailsProjection[i], editText.getText().toString());
        }

        if (MODE == Util.EDIT_ORPHAN_MODE || MODE == Util.EDIT_RELATIONSHIP_MODE) {
            ServiceHelper.startServiceForUpdate(getBaseContext(), Uri.withAppendedPath(mDbHelper.getModuleUri(mModuleName), mRowId), mModuleName, mSugarBeanId, modifiedValues);
        } else if (MODE == Util.NEW_RELATIONSHIP_MODE) {
            modifiedValues.put(ModuleFields.DELETED, Util.NEW_ITEM);
            ServiceHelper.startServiceForInsert(getBaseContext(), mIntentUri, mModuleName, modifiedValues);
        } else if (MODE == Util.NEW_ORPHAN_MODE) {
            modifiedValues.put(ModuleFields.DELETED, Util.NEW_ITEM);
            ServiceHelper.startServiceForInsert(getBaseContext(), mDbHelper.getModuleUri(mModuleName), mModuleName, modifiedValues);
        }

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Hold on to this
        // Inflate the currently selected menu XML resource.
        MenuItem item;
        item = menu.add(1, R.id.save, 1, R.string.save);
        item.setIcon(android.R.drawable.ic_menu_save);
        item.setAlphabeticShortcut('s');
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.save:
            saveModuleItem(null);
            return true;
        default:
            return true;
        }
        // return false;
    }

}
