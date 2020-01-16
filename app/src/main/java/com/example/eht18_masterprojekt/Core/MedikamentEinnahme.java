package com.example.eht18_masterprojekt.Core;

import androidx.annotation.NonNull;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MedikamentEinnahme implements Iterable<EinnahmeTuple>{
    private List<EinnahmeTuple> einnahmeProtokoll;

    public MedikamentEinnahme(){
        einnahmeProtokoll = new ArrayList<>();
    }

    @NonNull
    @Override
    public Iterator<EinnahmeTuple> iterator() {
        return new Iterator<EinnahmeTuple>() {
            private int iteratorPos = 0;

            @Override
            public boolean hasNext() {
                return iteratorPos < einnahmeProtokoll.size();
            }

            @Override
            public EinnahmeTuple next() {
                // TODO: Test me
                return einnahmeProtokoll.get(iteratorPos++);
            }
        };
    }

    public void add(LocalTime einnahmeZeit, String einnahmeDosis){
        einnahmeProtokoll.add(new EinnahmeTuple(einnahmeZeit, einnahmeDosis));
    }

    public List<LocalTime> toTimeList(){
        List<LocalTime> einnahmeZeiten = new ArrayList<>();
        Iterator i = einnahmeProtokoll.iterator();

        for(EinnahmeTuple e : einnahmeProtokoll){
            LocalTime einnahmeZeit = e.getEinnahmeZeit();
            einnahmeZeiten.add(einnahmeZeit);
        }
        return einnahmeZeiten;
    }

    @NonNull
    @Override
    public String toString() {
        String result = "";

        for(EinnahmeTuple e : einnahmeProtokoll){
            LocalTime einnahmeZeit = e.getEinnahmeZeit();
            String dosis = e.getEinnahmeDosis();
            result += "Um " + einnahmeZeit.toString() + "Uhr: " + dosis + System.getProperty("line.separator");
        }
        return result;
    }

}
