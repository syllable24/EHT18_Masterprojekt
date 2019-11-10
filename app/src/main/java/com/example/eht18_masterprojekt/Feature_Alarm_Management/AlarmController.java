package com.example.eht18_masterprojekt.Feature_Alarm_Management;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.eht18_masterprojekt.Core.Medikament;
import com.example.eht18_masterprojekt.Core.MedikamentEinnahme;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

class AlarmController {

    Context c;

    public AlarmController(Context context){
        c = context;
    }

    /**
     * Determines if alarms are already initialized.
     * @param medList
     * @return
     */
    boolean scheduleAlarms(List<Medikament> medList){
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
        AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);

        for (Medikament med : medList){
            MedikamentEinnahme me = med.getEinnahmeZeiten();

            for (LocalTime dt : me.toTimeList()){
                Intent i = new Intent(c, AlarmReceiver.class);

                long alarmTime = toAlarmTime(dt);

                // if cancelling the alarms should be needed these ids must be remembered
                int uniqueId = (int) System.currentTimeMillis();
                PendingIntent alarmAction = PendingIntent.getBroadcast(c, uniqueId, i, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, alarmAction);
            }
        }
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

    private boolean determineAlarmsInitialized(){
        // TODO: implement me
        return false;
    }

}
