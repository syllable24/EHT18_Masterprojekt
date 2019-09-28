package com.example.eht18_masterprojekt.Feature_SMS_Import;

import androidx.annotation.NonNull;

public class Medikament {
    private int pharmazentralnummer;
    private String bezeichnung;
    private String einheit;
    private int stueckzahl;
    private MedikamentDosierung dosierung;

    public int getPharmazentralnummer() {
        return pharmazentralnummer;
    }

    public void setPharmazentralnummer(int pharmazentralnummer) {
        this.pharmazentralnummer = pharmazentralnummer;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getEinheit() {
        return einheit;
    }

    public void setEinheit(String einheit) {
        this.einheit = einheit;
    }

    public int getStueckzahl() {
        return stueckzahl;
    }

    public void setStueckzahl(int stueckzahl) {
        this.stueckzahl = stueckzahl;
    }

    public MedikamentDosierung getDosierung() {
        return dosierung;
    }

    public void setDosierung(MedikamentDosierung dosierung) {
        this.dosierung = dosierung;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
