package com.example.eht18_masterprojekt.Feature_Alarm_Management;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.eht18_masterprojekt.Core.Medikament;
import com.example.eht18_masterprojekt.Core.MedikamentEinnahme;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class AlarmController {

    Context context;
    AlarmManager alarmManager;
    public static final int FULL_DAY_MILLIS = 1000 * 60 * 60 * 24;
    public static final String ACTION_MED_ALARM = "MedAlarm";
    public static final String ALARM_INTENT_EXTRA_MED_ID = "medID";
    public static final String ALARM_INTENT_EXTRA_MED_EINNAHME_ZEIT = "medEinnahmeZeit";

    public AlarmController(Context context){
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * Basierend auf übergebener MedListe Alarme für die einzelnen
     * Medikamente erstellen und in Android registrieren.
     * @param medList basis for alarm scheduling
     */
    public List<MedicationAlarm> registerAlarms(@NotNull List<Medikament> medList){

        //TODO: Add Group Alarms for meds with the same alarmTime.

        List<MedicationAlarm> alarmList = new ArrayList<>();

        for (Medikament med : medList){
            MedikamentEinnahme me = med.getEinnahmeZeiten();

            for (LocalTime dt : me.toTimeList()){
                MedicationAlarm mea = registerIndivAlarm(med, dt);
                alarmList.add(mea);
            }
        }
        return alarmList;
    }

    /**
     * Registrieren des wiederholenden Alarms für das übergebene Medikaments, zur übergebenen Zeit
     * im Android Alarm Manager. Der Alarm wird alle 24h wiederholt.
     *
     * @param med Medikament
     * @param lt Einnahmezeit
     * @return Registrierten MedAlarm
     */
    private MedicationAlarm registerIndivAlarm(Medikament med, LocalTime lt){
        long alarmTime = toAlarmTime(lt);
        long testAlarmTime = System.currentTimeMillis() + 1000 * 15;

        // uniqueID generieren, um den Alarm wieder deaktivieren zu können
        int uniqueId = (int) System.currentTimeMillis();

        Intent i = new Intent(context, AlarmReceiver.class);
        i.setAction(ACTION_MED_ALARM);
        i.putExtra(ALARM_INTENT_EXTRA_MED_ID, med.getMedId());
        i.putExtra(ALARM_INTENT_EXTRA_MED_EINNAHME_ZEIT, lt.toString());

        PendingIntent alarmAction = PendingIntent.getBroadcast(context, uniqueId, i, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, testAlarmTime, FULL_DAY_MILLIS, alarmAction);

        Log.d("APP", "Alarm um: " + i.getStringExtra(ALARM_INTENT_EXTRA_MED_EINNAHME_ZEIT) + " für: " + i.getLongExtra(ALARM_INTENT_EXTRA_MED_ID, 0) + " gestellt");

        MedicationAlarm mea = new MedicationAlarm(uniqueId, lt, med.getMedId(), med.getBezeichnung());

        return mea;
    }

    /**
     * Konvertiert die übergebene LocalTime zu einem long, der die kommende Einnahmezeit
     * eines Medikaments repräsentiert. Dabei wird geprüft, ob die Zeit für den aktuellen
     * oder den kommenden Tag gesetzt werden muss.
     *
     * @param scheduledTime: Uhrzeit, um die ein Medikament eingenommen werden muss, Bsp. 10:00
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
            // Zeit für den aktuellen Tag setzen
            //Log.d("APP", scheduledTime.toString() + " zu " + new SimpleDateFormat("yyyy.MM.dd HH:mm").format(new Date(scheduleCurrentDay)) + " konvertiert");
            return scheduleCurrentDay;
        }
        else {
            // Zeit für den kommenden Tag setzen
            midnight.add(Calendar.DAY_OF_MONTH, 1);
            long scheduleNextDay = midnight.getTime().getTime();
            //Log.d("APP", scheduledTime.toString() + " zu " + new SimpleDateFormat("yyyy.MM.dd HH:mm").format(new Date(scheduleNextDay)) + " konvertiert");
            return scheduleNextDay;
        }
    }

    /**
     * Löscht alle Android-Alarme der App.
     * @return Number of cancelled Alarms.
     */
    public int unregisterScheduledAlarms(List<Integer> registeredAlarmIds) {
        int i = 0;

        //Get Alarm Intents identified by uniqueID.
        for (int uniqueId : registeredAlarmIds) {
            // PendingIntent, der den Alarm erzeugt hat rekonstruieren und schauen, ob dieser registriert ist.
            //PendingIntent pi = PendingIntent.getBroadcast(context, uniqueId, new Intent(context, .class), PendingIntent.FLAG_NO_CREATE);
            //boolean alarmUp = (pi != null);
            //if (alarmUp) {
            //    alarmManager.cancel(pi);
            //    i++;
            //}
        }
        return i;
    }

    /**
     * Prüfen, ob die übergebene Liste an Alarmen registriert sind.
     * @return
     */
    public boolean checkAlarmsRegistered(@NotNull List<MedicationAlarm> alarms){
        boolean alarmUp;

        for (MedicationAlarm medicationAlarm : alarms) {
            int uniqueID = medicationAlarm.getAlarmID();
            alarmUp = (PendingIntent.getBroadcast(context, uniqueID, new Intent(ACTION_MED_ALARM), PendingIntent.FLAG_NO_CREATE) != null);
            if (alarmUp == false){
                return false;
            }
        }
        return true;
    }
}
