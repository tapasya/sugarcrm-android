package com.imaginea.android.sugarcrm;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SettingsMenu extends Activity {
    private Menu mMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Hold on to this
        mMenu = menu;

        // Inflate the currently selected menu XML resource.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.settings_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.settings:
            Intent myIntent = new Intent(SettingsMenu.this, SugarCrmSettings.class);
            SettingsMenu.this.startActivity(myIntent);
            return true;

        }
        return false;
    }

}
