package com.example.eht18_masterprojekt.Feature_Alarm_Management;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.eht18_masterprojekt.Core.Medikament;
import com.example.eht18_masterprojekt.Core.NotificationController;
import com.example.eht18_masterprojekt.Feature_Database.DatabaseAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;

public class AlarmController {

    Context context;
    AlarmManager alarmManager;
    public static final int FULL_DAY_MILLIS = 1000 * 60 * 60 * 24;
    public static final String ACTION_MED_ALARM = "MedAlarm";
    public static final String ALARM_INTENT_EXTRA_MED_ID = "medID";
    public static final String ALARM_INTENT_EXTRA_MED_EINNAHME_ZEIT = "medEinnahmeZeit";
    public static final String ALARM_INTENT_EXTRA_MED_EINNAHME_GROUP = "medEinnahmeGroup";

    public AlarmController(Context context){
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * Basierend auf übergebener MedListe werden Alarm Gruppen erstellt. Alarm Gruppen fassen
     * Alarme für unterschiedliche Medikamente, die zur selben Zeit eingenommen werden müssen,
     * zusammen. Dementsprechend wird für die Alarm Gruppe nur ein einzelner Alarm in Android
     * registriert.
     *
     * Jeder registrierte Alarm wird in der SQLiteDB gespeichert.
     *
     * @param medList Medikamente für die Alarme gesetzt werden sollen
     */
    public void registerAlarms(@NotNull List<Medikament> medList){

        // TODO: Test Group Alarm Feature
        Map<LocalTime, MedikamentEinnahmeGroupAlarm> alarmMedGroups = new HashMap<>();

        for (Medikament med : medList){
            int notificationID = NotificationController.NOTIFICATION_BASE_ID; // TODO: This is a job for NotificationController.java

            for (Medikament.MedEinnahme me : med.getEinnahmeProtokoll()){

                MedikamentEinnahmeGroupAlarm alarmGroup = alarmMedGroups.get(me.getEinnahmeZeit());

                if (alarmGroup == null){
                    MedikamentEinnahmeGroupAlarm newAlarmGroup = new MedikamentEinnahmeGroupAlarm(me.getEinnahmeZeit());
                    newAlarmGroup.addAlarm(me.getMed().getMedId());
                    alarmMedGroups.put(newAlarmGroup.getAlarmTime(), newAlarmGroup);
                }
                else {
                    alarmGroup.addAlarm(me.getMed().getMedId());
                }
            }

            for(LocalTime key : alarmMedGroups.keySet()){
                registerIndivAlarm(alarmMedGroups.get(key));
            }
        }

        DatabaseAdapter da = new DatabaseAdapter(context);
        da.storeAlarms(medList);
    }

    /**
     * Registrieren des wiederholenden Alarms für die übergebene MedikamentEinnahme im Android
     * Alarm Manager. Der Alarm wird alle 24h wiederholt. Die medEinnahme wird mit dem generiertem
     * Alarm verlinkt.
     *
     * @param medEinnahme
     */
    private void registerIndivAlarm(Medikament.MedEinnahme medEinnahme, int notificationID){
        long alarmTime = toAlarmTime(medEinnahme.getEinnahmeZeit());
        long testAlarmTime = System.currentTimeMillis() + 1000 * 15;
        Date display = new Date(testAlarmTime);
        Log.d("APP-ALARM_TIME_TEST", "testAlarmTime " + new SimpleDateFormat("YYYY.MM.dd hh:mm:ss").format(display));

        // uniqueID generieren, um den Alarm wieder deaktivieren zu können
        int alarmID = (int) System.currentTimeMillis();

        Intent i = new Intent(context, AlarmReceiver.class);
        i.putExtra(ALARM_INTENT_EXTRA_MED_ID, medEinnahme.getMed().getMedId());
        i.putExtra(ALARM_INTENT_EXTRA_MED_EINNAHME_ZEIT, medEinnahme.getEinnahmeZeit().toString());

        PendingIntent alarmAction = PendingIntent.getBroadcast(context, alarmID, i, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, testAlarmTime, FULL_DAY_MILLIS, alarmAction);

        medEinnahme.linkAlarm(alarmID, notificationID);

        Log.d("APP-ALARM_TIME", "Alarm um: " + i.getStringExtra(ALARM_INTENT_EXTRA_MED_EINNAHME_ZEIT) + " für: " + i.getLongExtra(ALARM_INTENT_EXTRA_MED_ID, 0) + " gestellt");
    }

    /**
     * Registrieren eines wiederholenden Alarms für die übergebene MedikamentEinnahmeGroup.
     *
     * @param me MedikamentEinnahmeGruppe, für die ein Alarm gestellt werden soll.
     */
    private void registerIndivAlarm(MedikamentEinnahmeGroupAlarm me){
        long alarmTime = toAlarmTime(me.getAlarmTime());
        long testAlarmTime = System.currentTimeMillis() + 1000 * 15;
        Date display = new Date(testAlarmTime);
        Log.d("APP-ALARM_TIME_TEST", "testAlarmTime " + new SimpleDateFormat("YYYY.MM.dd hh:mm:ss").format(display));

        // uniqueID generieren, um den Alarm wieder deaktivieren zu können
        int alarmID = (int) System.currentTimeMillis();

        Intent i = new Intent(context, AlarmReceiver.class);
        i.putExtra(ALARM_INTENT_EXTRA_MED_EINNAHME_GROUP, me);

        PendingIntent alarmAction = PendingIntent.getBroadcast(context, alarmID, i, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, testAlarmTime, FULL_DAY_MILLIS, alarmAction);

        String medIDs = "";
        for (Long medID : me.getMedsToTakeIds()){
            medIDs += medID + " ";
        }
        Log.d("APP-ALARM_TIME", "Alarm um: " + me.getAlarmTime() + " für Meds: " + medIDs + "gestellt");
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

    /**
     * Container für Medikament-Alarme die zur selben Zeit abgefeuert werden sollen. Dementsprechend
     * soll für n Medikament nur ein Alarm in Android registriert werden.
     */
    public class MedikamentEinnahmeGroupAlarm implements Serializable{

        private final int ALARM_NOT_REGISTERED = -1;

        private int alarmID = ALARM_NOT_REGISTERED;
        private LocalTime alarmTime;
        private List<Long> medsToTakeIds;

        MedikamentEinnahmeGroupAlarm(LocalTime alarmTime) {
            this.medsToTakeIds = new ArrayList<>();
            this.alarmTime = alarmTime;
        }

        LocalTime getAlarmTime() {
            return alarmTime;
        }

        void addAlarm(Long medID){
            medsToTakeIds.add(medID);
        }

        public List<Long> getMedsToTakeIds() {
            return medsToTakeIds;
        }

        public boolean isRegistered(){
            return alarmID != ALARM_NOT_REGISTERED;
        }
    }
}
