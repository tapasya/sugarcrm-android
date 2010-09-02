package com.imaginea.android.sugarcrm;

import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

/**
 * CRMContentObserver
 * 
 * @author chander
 */
public class CRMContentObserver extends ContentObserver {

    /**
     * <p>
     * Constructor for CRMContentObserver.
     * </p>
     * 
     * @param handler
     *            a {@link android.os.Handler} object.
     */
    public CRMContentObserver(Handler handler) {
        super(handler);
    }

    /** {@inheritDoc} */
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.d("CRMCO", "onChange called");
    }

}
