package com.example.eht18_masterprojekt.Feature_Med_List;

import com.example.eht18_masterprojekt.Core.Medikament;

import java.util.List;

public class MedListHolder {

    private static List<Medikament> medList;

    public static List<Medikament> getMedList() {
        return medList;
    }

    public static void setMedList(List<Medikament> medList) {
        medList = medList;
    }
}
