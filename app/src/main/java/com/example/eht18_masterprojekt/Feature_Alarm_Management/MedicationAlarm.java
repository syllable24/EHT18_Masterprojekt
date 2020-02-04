package com.example.eht18_masterprojekt.Feature_Alarm_Management;

import java.time.LocalTime;

public class MedicationAlarm {
    private int alarmID;
    private LocalTime alarmTime;
    private long medToTakeID;
    private String medToTakeName;

    public MedicationAlarm(int alarmID, LocalTime alarmTime, long medToTakeID, String medToTakeName) {
        this.alarmID = alarmID;
        this.alarmTime = alarmTime;
        this.medToTakeID = medToTakeID;
        this.medToTakeName = medToTakeName;
    }

    public String getMedToTakeName() {return medToTakeName;}

    public int getAlarmID() {
        return alarmID;
    }

    public LocalTime getAlarmTime() {
        return alarmTime;
    }

    public long getMedToTakeID() {
        return medToTakeID;
    }
}
