package com.imaginea.android.sugarcrm;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Helper class to prepare the menu options for some module specific actions
 * 
 * @author chander
 */
public class MenuHelper {

    public static void onPrepareOptionsMenu(Activity activity, Menu menu, String moduleName) {
        MenuItem menuItem;

        if (activity instanceof ContactListActivity) {
            if (moduleName.equals(activity.getString(R.string.accounts))) {
                menuItem = menu.findItem(R.id.search);
                menuItem.setEnabled(true);
                menuItem.setVisible(true);

            } else {
                menuItem = menu.findItem(R.id.addItem);
                menuItem.setEnabled(false);
                menuItem.setVisible(false);
            }
        } else {

        }

    }

    /**
     * not used
     * 
     * @param menu
     * @return
     */
    public static boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    public static boolean onOptionsItemSelected(Activity activity, MenuItem item) {
        switch (item.getItemId()) {

        }
        return false;
    }
}
