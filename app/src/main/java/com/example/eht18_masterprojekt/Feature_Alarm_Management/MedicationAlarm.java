package com.example.eht18_masterprojekt.Feature_Alarm_Management;

import java.time.LocalTime;

public class MedicationAlarm {
    private int alarmID;
    private LocalTime alarmTime;
    private long medToTakeID;
    private String medToTakeName;
    private int notificationID;

    public MedicationAlarm(int alarmID, LocalTime alarmTime, long medToTakeID, String medToTakeName, int notificationID) {
        this.alarmID = alarmID;
        this.alarmTime = alarmTime;
        this.medToTakeID = medToTakeID;
        this.medToTakeName = medToTakeName;
        this.notificationID = notificationID;
    }

    public int getAlarmID() {
        return alarmID;
    }

    public LocalTime getAlarmTime() {
        return alarmTime;
    }

    public long getMedToTakeID() {
        return medToTakeID;
    }

    public String getMedToTakeName() {
        return medToTakeName;
    }

    public int getNotificationID() {
        return notificationID;
    }

    public void setAlarmID(int alarmID) {
        this.alarmID = alarmID;
    }

    public void setAlarmTime(LocalTime alarmTime) {
        this.alarmTime = alarmTime;
    }

    public void setMedToTakeID(long medToTakeID) {
        this.medToTakeID = medToTakeID;
    }

    public void setMedToTakeName(String medToTakeName) {
        this.medToTakeName = medToTakeName;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    @Override
    public String toString() {
        return medToTakeName + " um " + alarmTime.toString() + " Uhr";
    }
}
