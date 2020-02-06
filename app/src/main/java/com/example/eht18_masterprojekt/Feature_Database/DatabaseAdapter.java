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
import com.example.eht18_masterprojekt.Feature_Alarm_Management.AlarmController;
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
    public static final int DB_VERSION = 3;

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
    public static final String DB_CREATE_TABLE_ALARMS = "CREATE TABLE " + TABLE_ALARMS + "(" + COL_ALARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_ALARM_ZEIT + " TEXT, " + COL_ALARM_MED_ID + " INTEGER, FOREIGN KEY(" + COL_ALARM_MED_ID + ") REFERENCES " + TABLE_MED_LIST + "(" + COL_MED_ID + "));";

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
                    Log.d("PERSIST-MED", "Med-Einnahme für: " + m.getBezeichnung() + " um " + et.getEinnahmeZeit().toString() + " mit ID: " + id + " gespeichert");
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
     * Alle Android Alarme werden gelöscht.
     */
    private void deleteCurrentMedList() {
        db.execSQL("DELETE FROM " + TABLE_MED_EINNAHME);
        db.execSQL("DELETE FROM " + TABLE_MED_LIST);

        Cursor registeredAlarmIds = db.rawQuery("SELECT " + COL_ALARM_ID + " FROM " + TABLE_ALARMS, null);
        List<Integer> alarmIds = new ArrayList<>();

        while (registeredAlarmIds.moveToNext()){
            alarmIds.add(registeredAlarmIds.getInt(0));
        }
        AlarmController ac = new AlarmController(context);
        ac.unregisterScheduledAlarms(alarmIds);

        db.execSQL("DELETE FROM " + TABLE_ALARMS);
    }

    public void emptyDatabase(){
        db.execSQL("DELETE FROM " + TABLE_MED_EINNAHME);
        db.execSQL("DELETE FROM " + TABLE_MED_LIST);
        db.execSQL("DELETE FROM " + TABLE_ALARMS);
    }

    public void storeAlarms(@NotNull List<MedicationAlarm> alarmList){
        for (MedicationAlarm mea : alarmList){
            if (mea.getMedToTakeID() != 0) {
                ContentValues cv = new ContentValues();
                cv.put(COL_ALARM_ID, mea.getAlarmID());
                cv.put(COL_ALARM_ZEIT, mea.getAlarmTime().toString());
                cv.put(COL_ALARM_MED_ID, mea.getMedToTakeID());
                db.insertOrThrow(TABLE_ALARMS, null, cv);
            }
            else throw new IllegalArgumentException(mea.toString() + " has no medID, store in DB first!");
        }
    }

    /**
     * Retourniert das Medikament der übergebenen medID mit der Einnahmedosis für die übergebene
     * Einnahmezeit.
     *
     * @param medID
     * @param einnahmeZeit
     * @return Medikament mit einer Einnahmedosis
     */
    public Medikament retrieveMedikamentWithEinnahmeDosis(long medID, String einnahmeZeit){
        String queryMed =
                "SELECT "
                    + COL_MED_ID
                    + ", " + COL_MED_BEZEICHNUNG
                    + ", " + COL_MED_STUECKZAHL
                    + ", " + COL_MED_EINHEIT
                    + ", " + COL_MED_EINNAHME_EINNAHME_DOSIS
                + " FROM "
                    + TABLE_MED_LIST
                    + " INNER JOIN "
                        + TABLE_MED_EINNAHME + " USING("+ COL_MED_ID +")"
                + " WHERE "
                    + COL_MED_ID + " = " + medID
                    + " AND " + COL_MED_EINNAHME_EINNAHME_ZEIT + " = '" + einnahmeZeit + "'";

        Cursor c = db.rawQuery(queryMed, null);
        Medikament m = new Medikament();
        MedikamentEinnahme mea = new MedikamentEinnahme();

        if (c.moveToNext()){
            m.setMedId(c.getLong(0));
            m.setBezeichnung(c.getString(1));
            m.setStueckzahl(c.getString(2));
            m.setEinheit(c.getString(3));
            mea.add(LocalTime.parse(einnahmeZeit), c.getString(4));
            m.setEinnahmeZeiten(mea);
        }
        c.close();

        return m;
    }

    /**
     * Auslesen der gespeicherten Alarme.
     * @return Liste an Alarmen
     */
    public List<MedicationAlarm> retrieveAlarms(){
        List<MedicationAlarm> temp = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT " + COL_ALARM_ID + ", " + COL_ALARM_MED_ID + ", " + COL_ALARM_ZEIT + " FROM " + TABLE_ALARMS, null);

        while (c.moveToNext()){
            Log.d("APP", "Values AlarmID: " + c.getLong(0) + " MedID: " + c.getLong(1) + " AlarmZeit: " + c.getString(2));
            Cursor cSingleValue = db.rawQuery("SELECT " + COL_MED_BEZEICHNUNG + " FROM " + TABLE_MED_LIST + " WHERE " + COL_MED_ID + " = " + c.getLong(1), null);
            cSingleValue.moveToNext();
            if (cSingleValue.isAfterLast()) throw new RuntimeException("MedID " + c.getLong(0) + " is not in MedList!");

            String medToTakeName = cSingleValue.getString(0);
            MedicationAlarm mea
                    = new MedicationAlarm(c.getInt(0)
                                        , LocalTime.parse(c.getString(2))
                                        , c.getLong(1)
                                        , medToTakeName);
            temp.add(mea);
            cSingleValue.close();
        }
        c.close();
        return temp;
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
                m.setMedId(medResult.getInt(3));

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
