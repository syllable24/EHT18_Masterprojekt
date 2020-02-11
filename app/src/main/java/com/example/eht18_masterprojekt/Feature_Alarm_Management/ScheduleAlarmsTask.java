package com.example.eht18_masterprojekt.Feature_Alarm_Management;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.example.eht18_masterprojekt.Core.GlobalListHolder;
import com.example.eht18_masterprojekt.Feature_Database.DatabaseAdapter;

import java.util.List;

public class ScheduleAlarmsTask extends AsyncTask{

    public static final String ACTION_ALARMS_SCHEDULED = "AlarmsScheduled";

    Context context;
    DatabaseAdapter da;

    public ScheduleAlarmsTask(Context ctx, DatabaseAdapter da){
        this.context = ctx;
        this.da = da;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        AlarmController ac = new AlarmController(context);
        ac.registerAlarms(GlobalListHolder.getMedList());
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Intent i = new Intent(ACTION_ALARMS_SCHEDULED);
        context.sendBroadcast(i);
    }
}
