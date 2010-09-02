package com.imaginea.android.sugarcrm;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.SugarBean;
import com.imaginea.android.sugarcrm.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * AccountListActivity, not used. See ContactListActivity
 * 
 * @author vasavi
 */
public class AccountListActivity extends ListActivity {

    public final static String LOG_TAG = "AccounttListActivity";

    private AccountsAdapter mAdapter;

    private boolean mBusy = false;

    private boolean mStopLoading = false;

    private LoadAccountsTask mTask;

    private ListView mListView;

    private View mEmpty;

    private TextView mStatus;

    private String mSessionId;

    private int mCurrentOffset = 0;

    private String[] mSelectFields = { ModuleFields.NAME, ModuleFields.EMAIL1 };

    private HashMap<String, List<String>> mLinkNameToFieldsArray = new HashMap<String, List<String>>();

    // we don't make this final as we may want to use the sugarCRM value dynamically
    public static int mMaxResults = 20;

    /** {@inheritDoc} */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.common_list);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String moduleName = "Contacts";
        if (extras != null)
            moduleName = extras.getString(RestUtilConstants.MODULE_NAME);

        TextView tv = (TextView) findViewById(R.id.headerText);
        tv.setText(Util.ACCOUNTS);
        mStatus = (TextView) findViewById(R.id.status);
        // mStatus.setText("Idle");

        // Use an existing ListAdapter that will map an array
        // of strings to TextViews
        mAdapter = new AccountsAdapter(this);
        mListView = getListView();

        // mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                // openDetailScreen(position);
                Log.i(LOG_TAG, "item " + position + " clicked!");
                openDetailScreen(position);
            }
        });

        // button code in the layout - 1.6 SDK feature to specify onClick
        mListView.setItemsCanFocus(true);
        mListView.setFocusable(true);
        mEmpty = findViewById(R.id.empty);
        mListView.setEmptyView(mEmpty);
        // registerForContextMenu(getListView());
        mTask = new LoadAccountsTask();
        mTask.execute(null);
    }

    /**
     * opens the Detail Screen
     * 
     * @param position
     */
    void openDetailScreen(int position) {
        Intent detailIntent = new Intent(AccountListActivity.this, AccountDetailsActivity.class);

        Cursor cursor = (Cursor) getListAdapter().getItem(position);
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }
        // SugarBean bean = (SugarBean) getListView().getItemAtPosition(position);
        Log.d(LOG_TAG, "beanId:" + cursor.getString(1));
        detailIntent.putExtra(RestUtilConstants.ID, cursor.getString(0));
        startActivity(detailIntent);
    }

    /** {@inheritDoc} */
    @Override
    protected void onPause() {
        super.onPause();

        // cancel the task if we are running
        if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
            mTask.cancel(true);
        }
    }

    void fetchMoreItemsForList() {
        if (mBusy && !mStopLoading) {

            /*
             * do not load the accounts again till the previous call has not finished, this ensures
             * that we have the sorting order in place as the tasks are asynchronous
             */
            if (mTask == null || (mTask != null && mTask.getStatus() == AsyncTask.Status.FINISHED)) {
                mTask = new LoadAccountsTask();
                mCurrentOffset = mCurrentOffset + mMaxResults;
                mTask.execute(mCurrentOffset);
            }
        }
    }

    /**
     * LoadAccountsTask
     */
    class LoadAccountsTask extends AsyncTask<Object, Void, Object> {

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
                String url = pref.getString("URL", getString(R.string.defaultUrl));
                String userName = pref.getString("USER_NAME", getString(R.string.defaultUser));
                String password = pref.getString("PASSWORD", getString(R.string.defaultPwd));
                Log.i(LOG_TAG, url + userName + password);
                // SugarCrmApp app =
                // mSessionId = ((SugarCrmApp) getApplication()).getSessionId();
                if (mSessionId == null) {
                    mSessionId = RestUtil.loginToSugarCRM(url, userName, password);
                }

                // RestUtil.getModuleFields(url, mSessionId, moduleName, fields);
                String query = "", orderBy = ModuleFields.NAME;

                int offset = 0;
                if (params != null)
                    offset = (Integer) params[0];

                int deleted = 0;

                SugarBean[] sBeans = RestUtil.getEntryList(url, mSessionId, Util.ACCOUNTS, query, orderBy, offset
                                                + "", mSelectFields, mLinkNameToFieldsArray, mMaxResults
                                                + "", deleted + "");
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

    /**
     * Will not bind views while the list is scrolling
     * 
     */
    private class AccountsAdapter extends BaseAdapter {

        /**
         * Remember our context so we can use it when constructing views.
         */
        private Context mContext;

        private ArrayList<SugarBean> sugarBeanList = new ArrayList<SugarBean>();

        private LayoutInflater mInflater;

        private TextView footer;

        public AccountsAdapter(Context context) {
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
            String name = bean.getFieldValue(ModuleFields.NAME);
            text.setText(name);
            return text;
        }
    }

}
