package com.imaginea.android.sugarcrm;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.util.Util;

public class SearchActivity extends ListActivity {

    private static final String TAG = SearchActivity.class.getSimpleName();

    private ListView mListView;

    private View mEmpty;

    private Menu mMenu;

    private String mQuery = null;

    private String mModuleName = null;

    private String[] mModules = null;

    private int mOffset = 0;

    private int mMaxResults = 20;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_list);

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
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "query - " + mQuery);
            }
            showResults(mQuery);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // handles a click on a search suggestion; launches activity to show the bean
            Intent detailIntent = new Intent(this, AccountDetailsActivity.class);
            Log.i(TAG, "view uri - " + intent.getData());
            detailIntent.putExtra(Util.ROW_ID, intent.getData().getLastPathSegment());
            detailIntent.putExtra(RestUtilConstants.MODULE_NAME, Util.ACCOUNTS);
            detailIntent.setData(intent.getData());
            startActivity(detailIntent);
            finish();
        }
    }

    private void showResults(String query) {
        DatabaseHelper dbHelper = new DatabaseHelper(SearchActivity.this);
        Uri moduleUri = dbHelper.getModuleUri(mModuleName);
        if (getIntent().getData() == null) {
            getIntent().setData(moduleUri);
        }

        TextView tv = (TextView) findViewById(R.id.headerText);
        tv.setText(mModuleName + " : Search results");

        Cursor cursor = managedQuery(getIntent().getData(), dbHelper.getModuleProjections(mModuleName), dbHelper.getModuleSelection(mModuleName, query), null, null);

        // startManagingCursor(cursor);
        GenericCursorAdapter adapter;
        String[] moduleSel = dbHelper.getModuleListSelections(mModuleName);
        cursor.moveToFirst();
        if (moduleSel.length >= 2)
            adapter = new GenericCursorAdapter(this, R.layout.contact_listitem, cursor, moduleSel, new int[] {
                    android.R.id.text1, android.R.id.text2 });
        else
            adapter = new GenericCursorAdapter(this, R.layout.contact_listitem, cursor, moduleSel, new int[] { android.R.id.text1 });
        mListView.setAdapter(adapter);
        setListAdapter(adapter);

        if (adapter.getCount() == 0)
            mListView.setVisibility(View.GONE);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                openDetailScreen(position);
            }
        });
    }

    /**
     * opens the Detail Screen
     * 
     * @param position
     */
    void openDetailScreen(int position) {
        Intent detailIntent = new Intent(SearchActivity.this, AccountDetailsActivity.class);

        Cursor cursor = (Cursor) getListAdapter().getItem(position);
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }
        // SugarBean bean = (SugarBean) getListView().getItemAtPosition(position);
        // TODO
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "beanId:" + cursor.getString(1) + " rowId: " + cursor.getString(0));
        }
        detailIntent.putExtra(Util.ROW_ID, cursor.getString(0));
        detailIntent.putExtra(RestUtilConstants.MODULE_NAME, mModuleName);
        startActivity(detailIntent);
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
            return v;
        }
    }

}
