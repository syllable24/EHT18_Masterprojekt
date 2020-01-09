package com.example.eht18_masterprojekt.Core;

import androidx.annotation.NonNull;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MedikamentEinnahme {
    private Map<LocalTime, String> einnahmeProtokoll;

    public MedikamentEinnahme(){
        einnahmeProtokoll = new TreeMap<>();
    }

    public void add(LocalTime einnahmeZeit, String einnahmeDosis){
        einnahmeProtokoll.put(einnahmeZeit, einnahmeDosis);
    }

    public List<LocalTime> toTimeList(){
        List<LocalTime> einnahmeZeiten = new ArrayList<>();
        Iterator i = einnahmeProtokoll.keySet().iterator();

        while (i.hasNext()){
            LocalTime einnahmeZeit = (LocalTime) i.next();
            einnahmeZeiten.add(einnahmeZeit);
        }
        return einnahmeZeiten;
    }

    @NonNull
    @Override
    public String toString() {
        String result = "";
        Iterator i = einnahmeProtokoll.keySet().iterator();

        while(i.hasNext()){
            LocalTime einnahmeZeit = (LocalTime) i.next();
            String dosis = einnahmeProtokoll.get(einnahmeZeit);
            result += "Um " + einnahmeZeit.toString() + "Uhr: " + dosis + System.getProperty("line.separator");
        }
        return result;
    }
}
