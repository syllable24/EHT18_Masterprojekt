package com.example.eht18_masterprojekt.Feature_Database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

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

    public static final String DB_CREATE =
            "CREATE TABLE MedList(MedID integer primary key autoincrement, Bezeichnung text, Einheit text, Stueckzahl int);" +
            "CREATE TABLE MedEinnahme(MedID integer NOT NULL, FOREIGN KEY(MedID REFERENCES MedList(MedID), EinnahmeZeit text, EinnahmeDosis text);" +
            "CREATE TABLE Alarms(AlarmID integer primary key autoincrement, AlarmZeit text);";

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
