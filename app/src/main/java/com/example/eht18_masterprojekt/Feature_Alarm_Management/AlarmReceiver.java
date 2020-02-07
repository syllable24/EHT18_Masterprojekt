package com.example.eht18_masterprojekt.Feature_Alarm_Management;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.eht18_masterprojekt.R;

public class AlarmReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startMusicService = new Intent(context, AlarmMusicService.class);
        startMusicService.putExtras(intent);
        context.startService(startMusicService);

        Log.d("APP", "Med Alarm für MedID: " + intent.getExtras().getLong(AlarmController.ALARM_INTENT_EXTRA_MED_ID) + " erhalten");
    }
}
