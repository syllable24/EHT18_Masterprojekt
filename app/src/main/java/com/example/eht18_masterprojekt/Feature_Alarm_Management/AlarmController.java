package com.example.eht18_masterprojekt.Feature_Alarm_Management;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.eht18_masterprojekt.Core.Medikament;
import com.example.eht18_masterprojekt.Core.MedikamentEinnahme;
import com.example.eht18_masterprojekt.Feature_Database.DatabaseAdapter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class AlarmController {

    Context context;

    public AlarmController(Context context){
        this.context = context;
    }

    /**
     * Determines if alarms are already initialized.
     * @param medList
     * @return
     */
    public boolean scheduleAlarms(List<Medikament> medList){

        boolean alarmsInitialized = determineAlarmsInitialized();

        if (!alarmsInitialized){
            createAlarms(medList);
            return true;
        }
        else return false;
    }

    /**
     * Schedules android-alarms based on given List.
     * @param medList basis for alarm scheduling
     */
    private void createAlarms(List<Medikament> medList){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        List<MedicationAlarm> alarmList = new ArrayList<>();

        for (Medikament med : medList){
            MedikamentEinnahme me = med.getEinnahmeZeiten();

            for (LocalTime dt : me.toTimeList()){
                Intent i = new Intent(context, AlarmReceiver.class);

                long alarmTime = toAlarmTime(dt);

                int uniqueId = (int) System.currentTimeMillis(); // uniqueID generiere, um den Alarm wieder deaktivieren zu können
                PendingIntent alarmAction = PendingIntent.getBroadcast(context, uniqueId, i, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, alarmAction);

                MedicationAlarm mea = new MedicationAlarm(uniqueId, dt, med.getMedId());
                alarmList.add(mea);
            }
        }
        DatabaseAdapter da = new DatabaseAdapter(context);
        da.storeAlarms(alarmList);
    }

    /**
     * Returns Millisecond-Time on which an Alarm can be scheduled based on the given Time.
     * Determines if the Alarm will be scheduled on the current day or the next day.
     *
     * @param scheduledTime: Time at which a drug must be taken e.g. at 10:00 every day
     * @return
     */
    private long toAlarmTime(LocalTime scheduledTime){
        Calendar midnight = new GregorianCalendar();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);

        midnight.add(Calendar.HOUR, scheduledTime.getHour());
        midnight.add(Calendar.MINUTE, scheduledTime.getMinute());

        long scheduleCurrentDay = midnight.getTime().getTime();
        Date currentTime = new Date();

        if (scheduleCurrentDay > currentTime.getTime()){
            // Alarm can be set for current day
            return scheduleCurrentDay;
        }
        else {
            // Alarm must be set for the next day
            midnight.add(Calendar.DAY_OF_MONTH, 1);
            long scheduleNextDay = midnight.getTime().getTime();
            return scheduleNextDay;
        }
    }

    /**
     * Checks if alarms are set.
     * @return true if at least one alarm is set
     */
    private boolean determineAlarmsInitialized(){
        // TODO: Loop through possible alarms

        boolean alarmUp = (PendingIntent.getBroadcast(context, 0,
                new Intent("com.my.package.MY_UNIQUE_ACTION"), // TODO: Insert Alarm Action
                PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp)
        {
            Log.d("myTag", "Alarm is already active");
            return true;
        }

        return false;
    }

}
