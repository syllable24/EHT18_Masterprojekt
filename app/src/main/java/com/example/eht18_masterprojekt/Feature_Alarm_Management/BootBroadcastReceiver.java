package com.example.eht18_masterprojekt.Feature_Alarm_Management;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;

import com.example.eht18_masterprojekt.Core.GlobalListHolder;
import com.example.eht18_masterprojekt.Feature_Database.DatabaseAdapter;

public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO: TEST THIS
        // ReRegister All Med-Alarms after device restart.
        AlarmController ac = new AlarmController(context);
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
        boolean alarmsRegistered = ac.checkAlarmsRegistered(GlobalListHolder.getMedList(), databaseAdapter);

        if (!alarmsRegistered) {
            ac.registerAlarms(GlobalListHolder.getMedList());
        }

    }
}
