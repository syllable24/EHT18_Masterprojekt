package com.example.eht18_masterprojekt.Core;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.time.LocalTime;

public class EinnahmeTuple {
    LocalTime einnahmeZeit;
    String einnahmeDosis;

    public LocalTime getEinnahmeZeit() {
        return einnahmeZeit;
    }

    public void setEinnahmeZeit(LocalTime einnahmeZeit) {
        this.einnahmeZeit = einnahmeZeit;
    }

    public String getEinnahmeDosis() {
        return einnahmeDosis;
    }

    public void setEinnahmeDosis(String einnahmeDosis) {
        this.einnahmeDosis = einnahmeDosis;
    }

    public EinnahmeTuple(LocalTime e, String d){
        einnahmeDosis = d;
        einnahmeZeit = e;
    }

    @NonNull
    @Override
    public String toString() {
        return "Um " + einnahmeZeit.toString() + " " + einnahmeDosis;
    }
}
