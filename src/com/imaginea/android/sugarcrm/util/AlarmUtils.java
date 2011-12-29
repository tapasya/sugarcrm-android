package com.imaginea.android.sugarcrm.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMProvider;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AlarmColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.MeetingsColumns;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AlarmUtils {

    public static final String INTENT_EXTRA_EVENT_DETAILS = "eventextras";
    
    public static final int ALARM_STATE_ENABLED = 1;
    
    public static final int ALARM_STATE_DISABLED = 0;
    
    public static final int ALARM_STATE_NA = -1;

    /**
     * @param context
     * @param rowId
     * @param date
     */
    public static void setEventAlarm(Context context, String rowId, long time, Bundle eventExtras) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(AlarmReceiver.ALARM_INTENT_ACTION);
        intent.putExtra(AlarmColumns.ID, rowId);
        intent.setData(Uri.parse(rowId));
        intent.putExtra(INTENT_EXTRA_EVENT_DETAILS, eventExtras);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pi);
    }

    /**
     * @param context
     * @param rowId
     */
    public static void cancelEventAlarm(Context context, String rowId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(AlarmReceiver.ALARM_INTENT_ACTION);
        intent.putExtra(AlarmColumns.ID, rowId);
        intent.setData(Uri.parse(rowId));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pi);
    }
    
    public static void fireFirstAlarm(Context context, String mModuleName){
        fireNextAlarm(context, "0", mModuleName);
    }
    
    public static void fireNextAlarm(Context context, String currentRowId, String mModuleName){
        String nextRowId = Integer.toString(Integer.parseInt(currentRowId) + 1 );
        Bundle eventDetails = fetchMeetingDetails(context, nextRowId, mModuleName);
        setEventAlarm(context, nextRowId, getTime(eventDetails.getString(MeetingsColumns.START_DATE)), eventDetails);
    }

    public static int isAlarmEnabled(Context context, String rowId) {
        int notificationStatus = -1;
        DatabaseHelper mDbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor mCursor = db.query(DatabaseHelper.ALARM_TABLE_NAME, null, AlarmColumns.ID + "=\"" + rowId + "\"", null, null, null, null);
        if (mCursor != null && mCursor.moveToFirst()) {
            notificationStatus = mCursor.getInt(mCursor.getColumnIndex(AlarmColumns.ALARM_STATE));
            mCursor.close();            
        } 
        db.close();
        return notificationStatus;
    }
    
    public static void makeAlarmEntry(Context context, String rowId, int alarmState){
        DatabaseHelper mDbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AlarmColumns.ID, rowId);
        values.put(AlarmColumns.ALARM_STATE, alarmState);
        String whereClause = Util.ROW_ID + "=\"" + rowId + "\"";
        long n = db.update(DatabaseHelper.ALARM_TABLE_NAME, values, whereClause, null);
        if (n <= 0)
            n = db.insert(DatabaseHelper.ALARM_TABLE_NAME, null, values);

        db.close();
        return;
    }
    
    public static Bundle fetchMeetingDetails(Context context, String mRowId, String mModuleName) {
        Bundle extras = new Bundle();
        Uri uri = Uri.withAppendedPath(Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/"
                                        + Util.MEETINGS), mRowId);
        DatabaseHelper mDbHelper = new DatabaseHelper(context);
        Cursor mCursor = context.getContentResolver().query(uri, mDbHelper.getModuleProjections(mModuleName), null, null, mDbHelper.getModuleSortOrder(mModuleName));
        if (mCursor != null && mCursor.moveToFirst()) {
            String title = mCursor.getString(mCursor.getColumnIndex(MeetingsColumns.NAME));
            extras.putString(MeetingsColumns.NAME, title);
            String message = mCursor.getString(mCursor.getColumnIndex(MeetingsColumns.DESCRIPTION));
            extras.putString(MeetingsColumns.DESCRIPTION, message);
            String ticker = mCursor.getString(mCursor.getColumnIndex(MeetingsColumns.ASSIGNED_USER_NAME));
            extras.putString(MeetingsColumns.ASSIGNED_USER_NAME, ticker);
            String date = mCursor.getString(mCursor.getColumnIndex(MeetingsColumns.START_DATE));
            extras.putString(MeetingsColumns.START_DATE, date);
        }

        if (mCursor != null)
            mCursor.close();
        return extras;
    }
    
    public static long getTime(String dateString){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = formatter.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        } 
        return SystemClock.elapsedRealtime();
    }
    
    public static void enableAlarmSetting(Context context){
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = mPreferences.edit();
        editor.putBoolean(Util.PREF_ALARM_STATE, true);
        editor.commit();
    }
}
