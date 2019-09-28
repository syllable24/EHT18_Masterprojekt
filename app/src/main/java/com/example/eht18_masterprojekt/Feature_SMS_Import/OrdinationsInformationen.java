package com.example.eht18_masterprojekt.Feature_SMS_Import;

import androidx.annotation.NonNull;

import com.example.eht18_masterprojekt.DateTuple;
import com.example.eht18_masterprojekt.TimeTuple;

import java.util.HashMap;
import java.util.List;

public class OrdinationsInformationen {
    String arztName;
    String addresse;
    HashMap<java.time.DayOfWeek, TimeTuple> oeffnungszeiten;
    List<DateTuple> urlaub;

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
