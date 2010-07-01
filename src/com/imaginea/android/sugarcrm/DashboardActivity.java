package com.imaginea.android.sugarcrm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DashboardActivity extends Activity {

    GridView dashboard;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dashboard_activity);
        dashboard = (GridView) findViewById(R.id.dashboard);
        dashboard.setAdapter(new AppsAdapter(this));

        // Activities corresponding to the items in the GridView
        final Class[] moduleActivities = { AccountsActivity.class, ContactListActivity.class,
                LeadsActivity.class, AccountsActivity.class, SugarCrmSettings.class };

        dashboard.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // invoke the corresponding activity when the item in the GridView is clicked
                Intent myIntent = new Intent(DashboardActivity.this, moduleActivities[position]);
                DashboardActivity.this.startActivity(myIntent);
            }
        });

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

        // references to the module images
        private Integer[] mModuleThumbIds = { R.drawable.account, R.drawable.contact,
                R.drawable.lead, R.drawable.opportunity, R.drawable.setting };

        // reference to the module names
        private Integer[] mModuleNameIds = { R.string.accounts, R.string.contacts, R.string.leads,
                R.string.opportunities, R.string.settings };

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