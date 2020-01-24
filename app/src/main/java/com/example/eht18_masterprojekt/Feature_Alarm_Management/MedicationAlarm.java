package com.example.eht18_masterprojekt.Feature_Alarm_Management;

import java.time.LocalTime;

public class MedicationAlarm {
    private long alarmID;
    private LocalTime alarmTime;
    private long medToTakeID;

    public MedicationAlarm(long alarmID, LocalTime alarmTime, long medToTakeID) {
        this.alarmID = alarmID;
        this.alarmTime = alarmTime;
        this.medToTakeID = medToTakeID;
    }

    public long getAlarmID() {
        return alarmID;
    }

    public LocalTime getAlarmTime() {
        return alarmTime;
    }

    public long getMedToTakeID() {
        return medToTakeID;
    }
}
