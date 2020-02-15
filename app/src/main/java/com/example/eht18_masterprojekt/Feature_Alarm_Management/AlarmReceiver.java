package com.example.eht18_masterprojekt.Feature_Alarm_Management;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent musicServiceIntent = new Intent(context, AlarmMusicService.class);
        musicServiceIntent.putExtras(intent);
        context.startForegroundService(musicServiceIntent);

        Log.d("APP-ALARM_RECEIVED", "Med Alarm erhalten");
    }
}
