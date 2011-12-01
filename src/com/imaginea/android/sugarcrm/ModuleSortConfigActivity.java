package com.imaginea.android.sugarcrm;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.util.ModuleField;
import com.imaginea.android.sugarcrm.util.ViewUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * ModuleSortConfigActivity class.
 * </p>
 * 
 */
public class ModuleSortConfigActivity extends Activity {

    private final String TAG = ModuleSortConfigActivity.class.getSimpleName();

    private DatabaseHelper mDbHelper;

    private TextView mHeaderTextView;

    private Spinner mModuleNameSpinner;

    private Spinner mFieldNameSpinner;

    private Spinner mSortOrderSpinner;

    private int selectedModuleIndex;

    private int selectedFieldIndex;

    private int selectedOrderIndex;

    private Map<String, String> fieldMap = new HashMap<String, String>();

    private Map<String, String> orderMap = new HashMap<String, String>();;

    private SugarCrmApp app;

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup layout
        setContentView(R.layout.module_sort_config);
        mHeaderTextView = (TextView) findViewById(R.id.headerText);
        mHeaderTextView.setText(R.string.sortSettings);

        mDbHelper = new DatabaseHelper(this);
        app = (SugarCrmApp) getApplication();

        // get the modules that are displayed in the dashboard
        List<String> moduleList = mDbHelper.getModuleList();
        final String[] moduleNames = new String[moduleList.size()];
        moduleList.toArray(moduleNames);

        ViewGroup linearLayout = (ViewGroup) findViewById(R.id.container);
        mModuleNameSpinner = (Spinner) ((ViewGroup) linearLayout.getChildAt(0)).getChildAt(1);
        mFieldNameSpinner = (Spinner) ((ViewGroup) linearLayout.getChildAt(1)).getChildAt(1);
        mSortOrderSpinner = (Spinner) ((ViewGroup) linearLayout.getChildAt(2)).getChildAt(1);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, moduleNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mModuleNameSpinner.setAdapter(adapter);

        // disable the fieldname spinner until the user selects the module
        mFieldNameSpinner.setEnabled(false);

        // order vs sql syntax
        orderMap.put(getString(R.string.ascending), "ASC");
        orderMap.put(getString(R.string.descending), "DESC");

        adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, new String[] {
                getString(R.string.ascending), getString(R.string.descending) });
        mSortOrderSpinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // disable the sortOrder spinner until the user selects the fieldName
        mSortOrderSpinner.setEnabled(false);

        mModuleNameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // store the selected module index
                selectedModuleIndex = position;

                String moduleName = moduleNames[position];
                // get the fields that are in the LIST_PROJECTION of the module
                String[] moduleFields = mDbHelper.getModuleListSelections(moduleName);
                // get the ModuleField objects for the module
                Map<String, ModuleField> map = mDbHelper.getModuleFields(moduleName);
                // get the labels of the module fields to display
                String[] moduleFieldsChoice = new String[moduleFields.length];
                for (int i = 0; i < moduleFields.length; i++) {
                    // add the module field label to be displayed in the choice
                    // menu
                    ModuleField modField = map.get(moduleFields[i]);
                    if (modField != null) {
                        moduleFieldsChoice[i] = modField.getLabel();
                        // fieldMap: label vs name
                        fieldMap.put(moduleFieldsChoice[i], moduleFields[i]);
                    } else
                        moduleFieldsChoice[i] = "";

                    if (moduleFieldsChoice[i].indexOf(":") > 0) {
                        moduleFieldsChoice[i] = moduleFieldsChoice[i].substring(0, moduleFieldsChoice[i].length() - 1);
                        fieldMap.put(moduleFieldsChoice[i], moduleFields[i]);
                    }
                }
                ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getBaseContext(), android.R.layout.simple_spinner_item, moduleFieldsChoice);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mFieldNameSpinner.setAdapter(adapter);
                mFieldNameSpinner.setEnabled(true);
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mFieldNameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // store the selected field index
                selectedFieldIndex = position;
                mSortOrderSpinner.setEnabled(true);
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSortOrderSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // store the selected order index
                selectedOrderIndex = position;
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /** {@inheritDoc} */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /** {@inheritDoc} */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * <p>
     * saveSortOrder
     * </p>
     * 
     * @param view
     *            a {@link android.view.View} object.
     */
    public void saveSortOrder(View view) {
        Log.i(TAG, "saveSortOrder : ");

        String module = (String) mModuleNameSpinner.getAdapter().getItem(selectedModuleIndex);
        String fieldName = fieldMap.get(mFieldNameSpinner.getAdapter().getItem(selectedFieldIndex));
        String sortBy = orderMap.get(mSortOrderSpinner.getAdapter().getItem(selectedOrderIndex));
        Log.i(TAG, "module : " + module + " field : " + fieldName + " order : " + sortBy);

        app.setModuleSortOrder(module, fieldName, sortBy);

        ViewUtil.makeToast(getBaseContext(), R.string.settingsSaved);
    }

}
