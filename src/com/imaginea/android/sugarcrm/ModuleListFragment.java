package com.imaginea.android.sugarcrm;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Contacts;


public class ModuleListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private View mEmpty;

    private View mListFooterView;

    private TextView mListFooterText;

    private View mListFooterProgress;
    
    private DatabaseHelper mDbHelper;

    private GenericCursorAdapter mAdapter;
    
    private boolean mBusy = false;
    
    private String mModuleName;

    private Uri mModuleUri;

    // private boolean mStopLoading = false;

    private Uri mIntentUri;

    private int mCurrentSelection;
    
    public final static String LOG_TAG = ModuleListFragment.class.getSimpleName();
    
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    /**
     * GenericCursorAdapter
     */
    private final class GenericCursorAdapter extends SimpleCursorAdapter implements Filterable {

        private int realoffset = 0;

        private int limit = 20;

        private ContentResolver mContent;
        
        

        public GenericCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
            mContent = context.getContentResolver();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = super.getView(position, convertView, parent);
            int count = getCursor().getCount();
            Log.d(LOG_TAG, "Get Item" + getItemId(position));
            if (!mBusy && position != 0 && position == count - 1) {
                mBusy = true;
                realoffset += count;
                // Uri uri = getIntent().getData();
                // TODO - fix this, this is no longer used
                Uri newUri = Uri.withAppendedPath(Contacts.CONTENT_URI, realoffset + "/" + limit);
                Log.d(LOG_TAG, "Changing cursor:" + newUri.toString());
                final Cursor cursor = getActivity().managedQuery(newUri, Contacts.LIST_PROJECTION, null, null, Contacts.DEFAULT_SORT_ORDER);
                CRMContentObserver observer = new CRMContentObserver(new Handler() {

                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        Log.d(LOG_TAG, "Changing cursor: in handler");
                        // if (cursor.getCount() < mMaxResults)
                        // mStopLoading = true;
                        changeCursor(cursor);
                        mListFooterText.setVisibility(View.GONE);
                        mListFooterProgress.setVisibility(View.GONE);
                        mBusy = false;
                    }
                });
                cursor.registerContentObserver(observer);
            }
            if (mBusy) {
                mListFooterProgress.setVisibility(View.VISIBLE);
                mListFooterText.setVisibility(View.VISIBLE);
                mListFooterText.setText("Loading...");
                // Non-null tag means the view still needs to load it's data
                // text.setTag(this);
            }
            return v;
        }

        @Override
        public String convertToString(Cursor cursor) {
            Log.i(LOG_TAG, "convertToString : " + cursor.getString(2));
            return cursor.getString(2);
        }

        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            if (getFilterQueryProvider() != null) {
                return getFilterQueryProvider().runQuery(constraint);
            }

            StringBuilder buffer = null;
            String[] args = null;
            if (constraint != null) {
                buffer = new StringBuilder();
                buffer.append("UPPER(");
                buffer.append(mDbHelper.getModuleListSelections(mModuleName)[0]);
                buffer.append(") GLOB ?");
                args = new String[] { constraint.toString().toUpperCase() + "*" };
            }

            return mContent.query(mDbHelper.getModuleUri(mModuleName), mDbHelper.getModuleListProjections(mModuleName), buffer == null ? null
                                            : buffer.toString(), args, mDbHelper.getModuleSortOrder(mModuleName));
        }
    }
    
    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
        
    }

}
