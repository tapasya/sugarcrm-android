package com.imaginea.android.sugarcrm;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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

    private static final int FETCH_FAILED = 0;

    private static final int REFRESH_LIST = 0;

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

            int first = view.getFirstVisiblePosition();
            int count = view.getChildCount();
            for (int i = 0; i < count; i++) {
                TextView t = (TextView) view.getChildAt(i);
                if (t.getTag() != null) {
                    t.setText(mAdapter.getItem(first + i).getFieldValue(ModuleFields.NAME));
                    t.setTag(null);
                }
            }

            mStatus.setText("Idle");
            break;
        case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
            mBusy = true;
            mStatus.setText("Touch scroll");
            break;
        case OnScrollListener.SCROLL_STATE_FLING:
            mBusy = true;
            mStatus.setText("Fling");
            break;
        }
    }

    /**
     *
     */
    class LoadContactsTask extends AsyncTask<Void, Void, Object> {

        @Override
        protected Object doInBackground(Void... params) {
            try {
                String url = "http://192.168.2.245/sugarcrm/service/v2/rest.php";
                String sessionId = RestUtil.loginToSugarCRM(url, "will", Util.MD5("will"));

                String[] fields = new String[] {};
                // RestUtil.getModuleFields(url, mSessionId, moduleName, fields);
                String query = "", orderBy = "";
                int offset = 0;
                String[] selectFields = { ModuleFields.NAME, ModuleFields.EMAIL1 };
                String[] linkNameToFieldsArray = new String[] {};

                int maxResults = 10, deleted = 0;

                SugarBean[] sBeans = RestUtil.getEntryList(url, sessionId, RestUtilConstants.CONTACTS_MODULE, query, orderBy, offset, selectFields, linkNameToFieldsArray, maxResults, deleted);
                mAdapter.setSugarBeanArray(sBeans);

            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                return FETCH_FAILED;
            }

            return REFRESH_LIST;
        }

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            int retVal = (Integer) result;
            switch (retVal) {
            case FETCH_FAILED:

            default:
                setListAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        }

    }

    /**
     * Will not bind views while the list is scrolling
     * 
     */
    private class ContactsAdapter extends BaseAdapter {

        private SugarBean[] mSugarBeanArray;

        private LayoutInflater mInflater;

        public ContactsAdapter(Context context) {
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        /**
         * The number of items in the list is determined by the number of speeches in our array.
         * 
         * @see android.widget.ListAdapter#getCount()
         */
        public int getCount() {
            return mSugarBeanArray.length;
        }

        public void setSugarBeanArray(SugarBean[] sbArray) {
            mSugarBeanArray = sbArray;
        }

        /**
         * Since the data comes from an array, just returning the index is sufficent to get at the
         * data. If we were using a more complex data structure, we would return whatever object
         * represents one row in the list.
         * 
         * @see android.widget.ListAdapter#getItem(int)
         */
        public SugarBean getItem(int position) {
            return mSugarBeanArray[position];
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

            if (!mBusy) {
                text.setText(mSugarBeanArray[position].getFieldValue(ModuleFields.NAME));
                // Null tag means the view has the correct data
                text.setTag(null);
            } else {
                text.setText("Loading...");
                // Non-null tag means the view still needs to load it's data
                text.setTag(this);
            }

            return text;
        }

        /**
         * Remember our context so we can use it when constructing views.
         */
        private Context mContext;
    }
}
