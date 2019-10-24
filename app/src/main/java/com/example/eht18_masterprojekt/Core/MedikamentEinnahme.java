package com.example.eht18_masterprojekt.Core;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MedikamentEinnahme {
    // the Date must only contain Hour, Minute and Second
    Map<Date, String> einnahmeProtokoll;

    public void add(Date einnahmeZeit, String einnahmeDosis){
        einnahmeProtokoll.put(einnahmeZeit, einnahmeDosis);
    }

    public List<Date> getEinnahmeZeiten(){
        List<Date> einnahmeZeiten = new ArrayList<>();
        Iterator i = einnahmeProtokoll.keySet().iterator();

        while (i.hasNext()){
            Date einnahmeZeit = (Date) i.next();
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
            Date einnahmeZeit = (Date) i.next();
            String dosis = einnahmeProtokoll.get(einnahmeZeit);

            result += "Um " + new SimpleDateFormat("HH:MM").format(einnahmeZeit) + "Uhr: " + dosis + System.getProperty("line.separator");
        }
        return result;
    }
}
