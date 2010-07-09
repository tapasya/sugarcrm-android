package com.imaginea.android.sugarcrm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * DashboardActivity
 * 
 * @author Vasavi
 */
public class DashboardActivity extends Activity {

    private GridView mDashboard;

    // references to the module images
    private Integer[] mModuleThumbIds = { R.drawable.account, R.drawable.contact, R.drawable.lead,
            R.drawable.opportunity, R.drawable.setting };

    // reference to the module names
    private Integer[] mModuleNameIds = { R.string.accounts, R.string.contacts, R.string.leads,
            R.string.opportunities, R.string.settings };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        startActivityForResult(new Intent(this, WizardActivity.class), 0);

        setContentView(R.layout.dashboard_activity);
        TextView tv = (TextView) findViewById(R.id.headerText);
        tv.setText(R.string.home);
        mDashboard = (GridView) findViewById(R.id.dashboard);
        mDashboard.setAdapter(new AppsAdapter(this));

        // Activities corresponding to the items in the GridView
        final Class[] moduleActivities = { ContactListActivity.class, ContactListActivity.class,
                ContactListActivity.class, ContactListActivity.class, SugarCrmSettings.class };

        mDashboard.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // invoke the corresponding activity when the item in the GridView is clicked
                Intent myIntent = new Intent(DashboardActivity.this, moduleActivities[position]);
                myIntent.putExtra(RestUtilConstants.MODULE_NAME, getString(mModuleNameIds[position]));
                DashboardActivity.this.startActivity(myIntent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED)
            finish();
    }

    public class AppsAdapter extends BaseAdapter {
        private Context mContext;

        public AppsAdapter(Context context) {
            mContext = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view; // an item in the GridView
            if (convertView == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.dashboard_item, parent, false);

                ImageView iv = (ImageView) view.findViewById(R.id.moduleImage);
                iv.setImageResource(mModuleThumbIds[position]);

                TextView tv = (TextView) view.findViewById(R.id.moduleName);
                tv.setText(mModuleNameIds[position]);
            } else {
                view = (LinearLayout) convertView;
            }

            return view;
        }

        public final int getCount() {
            return mModuleThumbIds.length;
        }

        public final Object getItem(int position) {
            return null;
        }

        public final long getItemId(int position) {
            return 0;
        }
    }
}