package com.imaginea.android.sugarcrm;

import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

/**
 * CRMContentObserver
 * 
 * @author chander
 *
 */
public class CRMContentObserver extends ContentObserver{

    public CRMContentObserver(Handler handler) {
        super(handler);       
    }

    @Override
    public void onChange(boolean selfChange) { 
        super.onChange(selfChange);
        Log.d("CRMCO", "onChange called");
    }

}
