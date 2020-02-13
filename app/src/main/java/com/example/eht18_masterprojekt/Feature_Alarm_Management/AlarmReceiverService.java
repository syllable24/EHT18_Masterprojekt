package com.example.eht18_masterprojekt.Feature_Alarm_Management;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class AlarmReceiverService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
        //TODO: Alarms are not fired when app gets cancelled.
        Log.d("APP-ALARM_RECEIVER", "AlarmReceiverService started");
    }

    @Override
    public void onDestroy() {
        Log.d("APP-ALARM_RECEIVER", "AlarmReceiverService killed");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
