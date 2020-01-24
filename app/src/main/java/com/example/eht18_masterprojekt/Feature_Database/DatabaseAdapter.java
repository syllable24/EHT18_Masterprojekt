package com.example.eht18_masterprojekt.Feature_Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.eht18_masterprojekt.Core.EinnahmeTuple;
import com.example.eht18_masterprojekt.Core.Medikament;
import com.example.eht18_masterprojekt.Core.MedikamentEinnahme;
import com.example.eht18_masterprojekt.Feature_Alarm_Management.MedicationAlarm;

import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {

    public static SQLiteDatabase db;
    private static DatabaseHelper dbHelper;
    private Context context;
    private boolean medListStored = false;

    public static final String DB_NAME = "MedList";
    public static final int DB_VERSION = 2;

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
    public static final String COL_ALARM_MED_ID = "MedID";

    public static final String DB_CREATE_TABLE_MED_LIST = "CREATE TABLE " + TABLE_MED_LIST + "(" + COL_MED_ID +" INTEGER PRIMARY KEY autoincrement, " + COL_MED_BEZEICHNUNG + " text, " + COL_MED_EINHEIT + " text, " + COL_MED_STUECKZAHL + " int);";
    public static final String DB_CREATE_TABLE_MED_EINNAHME = "CREATE TABLE " + TABLE_MED_EINNAHME + "(" + COL_MED_EINNAHME_MED_ID + " integer NOT NULL, " + COL_MED_EINNAHME_EINNAHME_ZEIT + " TEXT, " + COL_MED_EINNAHME_EINNAHME_DOSIS +  " TEXT, FOREIGN KEY(" + COL_MED_EINNAHME_MED_ID + ") REFERENCES " + TABLE_MED_LIST + "(" + COL_MED_ID + "));";
    public static final String DB_CREATE_TABLE_ALARMS = "CREATE TABLE " + TABLE_ALARMS + "(" + COL_ALARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_ALARM_ZEIT + " TEXT " + COL_ALARM_MED_ID + " INTEGER);"; // TODO: Add foreign key reference

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

    /**
     * Speichern der übergebenen Medliste in SQLite DB.
     * Aktualisieren der medIDs der Medikamente in MedList.
     *
     * @param medList die gespeichert werden soll
     * @return true, wenn medListe erfolgreich gespeichert wurde.
     */
    public boolean storeMedList(@NotNull List<Medikament> medList){
        deleteCurrentMedList();
        ContentValues cvMed = new ContentValues();
        ContentValues cvEinnahme = new ContentValues();

        try {
            for (Medikament m : medList) {
                cvMed.put(COL_MED_BEZEICHNUNG, m.getBezeichnung());
                cvMed.put(COL_MED_EINHEIT, m.getEinheit());
                cvMed.put(COL_MED_STUECKZAHL, m.getStueckzahl());
                long id = db.insertOrThrow(TABLE_MED_LIST, null, cvMed);
                m.setMedId(id);

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
        return true;
    }

    /**
     * Löscht die bestehende MedListe aus SQLite DB.
     */
    private void deleteCurrentMedList() {
        Cursor temp = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table';", null);
        while (temp.moveToNext()){
            for (int i = 0; i < temp.getColumnCount(); i++){
                Log.d("DEBUG", temp.getString(i));
            }
        }

        db.execSQL("DELETE FROM " + TABLE_MED_EINNAHME);
        db.execSQL("DELETE FROM " + TABLE_MED_LIST);
        db.execSQL("DELETE FROM " + TABLE_ALARMS);
        // TODO: Unregister all Alarms
    }

    public void storeAlarms(List<MedicationAlarm> alarmList){
        for (MedicationAlarm mea : alarmList){
            ContentValues cv = new ContentValues();
            cv.put(COL_ALARM_ID, mea.getAlarmID());
            cv.put(COL_ALARM_ZEIT, mea.getAlarmTime().toString());
            cv.put(COL_ALARM_MED_ID, mea.getAlarmID());
            db.insertOrThrow(TABLE_ALARMS, null, cv);
        }
    }

    public List retrieveAlarms(){
        return null;
    }

    public List<Medikament> retrieveMedList(){
        if (db == null){
            throw new RuntimeException("No Database opened!");
        }
        List<Medikament> retrievedMedList = new ArrayList<>();

        try{
            Cursor medResult = db.rawQuery(
                    "SELECT "
                    + COL_MED_BEZEICHNUNG +","
                    + COL_MED_EINHEIT + ", "
                    + COL_MED_STUECKZAHL + ","
                    + COL_MED_ID
                    + " FROM "
                    + TABLE_MED_LIST,null);

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
                medListStored = true;
            }
            medResult.close();
        }
        catch(SQLException e){
            Log.e("DB-Q", e.getMessage());
        }
        return retrievedMedList;
    }

    public boolean isMedListStored() {
        return medListStored;
    }
}
