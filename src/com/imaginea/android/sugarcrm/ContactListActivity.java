package com.imaginea.android.sugarcrm;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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

    private TextView mStatus;

    private boolean mBusy = false;

    private LoadContactsTask mTask;

    private int mCurrentOffset = 0;

    private String mSessionId;

    private boolean mStopLoading = false;

    // we don't make this final as we may want to use the sugarCRM value dynamically
    public static int mMaxResults = 20;

    public static final int FETCH_FAILED = 0;

    public static final int REFRESH_LIST = 1;

    public final static String LOG_TAG = "ContactListActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_list);
        mStatus = (TextView) findViewById(R.id.status);
        mStatus.setText("Idle");

        // Use an existing ListAdapter that will map an array
        // of strings to TextViews
        mAdapter = new ContactsAdapter(this);

        getListView().setOnScrollListener(this);
        mTask = new LoadContactsTask();
        mTask.execute(null);
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
     *
     */
    class LoadContactsTask extends AsyncTask<Object, Void, Object> {

        @Override
        protected Object doInBackground(Object... params) {
            try {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                // TODO use a constant and remove this as we start from the login screen
                String url = pref.getString("URL", getString(R.string.default_url));
                String userName = pref.getString("USER_NAME", getString(R.string.default_username));
                String password = pref.getString("PASSWORD", getString(R.string.default_password));
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
                String[] selectFields = { ModuleFields.FIRST_NAME, ModuleFields.LAST_NAME,
                        ModuleFields.EMAIL1 };
                String[] linkNameToFieldsArray = new String[] {};

                int deleted = 0;

                SugarBean[] sBeans = RestUtil.getEntryList(url, mSessionId, RestUtilConstants.CONTACTS_MODULE, query, orderBy, offset, selectFields, linkNameToFieldsArray, mMaxResults, deleted);
                mAdapter.setSugarBeanArray(sBeans);
                // We can stop loading once we do not get the
                if (sBeans.length < mMaxResults)
                    mStopLoading = true;

            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                return FETCH_FAILED;
            }

            return REFRESH_LIST;
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
            case FETCH_FAILED:

            default:
                int firstPos = getListView().getFirstVisiblePosition();
                setListAdapter(mAdapter);
                getListView().setSelection(firstPos);
                mAdapter.notifyDataSetChanged();
            }
        }

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
