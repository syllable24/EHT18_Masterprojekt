package com.example.eht18_masterprojekt.Core;

import androidx.annotation.NonNull;

public class Medikament {
    private int pharmazentralnummer;
    private String bezeichnung;
    private String einheit;
    private String stueckzahl;
    private MedikamentEinnahme einnahmeZeiten;
    private long medId;

    public int getPharmazentralnummer() {
        return pharmazentralnummer;
    }
    public String getBezeichnung() {
        return bezeichnung;
    }
    public String getEinheit() {
        return einheit;
    }
    public MedikamentEinnahme getEinnahmeZeiten() {return einnahmeZeiten;}
    public String getStueckzahl() {
        return stueckzahl;
    }
    public long getMedId() {return medId;}

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }
    public void setStueckzahl(String stueckzahl) {
        this.stueckzahl = stueckzahl;
    }
    public void setEinnahmeZeiten(MedikamentEinnahme einnahmeZeiten) {this.einnahmeZeiten = einnahmeZeiten;}
    public void setEinheit(String einheit) {
        this.einheit = einheit;
    }
    public void setPharmazentralnummer(int pharmazentralnummer) {this.pharmazentralnummer = pharmazentralnummer;}
    public void setMedId(long medId) {this.medId = medId;}

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
