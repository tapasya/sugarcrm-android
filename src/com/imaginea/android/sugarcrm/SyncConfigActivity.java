package com.imaginea.android.sugarcrm;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.provider.SugarCRMProvider;
import com.imaginea.android.sugarcrm.util.Util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * SyncConfigActivity
 * 
 * @author chander
 * 
 */
public class SyncConfigActivity extends Activity {

    private TextView mHeaderTextView;

    private TextView mTitleTextView;

    private Button mStartDateButton;

    private Button mEndDateButton;

    // cache the time
    private Date mStartTime;

    private Date mEndTime;

    public static final long THREE_MONTHS = 3 * 30 * 24 * 60 * 60 * 1000L;

    public static final String TAG = SyncConfigActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup layout
        setContentView(R.layout.sync_config);
        mHeaderTextView = (TextView) findViewById(R.id.headerText);
        mHeaderTextView.setText(R.string.syncSettings);
        // mTitleTextView = (TextView) findViewById(R.id.title);
        mStartDateButton = (Button) findViewById(R.id.start_date);
        mEndDateButton = (Button) findViewById(R.id.end_date);

        long time = System.currentTimeMillis();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        long startTime = pref.getLong(Util.PREF_SYNC_START_TIME, time - THREE_MONTHS);
        long endTime = pref.getLong(Util.PREF_SYNC_END_TIME, time);
        mStartTime = new Date();
        mStartTime.setTime(startTime);
        mEndTime = new Date();
        mEndTime.setTime(endTime);
        setDate(mStartDateButton, mStartTime);
        setDate(mEndDateButton, mEndTime);
        populateWhen();

        SugarCrmApp app = (SugarCrmApp) getApplication();
        final String usr = SugarCrmSettings.getUsername(SyncConfigActivity.this).toString();
        if (ContentResolver.isSyncActive(app.getAccount(usr), SugarCRMProvider.AUTHORITY)) {
            findViewById(R.id.syncLater).setVisibility(View.GONE);
            findViewById(R.id.cancelSync).setVisibility(View.VISIBLE);
        }
    }

    /**
     * starts sync for all the modules in the background
     * 
     * @param v
     */
    public void startSync(View v) {
        Bundle extras = new Bundle();
        // extras.putInt(key, value)
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS, true);
        extras.putInt(Util.SYNC_TYPE, Util.SYNC_MODULES_DATA);
        SugarCrmApp app = (SugarCrmApp) getApplication();
        final String usr = SugarCrmSettings.getUsername(SyncConfigActivity.this).toString();
        ContentResolver.requestSync(app.getAccount(usr), SugarCRMProvider.AUTHORITY, extras);
        savePrefs();
    }

    private void savePrefs() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        long startMillis = mStartTime.getTime();
        long endMillis = mEndTime.getTime();
        Editor editor = pref.edit();
        editor.putLong(Util.PREF_SYNC_START_TIME, startMillis);
        editor.putLong(Util.PREF_SYNC_END_TIME, endMillis);
        editor.commit();
    }

    /**
     * cancel Sync
     * 
     * @param v
     */
    public void cancelSync(View v) {
        SugarCrmApp app = (SugarCrmApp) getApplication();
        final String usr = SugarCrmSettings.getUsername(SyncConfigActivity.this).toString();
        ContentResolver.cancelSync(app.getAccount(usr), SugarCRMProvider.AUTHORITY);
    }

    /**
     * sync Later, closes the activity
     * 
     * @param v
     */
    public void syncLater(View v) {
        savePrefs();
        setResult(RESULT_CANCELED);
        finish();
    }

    private void populateWhen() {
        setDate(mStartDateButton, mStartTime);
        setDate(mEndDateButton, mEndTime);

        mStartDateButton.setOnClickListener(new DateClickListener(mStartTime));
        mEndDateButton.setOnClickListener(new DateClickListener(mEndTime));
    }

    /**
     * DateListener
     */
    private class DateListener implements OnDateSetListener {
        View mView;

        public DateListener(View view) {
            mView = view;
        }

        public void onDateSet(DatePicker view, int year, int month, int monthDay) {
            // Cache the member variables locally to avoid inner class overhead.
            Date startDate = mStartTime;
            Date endDate = mEndTime;

            Calendar calendar = Calendar.getInstance();

            // Cache the start and end millis so that we limit the number
            // of calls to normalize() and toMillis(), which are fairly
            // expensive.
            long startMillis;
            long endMillis;
            if (mView == mStartDateButton) {
                // The start date was changed.
                long duartion = endDate.getTime() - startDate.getTime();
                calendar.set(year, month, monthDay);
                startMillis = calendar.getTimeInMillis();
                endMillis = startMillis + duartion;
                long curTime = System.currentTimeMillis();
                // see to that the endDate does not exceed the current date
                if (endMillis > curTime) {
                    endMillis = curTime;
                }

            } else {
                // The end date was changed.
                startMillis = startDate.getTime();
                calendar.set(year, month, monthDay);
                endMillis = calendar.getTimeInMillis();

                // Do not allow an event to have an end time before the start time.
                if (endDate.before(startDate)) {
                    endDate.setTime(startMillis);
                }
            }

            startDate.setTime(startMillis);
            endDate.setTime(endMillis);

            setDate(mStartDateButton, startDate);
            setDate(mEndDateButton, endDate);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            Editor editor = pref.edit();
            editor.putLong(Util.PREF_SYNC_START_TIME, startMillis);
            editor.putLong(Util.PREF_SYNC_END_TIME, endMillis);
            editor.commit();
        }
    }

    /**
     * DateClickListener
     */
    private class DateClickListener implements View.OnClickListener {
        private Date mDate;

        public DateClickListener(Date date) {
            mDate = date;
        }

        public void onClick(View v) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(mDate.getTime());

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int date = calendar.get(Calendar.DATE);

            new DatePickerDialog(SyncConfigActivity.this, new DateListener(v), year, month, date).show();
        }
    }

    private void setDate(TextView view, Date date) {
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                                        | DateUtils.FORMAT_SHOW_WEEKDAY
                                        | DateUtils.FORMAT_ABBREV_MONTH
                                        | DateUtils.FORMAT_ABBREV_WEEKDAY;
        view.setText(DateUtils.formatDateTime(this, date.getTime(), flags));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
