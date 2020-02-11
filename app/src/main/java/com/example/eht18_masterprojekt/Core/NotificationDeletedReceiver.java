package com.example.eht18_masterprojekt.Core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.eht18_masterprojekt.Feature_Alarm_Management.AlarmMusicService;

/**
 * Statischer BroadcastReceiver ist notwendig, um deleteIntents von Notifications erhalten zu
 * können. Der Intent wird an AlarmMusicService weitergeleitet.
 */
public class NotificationDeletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("APP", "NotificationID: " + intent.getIntExtra(NotificationController.EXTRA_NOTIFICATION_ID, 0) +  " gelöscht");
        Intent i = new Intent(AlarmMusicService.ACTION_STOP_ALARM);
        i.putExtras(intent);
        context.sendBroadcast(i);
    }
}