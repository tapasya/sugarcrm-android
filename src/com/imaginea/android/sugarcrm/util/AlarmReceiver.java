package com.imaginea.android.sugarcrm.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.imaginea.android.sugarcrm.RestUtilConstants;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AlarmColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.MeetingsColumns;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String ALARM_INTENT_ACTION = "com.imaginea.android.sugarcrm.ALARM_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isAlarmEnabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Util.PREF_ALARM_STATE, false);
        Bundle extras = intent.getBundleExtra(AlarmUtils.INTENT_EXTRA_EVENT_DETAILS);
        String mRowId = extras.getString(AlarmColumns.ID);
        String mModuleName = extras.getString(RestUtilConstants.MODULE_NAME);
        if (isAlarmEnabled) {
            int eventAlarmState = AlarmUtils.isAlarmEnabled(context, mRowId);
            if (eventAlarmState == AlarmUtils.ALARM_STATE_ENABLED
                                            || eventAlarmState == AlarmUtils.ALARM_STATE_NA) {
                showNotification(context, extras);
            } else {
                AlarmUtils.fireNextAlarm(context, mRowId, mModuleName);
            }
        } else {
            AlarmUtils.fireNextAlarm(context, mRowId, mModuleName);
        }
    }

    private void showNotification(Context context, Bundle extras) {
        String mTitle = extras.getString(MeetingsColumns.NAME);
        String mMessage = extras.getString(MeetingsColumns.DESCRIPTION);
        String mTickerText = extras.getString(MeetingsColumns.ASSIGNED_USER_NAME);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        Notification n = new Notification(android.R.drawable.stat_notify_sync_noanim, mTickerText, System.currentTimeMillis());
        n.setLatestEventInfo(context, mTitle, mMessage, pendingIntent);
        n.flags = Notification.FLAG_AUTO_CANCEL;
        n.defaults |= Notification.DEFAULT_SOUND;
        nm.notify(0, n);
    }
}
