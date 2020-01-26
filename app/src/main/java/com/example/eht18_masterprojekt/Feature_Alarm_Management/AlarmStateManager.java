package com.example.eht18_masterprojekt.Feature_Alarm_Management;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This Broadcast Receiver must be targeted whenever the State of the Medication List has changed
 * (eg a new SMS is imported). The Alarms will be set based on the new State of the Medication List
 */
public class AlarmStateManager extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmController ac = new AlarmController(context);
        // TODO: Get med List
        //boolean success = ac.scheduleAlarms(null);
    }
}
