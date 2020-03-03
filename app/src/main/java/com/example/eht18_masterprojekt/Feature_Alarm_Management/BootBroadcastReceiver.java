package com.example.eht18_masterprojekt.Feature_Alarm_Management;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.eht18_masterprojekt.Core.GlobalListHolder;
import com.example.eht18_masterprojekt.Feature_Database.DatabaseAdapter;
import com.example.eht18_masterprojekt.Feature_Database.DatabaseHelper;
import com.example.eht18_masterprojekt.Feature_Med_List.MainActivityView;

public class BootBroadcastReceiver extends BroadcastReceiver {
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        AlarmRegisterTask at = new AlarmRegisterTask();
        at.execute();
    }

    private class AlarmRegisterTask extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] objects) {
            DatabaseAdapter da = new DatabaseAdapter(context);
            da.open();
            GlobalListHolder.init(da);
            new AlarmController(context).reRegisterAlarms();
            da.close();
            Log.d("APP-BOOT-RECEIVER", "Post-Boot: Alarms ReRegistered");
            return null;
        }
    }
}
