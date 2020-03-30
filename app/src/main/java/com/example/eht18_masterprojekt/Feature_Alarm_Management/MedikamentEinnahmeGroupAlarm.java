package com.example.eht18_masterprojekt.Feature_Alarm_Management;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Container für Medikament-Alarme die zur selben Zeit abgefeuert werden sollen. Dementsprechend
 * soll für n Medikamente nur ein Alarm in Android registriert werden.
 */
public class MedikamentEinnahmeGroupAlarm implements Serializable {

    private final int ALARM_NOT_REGISTERED = -1;

    private final int S_REP_ALARM_ID = 0;
    private final int S_REP_ALARM_TIME = 1;
    private final int S_REP_MEDS_TO_TAKE_START = 2;

    private int alarmID = ALARM_NOT_REGISTERED;
    private LocalTime alarmTime;
    private ArrayList<Long> medsToTakeIds = new ArrayList<>();

    MedikamentEinnahmeGroupAlarm(String stringRepresentation) {
        String[] content = stringRepresentation.split(";");
        this.alarmID = Integer.parseInt(content[S_REP_ALARM_ID]);
        this.alarmTime = LocalTime.parse(content[S_REP_ALARM_TIME]);

        for (int i = S_REP_MEDS_TO_TAKE_START; i < content.length; i++){
            medsToTakeIds.add(Long.parseLong(content[i]));
        }
    }

    public void setAlarmID(int alarmID) {
        this.alarmID = alarmID;
    }

    public int getAlarmID() {
        return alarmID;
    }

    MedikamentEinnahmeGroupAlarm(LocalTime alarmTime) {
        this.alarmTime = alarmTime;
    }

    public LocalTime getAlarmTime() {
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

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(alarmID).append(";");
        builder.append(alarmTime).append(";");

        builder.append(medsToTakeIds.get(0));
        for (int i = 1; i < medsToTakeIds.size(); i++) {
            Long l = medsToTakeIds.get(i);
            builder.append(";").append(l);
        }
        return builder.toString();
    }
}