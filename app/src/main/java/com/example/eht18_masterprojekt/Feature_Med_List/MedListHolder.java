package com.example.eht18_masterprojekt.Feature_Med_List;

import com.example.eht18_masterprojekt.Core.Medikament;

import java.util.List;

public class MedListHolder {

    private static List<Medikament> globalMedList;

    public static List<Medikament> getMedList() {
        return globalMedList;
    }

    public static void setMedList(List<Medikament> medList) {
        if (globalMedList == null){
            globalMedList = medList;
        }
        else throw new RuntimeException("Tried to overwrite existing MedList");
    }
}
