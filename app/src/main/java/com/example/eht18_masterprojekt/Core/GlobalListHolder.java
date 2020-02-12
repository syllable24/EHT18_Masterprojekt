package com.example.eht18_masterprojekt.Core;

import com.example.eht18_masterprojekt.Core.Medikament;
import com.example.eht18_masterprojekt.Feature_Alarm_Management.MedicationAlarm;
import com.example.eht18_masterprojekt.Feature_Database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class GlobalListHolder {

    private static List<Medikament> globalMedList;
    private static boolean medListSet = false;

    public static void init(DatabaseAdapter da){
        globalMedList = da.retrieveMedList();
        medListSet = (globalMedList != null);
    }

    public static List<Medikament> getMedList() {
        return globalMedList;
    }

    public static void setMedList(List<Medikament> medList) {
        if (globalMedList == null){
            globalMedList = medList;
            medListSet = true;
        }
        else throw new RuntimeException("Tried to overwrite existing MedList");
    }

    public static boolean isMedListSet() {
        return medListSet;
    }
}
