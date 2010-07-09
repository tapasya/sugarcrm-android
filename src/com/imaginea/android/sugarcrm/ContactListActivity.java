package com.imaginea.android.sugarcrm;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Contacts;

/**
 * ContactListActivity
 * 
 * @author chander
 */
public class ContactListActivity extends ListActivity {

    private ListView mListView;

    private View mEmpty;

    private TextView mStatus;

    private boolean mBusy = false;

    private int mCurrentOffset = 0;

    private String mModuleName;

    private boolean mStopLoading = false;

    private String[] mSelectFields = { ModuleFields.FIRST_NAME, ModuleFields.LAST_NAME };

    private String[] mLinkNameToFieldsArray = new String[] {};

    // we don't make this final as we may want to use the sugarCRM value dynamically
    public static int mMaxResults = 20;

    public final static String LOG_TAG = "ContactListActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.common_list);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        mModuleName = "Contacts";
        if (extras != null)
            mModuleName = extras.getString(RestUtilConstants.MODULE_NAME);

        TextView tv = (TextView) findViewById(R.id.headerText);
        tv.setText(mModuleName);
        mStatus = (TextView) findViewById(R.id.status);

        mListView = getListView();
        // mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                openDetailScreen(position);
            }
        });

        // button code in the layout - 1.6 SDK feature to specify onClick
        mListView.setItemsCanFocus(true);
        mListView.setFocusable(true);
        mEmpty = findViewById(R.id.empty);
        mListView.setEmptyView(mEmpty);
        registerForContextMenu(getListView());

        // Perform a managed query. The Activity will handle closing and requerying the cursor
        // when needed.

        Log.d(LOG_TAG, "ModuleName" + mModuleName);
        if (intent.getData() == null) {
            intent.setData(DatabaseHelper.getModuleUri(mModuleName));
        }
        // TODO - optimize this, if we sync up a dataset, then no need to run detail projectio
        // nhere, just do a list projection
        Cursor cursor = managedQuery(getIntent().getData(), DatabaseHelper.getModuleProjections(mModuleName), null, null, DatabaseHelper.getModuleSortOrder(mModuleName));
        // CRMContentObserver observer = new CRMContentObserver()
        // cursor.registerContentObserver(observer);
        GenericCursorAdapter adapter;
        String[] moduleSel = DatabaseHelper.getModuleListSelections(mModuleName);
        if (moduleSel.length >= 2)
            adapter = new GenericCursorAdapter(this, R.layout.contact_listitem, cursor, moduleSel, new int[] {
                    android.R.id.text1, android.R.id.text2 });
        else
            adapter = new GenericCursorAdapter(this, R.layout.contact_listitem, cursor, moduleSel, new int[] { android.R.id.text1 });
        setListAdapter(adapter);

        if (adapter.getCount() == 0)
            mListView.setVisibility(View.GONE);
        mEmpty.findViewById(R.id.progress).setVisibility(View.VISIBLE);
        TextView tv1 = (TextView) (mEmpty.findViewById(R.id.mainText));
        tv1.setVisibility(View.GONE);
    }

    private final class GenericCursorAdapter extends SimpleCursorAdapter {

        private int realoffset = 0;

        private int limit = 20;

        public GenericCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = super.getView(position, convertView, parent);
            int count = getCursor().getCount();
            Log.d(LOG_TAG, "Get Item" + getItemId(position));
            if (!mBusy && position != 0 && position == count - 1) {
                mBusy = true;
                realoffset += count;
                Uri uri = getIntent().getData();
                Uri newUri = Uri.withAppendedPath(Contacts.CONTENT_URI, realoffset + "/" + limit);
                Log.d(LOG_TAG, "Changing cursor:" + newUri.toString());
                final Cursor cursor = managedQuery(newUri, Contacts.LIST_PROJECTION, null, null, Contacts.DEFAULT_SORT_ORDER);
                CRMContentObserver observer = new CRMContentObserver(new Handler() {

                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        Log.d(LOG_TAG, "Changing cursor: in handler");
                        if (cursor.getCount() < mMaxResults)
                            mStopLoading = true;
                        changeCursor(cursor);
                        mStatus.setVisibility(View.VISIBLE);
                        mBusy = false;
                    }
                });
                cursor.registerContentObserver(observer);
            }
            if (mBusy) {
                mStatus.setVisibility(View.VISIBLE);
                mStatus.setText("Loading...");
                // Non-null tag means the view still needs to load it's data
                // text.setTag(this);
            }
            return v;
        }
    }

    /**
     * opens the Detail Screen
     * 
     * @param position
     */
    void openDetailScreen(int position) {
        Intent detailIntent = new Intent(ContactListActivity.this, AccountDetailsActivity.class);

        Cursor cursor = (Cursor) getListAdapter().getItem(position);
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }
        // SugarBean bean = (SugarBean) getListView().getItemAtPosition(position);
        // TODO
        Log.d(LOG_TAG, "beanId:" + cursor.getString(1));
        detailIntent.putExtra(RestUtilConstants.ID, cursor.getString(0));
        detailIntent.putExtra(RestUtilConstants.MODULE_NAME, mModuleName);
        startActivity(detailIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    // We can stop loading once we do not get the
    // if (sBeans.length < mMaxResults)
    // mStopLoading = true;
    // @Override
    // protected void onPostExecute(Object result) {
    // super.onPostExecute(result);
    // if (isCancelled())
    // return;
    // int retVal = (Integer) result;
    //
    // switch (retVal) {
    // case Util.FETCH_FAILED:
    //
    // mEmpty.findViewById(R.id.progress).setVisibility(View.GONE);
    // TextView tv = (TextView) (mEmpty.findViewById(R.id.mainText));
    // tv.setVisibility(View.VISIBLE);
    // tv.setText(R.string.loadFailed);
    // TextView footer = (TextView) findViewById(R.id.status);
    // footer.setVisibility(View.VISIBLE);
    // footer.setText(R.string.loadFailed);
    //
    // break;
    //
    // case Util.REFRESH_LIST:
    // mBusy = false;
    // mStatus.setVisibility(View.GONE);
    // mListView.setVisibility(View.VISIBLE);
    // int firstPos = getListView().getFirstVisiblePosition();
    // setListAdapter(mAdapter);
    // getListView().setSelection(firstPos);
    // mAdapter.notifyDataSetChanged();
    // break;
    // default:
    //
    // }
    // }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(R.string.options);
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "Bad menuInfo", e);
            return;
        }

        menu.add(1, R.string.view, 2, R.string.view);

        menu.add(2, R.string.edit, 3, R.string.edit);
        menu.add(3, R.string.delete, 4, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info;

        try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "bad menuInfo", e);
            return false;
        }
        // ListView listView = getListView();

        int position = info.position;
        switch (item.getItemId()) {
        case R.string.view:
            openDetailScreen(position);
            return true;

        case R.string.edit:
            openDetailScreen(position);
            return true;

        case R.string.delete:

            return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
