package com.imaginea.android.sugarcrm;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.SugarBean;
import com.imaginea.android.sugarcrm.util.Util;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * ContactListActivity
 * 
 * @author chander
 */
public class ContactListActivity extends ListActivity implements ListView.OnScrollListener {

    private ContactsAdapter mAdapter;

    private ListView mListView;

    private View mEmpty;

    private TextView mStatus;

    private boolean mBusy = false;

    private LoadContactsTask mTask;

    private int mCurrentOffset = 0;

    private String mSessionId;

    private boolean mStopLoading = false;

    private String[] mSelectFields = { ModuleFields.FIRST_NAME, ModuleFields.LAST_NAME,
            ModuleFields.EMAIL1 };

    private String[] mLinkNameToFieldsArray = new String[] {};

    // we don't make this final as we may want to use the sugarCRM value dynamically
    public static int mMaxResults = 20;

    public final static String LOG_TAG = "ContactListActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_list);
        mStatus = (TextView) findViewById(R.id.status);
        // mStatus.setText("Idle");

        // Use an existing ListAdapter that will map an array
        // of strings to TextViews
        mAdapter = new ContactsAdapter(this);
        mListView = getListView();

        mListView.setOnScrollListener(this);
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
        mTask = new LoadContactsTask();
        mTask.execute(null);
    }

    /**
     * opens the Detail Screen
     * 
     * @param position
     */
    void openDetailScreen(int position) {
        Intent detailIntent = new Intent(ContactListActivity.this, ContactDetailsActivity.class);
        SugarBean bean = (SugarBean) getListView().getItemAtPosition(position);
        Log.d(LOG_TAG, "beanId:" + bean.getBeanId());
        detailIntent.putExtra(RestUtilConstants.ID, bean.getBeanId());
        startActivity(detailIntent);
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                    int totalItemCount) {

    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
        case OnScrollListener.SCROLL_STATE_IDLE:
            mBusy = false;
            mStatus.setVisibility(View.GONE);
            break;
        case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
            mBusy = true;
            // mStatus.setText("Touch scroll");
            break;
        case OnScrollListener.SCROLL_STATE_FLING:
            mBusy = true;
            // mStatus.setText("Fling");
            break;
        }
        fetchMoreItemsForList();
    }

    void fetchMoreItemsForList() {
        if (mBusy && !mStopLoading) {
            // int last = view.getLastVisiblePosition();

            /*
             * do not load the contacts again till the previous call has not finished, this ensures
             * that we have the sorting order in place as the tasks are asynchronous
             */
            if (mTask == null || (mTask != null && mTask.getStatus() == AsyncTask.Status.FINISHED)) {
                mTask = new LoadContactsTask();
                mCurrentOffset = mCurrentOffset + mMaxResults;
                mTask.execute(mCurrentOffset);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // cancel the task if we are running
        if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
            mTask.cancel(true);
        }
    }

    /**
     * LoadContactsTask
     */
    class LoadContactsTask extends AsyncTask<Object, Void, Object> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mAdapter.getCount() == 0)
                mListView.setVisibility(View.GONE);
            mEmpty.findViewById(R.id.progress).setVisibility(View.VISIBLE);
            TextView tv = (TextView) (mEmpty.findViewById(R.id.mainText));
            tv.setVisibility(View.GONE);
            // tv.setText(R.string.loading);

            // we are reusing - so remove the other views

        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                // TODO use a constant and remove this as we start from the login screen
                String url = pref.getString("URL", getString(R.string.defaultUrl));
                String userName = pref.getString("USER_NAME", getString(R.string.defaultUser));
                String password = pref.getString("PASSWORD", getString(R.string.defaultPwd));
                Log.i(LOG_TAG, url + userName + password);
                // SugarCrmApp app =
                // mSessionId = ((SugarCrmApp) getApplication()).getSessionId();
                if (mSessionId == null) {
                    mSessionId = RestUtil.loginToSugarCRM(url, userName, Util.MD5(password));
                }

                String[] fields = new String[] {};
                // RestUtil.getModuleFields(url, mSessionId, moduleName, fields);
                String query = "", orderBy = ModuleFields.FIRST_NAME;

                int offset = 0;
                if (params != null)
                    offset = (Integer) params[0];

                int deleted = 0;

                SugarBean[] sBeans = RestUtil.getEntryList(url, mSessionId, RestUtilConstants.CONTACTS_MODULE, query, orderBy, offset, mSelectFields, mLinkNameToFieldsArray, mMaxResults, deleted);
                mAdapter.setSugarBeanArray(sBeans);
                // We can stop loading once we do not get the
                if (sBeans.length < mMaxResults)
                    mStopLoading = true;

            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                return Util.FETCH_FAILED;
            }

            return Util.REFRESH_LIST;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (isCancelled())
                return;
            int retVal = (Integer) result;

            switch (retVal) {
            case Util.FETCH_FAILED:

                mEmpty.findViewById(R.id.progress).setVisibility(View.GONE);
                TextView tv = (TextView) (mEmpty.findViewById(R.id.mainText));
                tv.setVisibility(View.VISIBLE);
                tv.setText(R.string.loadFailed);
                TextView footer = (TextView) findViewById(R.id.status);
                footer.setVisibility(View.VISIBLE);
                footer.setText(R.string.loadFailed);

                break;

            case Util.REFRESH_LIST:
                mBusy = false;
                mStatus.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                int firstPos = getListView().getFirstVisiblePosition();
                setListAdapter(mAdapter);
                getListView().setSelection(firstPos);
                mAdapter.notifyDataSetChanged();
                break;
            default:

            }
        }

    }

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
            // Launch activity to insert a new item
            // use the id for multiple dialog display..not used here as packed
            // position is long and cannot be sent
            // showDialog(R.string.addErrand);
            // adapter.getView(position, null, null);
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

    /**
     * Will not bind views while the list is scrolling
     * 
     */
    private class ContactsAdapter extends BaseAdapter {

        /**
         * Remember our context so we can use it when constructing views.
         */
        private Context mContext;

        private ArrayList<SugarBean> sugarBeanList = new ArrayList<SugarBean>();

        private LayoutInflater mInflater;

        private TextView footer;

        public ContactsAdapter(Context context) {
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            footer = (TextView) findViewById(R.id.status);
        }

        /**
         * The number of items in the list is determined by the number of speeches in our array.
         * 
         * @see android.widget.ListAdapter#getCount()
         */
        public int getCount() {
            return sugarBeanList.size();
        }

        public void setSugarBeanArray(SugarBean[] sbArray) {
            sugarBeanList.addAll(Arrays.asList(sbArray));
        }

        /**
         * Since the data comes from an array, just returning the index is sufficient to get at the
         * data. If we were using a more complex data structure, we would return whatever object
         * represents one row in the list.
         * 
         * @see android.widget.ListAdapter#getItem(int)
         */
        public SugarBean getItem(int position) {
            return sugarBeanList.get(position);
        }

        /**
         * Use the array index as a unique id.
         * 
         * @see android.widget.ListAdapter#getItemId(int)
         */
        public long getItemId(int position) {
            return position;
        }

        /**
         * Make a view to hold each row.
         * 
         * @see android.widget.ListAdapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView text;

            if (convertView == null) {
                text = (TextView) mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                text = (TextView) convertView;
            }

            if (position == getCount() - 1) {
                Log.d(LOG_TAG, "last item:" + position);
                mBusy = true;
                fetchMoreItemsForList();
            }

            if (mBusy) {
                footer.setVisibility(View.VISIBLE);
                footer.setText("Loading...");
                // Non-null tag means the view still needs to load it's data
                // text.setTag(this);
            }
            SugarBean bean = sugarBeanList.get(position);
            String firstName = bean.getFieldValue(ModuleFields.FIRST_NAME);
            String lastName = bean.getFieldValue(ModuleFields.LAST_NAME);
            text.setText(firstName + " " + lastName);
            return text;
        }
    }
}
