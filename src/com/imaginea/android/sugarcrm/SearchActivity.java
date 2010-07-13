package com.imaginea.android.sugarcrm;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AccountsColumns;

public class SearchActivity extends ListActivity {
    
    private static final String TAG = SearchableActivity.class.getSimpleName();

    private ListView mListView;

    private View mEmpty;
    
    private ProgressDialog progressDialog;
    
    private String mQuery = null;

    private String mModuleName = null;

    private String[] mModules = null;

    private int mOffset = 0;

    private int mMaxResults = 20;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        
        Intent intent = getIntent();
        mListView = getListView();
        mEmpty = findViewById(R.id.empty);
        mListView.setEmptyView(mEmpty);
        
        Bundle appData = intent.getBundleExtra(SearchManager.APP_DATA);
        if (appData != null) {
            mModuleName = appData.getString(RestUtilConstants.MODULE_NAME);
            mModules = appData.getStringArray(RestUtilConstants.MODULES);
            mOffset = appData.getInt(RestUtilConstants.OFFSET);
            mMaxResults = appData.getInt(RestUtilConstants.MAX_RESULTS);
        }
        
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            Log.i(TAG, "query - " + mQuery);
            showResults(mQuery);
        }
    }

    private void showResults(String query) {
        Uri moduleUri = DatabaseHelper.getModuleUri(mModuleName);
        if (getIntent().getData() == null) {
            getIntent().setData(moduleUri);
        }
        
        Cursor cursor = managedQuery(getIntent().getData(), DatabaseHelper.getModuleProjections(mModuleName), AccountsColumns.NAME + "=?" , new String[]{query}, null);
        GenericCursorAdapter adapter;
        String[] moduleSel = DatabaseHelper.getModuleListSelections(mModuleName);
        Log.i(TAG, "count - " + cursor.getCount() + " moduleSel.length - " + moduleSel.length);
        if (moduleSel.length >= 2)
            adapter = new GenericCursorAdapter(this, R.layout.contact_listitem, cursor, moduleSel, new int[] {
                    android.R.id.text1, android.R.id.text2 });
        else
            adapter = new GenericCursorAdapter(this, R.layout.contact_listitem, cursor, moduleSel, new int[] { android.R.id.text1 });
        setListAdapter(adapter);
        
        if (adapter.getCount() == 0)
            mListView.setVisibility(View.GONE);
    }
    
    
    /**
     * GenericCursorAdapter
     */
    private final class GenericCursorAdapter extends SimpleCursorAdapter {

        public GenericCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            Cursor cursor = getCursor();
            Log.d(TAG, " name : " + cursor.getString(cursor.getColumnIndex(AccountsColumns.NAME)));
            Log.d(TAG, "Get Item" + getItemId(position));
            return v;
        }
    }

}
