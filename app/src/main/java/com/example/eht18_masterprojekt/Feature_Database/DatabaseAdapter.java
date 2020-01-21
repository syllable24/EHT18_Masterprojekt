package com.example.eht18_masterprojekt.Feature_Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.eht18_masterprojekt.Core.EinnahmeTuple;
import com.example.eht18_masterprojekt.Core.Medikament;
import com.example.eht18_masterprojekt.Core.MedikamentEinnahme;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {

    public static SQLiteDatabase db;
    private static DatabaseHelper dbHelper;
    private Context context;

    public static final String DB_NAME = "MedList.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_MED_LIST = "MedList";
    public static final String COL_MED_ID = "MedID";
    public static final String COL_MED_BEZEICHNUNG = "Bezeichnung";
    public static final String COL_MED_EINHEIT = "Einheit";
    public static final String COL_MED_STUECKZAHL = "Stueckzahl";

    public static final String TABLE_MED_EINNAHME = "MedEinnahme";
    public static final String COL_MED_EINNAHME_MED_ID = "MedID";
    public static final String COL_MED_EINNAHME_EINNAHME_ZEIT = "EinnahmeZeit";
    public static final String COL_MED_EINNAHME_EINNAHME_DOSIS = "EinnahmeDosis";

    public static final String TABLE_ALARMS = "Alarms";
    public static final String COL_ALARM_ID = "AlarmID";
    public static final String COL_ALARM_ZEIT = "AlarmZeit";

    public static final String DB_CREATE =
            "CREATE TABLE " + TABLE_MED_LIST + "(" + COL_MED_ID +" integer primary key autoincrement, " + COL_MED_BEZEICHNUNG + " text, " + COL_MED_EINHEIT + " text, " + COL_MED_STUECKZAHL + " int);" +
            "CREATE TABLE " + TABLE_MED_EINNAHME + "(" + COL_MED_EINNAHME_MED_ID + "integer NOT NULL, FOREIGN KEY(" + COL_MED_EINNAHME_MED_ID + " REFERENCES " + TABLE_MED_LIST + "(" + COL_MED_ID + "), " + COL_MED_EINNAHME_EINNAHME_ZEIT + "text, " + COL_MED_EINNAHME_EINNAHME_DOSIS +  " text);" +
            "CREATE TABLE " + TABLE_ALARMS + "(" + COL_ALARM_ID + "integer primary key autoincrement, " + COL_ALARM_ZEIT + " text);";

    public DatabaseAdapter(Context cx){
        context = cx;
        dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
    }

    public DatabaseAdapter open() throws SQLException{
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        db.close();
    }

    public SQLiteDatabase getDatabaseInstance(){
        return db;
    }

    public void storeMedList(List<Medikament> medList){
        ContentValues cvMed = new ContentValues();
        ContentValues cvEinnahme = new ContentValues();

        try {
            for (Medikament m : medList) {
                cvMed.put(COL_MED_BEZEICHNUNG, m.getBezeichnung());
                cvMed.put(COL_MED_EINHEIT, m.getEinheit());
                cvMed.put(COL_MED_STUECKZAHL, m.getStueckzahl());
                long id = db.insertOrThrow(TABLE_MED_LIST, null, cvMed);

                for (EinnahmeTuple et : m.getEinnahmeZeiten()) {
                    cvEinnahme.put(COL_MED_EINNAHME_MED_ID, id);
                    cvEinnahme.put(COL_MED_EINNAHME_EINNAHME_ZEIT, et.getEinnahmeZeit().toString());
                    cvEinnahme.put(COL_MED_EINNAHME_EINNAHME_DOSIS, et.getEinnahmeDosis());
                    db.insertOrThrow(TABLE_MED_EINNAHME, null, cvEinnahme);
                }
            }
        }
        catch (SQLException e){
            Log.e("Store MedList", e.getMessage());
        }
    }

    public void storeAlarms(List alarmList){

    }

    public List retrieveAlarms(){
        return null;
    }

    public List<Medikament> retrieveMedList(){
        List<Medikament> retrievedMedList = new ArrayList<>();

        try{
            Cursor medResult = db.rawQuery("SELECT Bezeichnung, Einheit, Stueckzahl, MedID FROM MedList",null);
            while (medResult.moveToNext()){
                Medikament m = new Medikament();
                m.setBezeichnung(medResult.getString(0));
                m.setEinheit(medResult.getString(1));
                m.setStueckzahl(medResult.getString(2));
                m.setPharmazentralnummer(medResult.getInt(3));

                Cursor einnahmeResult = db.rawQuery("SELECT EinnahmeZeit, EinnahmeDosis FROM MedEinnahme WHERE MedID = " + medResult.getInt(3), null);
                MedikamentEinnahme me = new MedikamentEinnahme();

                while(einnahmeResult.moveToNext()){
                    me.add(LocalTime.parse(einnahmeResult.getString(0)), einnahmeResult.getString(1));
                }
                einnahmeResult.close();
                m.setEinnahmeZeiten(me);
                retrievedMedList.add(m);
            }
            medResult.close();

        }
        catch(SQLException e){
            Log.e("DB-Q", e.getMessage());
        }
        return retrievedMedList;
    }
}
