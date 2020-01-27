package com.example.eht18_masterprojekt.Core;

import com.example.eht18_masterprojekt.Core.Medikament;
import com.example.eht18_masterprojekt.Feature_Alarm_Management.MedicationAlarm;

import java.util.List;

public class GlobalListHolder {

    private static List<Medikament> globalMedList;
    private static List<MedicationAlarm> globalAlarmList;

    public static List<Medikament> getMedList() {
        return globalMedList;
    }

    public static void setMedList(List<Medikament> medList) {
        if (globalMedList == null){
            globalMedList = medList;
        }
        else throw new RuntimeException("Tried to overwrite existing MedList");
    }

    public static List<MedicationAlarm> getAlarmList() {
        return globalAlarmList;
    }

    public static void setAlarmList(List<MedicationAlarm> alarmList) {
        if (globalAlarmList == null){
            globalAlarmList = alarmList;
        }
        else throw new RuntimeException("Tried to overwrite existing AlarmList");
    }
}
