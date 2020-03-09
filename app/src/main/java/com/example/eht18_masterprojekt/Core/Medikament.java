package com.example.eht18_masterprojekt.Core;

import androidx.annotation.NonNull;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Medikament {
    private String bezeichnung;
    private String einheit;
    private String stueckzahl;
    private MedEinnahmeProtokoll einnahmeProtokoll;
    private long medId;

    public String getBezeichnung() {
        return bezeichnung;
    }
    public String getEinheit() {
        return einheit;
    }
    public MedEinnahmeProtokoll getEinnahmeProtokoll() {return einnahmeProtokoll;}
    public String getStueckzahl() {
        return stueckzahl;
    }
    public long getMedId() {return medId;}

    /*
    public void setBezeichnung(String bezeichnung) {this.bezeichnung = bezeichnung;}
    public void setStueckzahl(String stueckzahl) {this.stueckzahl = stueckzahl;}
    public void setEinnahmeProtokoll(MedEinnahmeProtokoll einnahmeProtokoll) {this.einnahmeProtokoll = einnahmeProtokoll;}
    public void setEinheit(String einheit) {this.einheit = einheit;}
    public void setPharmazentralnummer(int pharmazentralnummer) {this.pharmazentralnummer = pharmazentralnummer;}
    */

    public void setMedId(long medId) {this.medId = medId;}

    public Medikament(int medID, String bezeichnung, String einheit, String stueckzahl){
        this.medId = medID;
        this.bezeichnung = bezeichnung;
        this.einheit = einheit;
        this.stueckzahl = stueckzahl;
        this.einnahmeProtokoll = new MedEinnahmeProtokoll();
    }

    public Medikament(String bezeichnung, String einheit, String stueckzahl) {
        this.bezeichnung = bezeichnung;
        this.einheit = einheit;
        this.stueckzahl = stueckzahl;
        this.einnahmeProtokoll = new MedEinnahmeProtokoll();
    }

    public Medikament(String bezeichnung, String einheit) {
        this.bezeichnung = bezeichnung;
        this.einheit = einheit;
        this.einnahmeProtokoll = new MedEinnahmeProtokoll();
    }

    @NonNull
    @Override
    public String toString() {
        String temp = "";
        temp += getBezeichnung();
        temp += " ";
        temp += getStueckzahl();
        temp += " ";
        temp += getEinheit();
        temp += " ";
        temp += " ID: " + getMedId();
        temp += " EinnahmeZeiten: ";
        temp += einnahmeProtokoll.toString();
        return temp;
    }

    public MedEinnahmeProtokoll createEinnahmeProtokoll(){
        return new MedEinnahmeProtokoll();
    }

    public class MedEinnahmeProtokoll implements Iterable<MedEinnahme>{

        private List<MedEinnahme> einnahmeProtokoll;

        private MedEinnahmeProtokoll(){
            einnahmeProtokoll = new ArrayList<>();
        }

        @NonNull
        @Override
        public Iterator<MedEinnahme> iterator() {
            return new Iterator<MedEinnahme>() {
                private int iteratorPos = 0;

                @Override
                public boolean hasNext() {
                    return iteratorPos < einnahmeProtokoll.size();
                }

                @Override
                public MedEinnahme next() {
                    return einnahmeProtokoll.get(iteratorPos++);
                }
            };
        }

        public void addEinnahme(LocalTime einnahmeZeit, String einnahmeDosis){
            einnahmeProtokoll.add(new MedEinnahme(einnahmeZeit, einnahmeDosis));
        }

        public List<LocalTime> toTimeList(){
            List<LocalTime> einnahmeZeiten = new ArrayList<>();
            Iterator i = einnahmeProtokoll.iterator();

            for(MedEinnahme e : einnahmeProtokoll){
                LocalTime einnahmeZeit = e.getEinnahmeZeit();
                einnahmeZeiten.add(einnahmeZeit);
            }
            return einnahmeZeiten;
        }

        @NonNull
        @Override
        public String toString() {
            String result = "";

            for(MedEinnahme e : einnahmeProtokoll){
                LocalTime einnahmeZeit = e.getEinnahmeZeit();
                String dosis = e.getEinnahmeDosis();
                result += "Um " + einnahmeZeit.toString() + "Uhr: " + dosis + System.getProperty("line.separator");
            }
            return result;
        }

        /**
         * Retournieren der Einnahmedosis zur übergebenen Zeit.
         * @param lt
         * @return Einnahmedosis
         */
        public MedEinnahme getEinnahmeAt(LocalTime lt){
            for (MedEinnahme et : einnahmeProtokoll){
                if(et.getEinnahmeZeit() == lt){
                    return et;
                }
            }
            throw new RuntimeException("Med Einnahme für LocalTime: " + lt.toString() + " not found");
        }
    }

    public class MedEinnahme {
        /**
         * Eine MedEinnahme ist dann verlinkt, wenn sie eine AlarmID und eine NotificationID hat.
         */
        private boolean alarmLinked = false;

        private long einnahmeID;
        private int alarmID;
        private int notificationID;
        private LocalTime einnahmeZeit;
        private String einnahmeDosis;

        private MedEinnahme(LocalTime einnahmeZeit, String einnahmeDosis) {
            this.einnahmeZeit = einnahmeZeit;
            this.einnahmeDosis = einnahmeDosis;
        }

        public void linkAlarm(int alarmID, int notificationID){
            this.alarmID = alarmID;
            this.notificationID = notificationID;
            this.alarmLinked = true;
        }

        public Medikament getMed(){
            return Medikament.this;
        }

        public long getEinnahmeID() {
            return einnahmeID;
        }

        public void setEinnahmeID(long einnahmeID) {
            this.einnahmeID = einnahmeID;
        }

        public boolean isAlarmLinked() {
            return alarmLinked;
        }

        public int getAlarmID() {
            return alarmID;
        }

        public int getNotificationID() {
            return notificationID;
        }

        public LocalTime getEinnahmeZeit() {
            return einnahmeZeit;
        }

        public String getEinnahmeDosis() {
            return einnahmeDosis;
        }

        @NonNull
        @Override
        public String toString() {
            return "Um " + einnahmeZeit.toString() + " " + einnahmeDosis;
        }

    }
}
