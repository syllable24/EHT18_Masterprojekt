package com.example.eht18_masterprojekt.Feature_Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.eht18_masterprojekt.Core.Medikament;
import com.example.eht18_masterprojekt.Feature_Alarm_Management.AlarmController;
import com.example.eht18_masterprojekt.Feature_Alarm_Management.MedikamentEinnahmeGroupAlarm;

import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.eht18_masterprojekt.Feature_Database.DatabaseHelper.*;

public class DatabaseAdapter {

    public static SQLiteDatabase db;
    private static DatabaseHelper dbHelper;
    private Context context;
    private boolean medListStored = false;

    public DatabaseAdapter(Context cx){
        context = cx;
        dbHelper = new DatabaseHelper(context, DatabaseHelper.DB_NAME, null, DatabaseHelper.DB_VERSION);
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
                long medID = db.insertOrThrow(TABLE_MED_LIST, null, cvMed);
                m.setMedId(medID);

                for (Medikament.MedEinnahme medEinnahme : m.getEinnahmeProtokoll()) {
                    cvEinnahme.put(COL_MED_EINNAHME_MED_ID, medID);
                    cvEinnahme.put(COL_MED_EINNAHME_EINNAHME_ZEIT, medEinnahme.getEinnahmeZeit().toString());
                    cvEinnahme.put(COL_MED_EINNAHME_EINNAHME_DOSIS, medEinnahme.getEinnahmeDosis());
                    long einnahmeID = db.insertOrThrow(TABLE_MED_EINNAHME, null, cvEinnahme);
                    medEinnahme.setEinnahmeID(einnahmeID);
                    Log.d("APP-PERSIST-MED", "Med-Einnahme für: " + m.getBezeichnung() + " um " + medEinnahme.getEinnahmeZeit().toString() + " mit ID: " + medID + " gespeichert");
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
        try {
            db.execSQL("DELETE FROM " + TABLE_MED_EINNAHME);
            db.execSQL("DELETE FROM " + TABLE_MED_LIST);
            db.execSQL("DELETE FROM " + TABLE_ALARMS);
        }
        catch (SQLException e ){
            Log.e("DB-EMPTY", e.getMessage());
        }
    }

    public void storeAlarms(@NotNull List<Medikament> medList){
        for (Medikament med : medList){
            if (med.getMedId() == 0) throw new RuntimeException(med.toString() + " has no medID, store in DB first!");
            for (Medikament.MedEinnahme mea : med.getEinnahmeProtokoll()) {
                ContentValues cv = new ContentValues();
                cv.put(COL_ALARM_ID, mea.getAlarmID());
                cv.put(COL_ALARM_ZEIT, mea.getEinnahmeZeit().toString());
                cv.put(COL_ALARM_MED_ID, med.getMedId());
                db.insertOrThrow(TABLE_ALARMS, null, cv);
            }
        }
    }

    public void storeAlarms(@NotNull Map<LocalTime, MedikamentEinnahmeGroupAlarm> groupAlarmMap){
        for (LocalTime l : groupAlarmMap.keySet()){
            MedikamentEinnahmeGroupAlarm current = groupAlarmMap.get(l);

            if(current.getMedsToTakeIds() == null) throw new RuntimeException("No MedIDs in groupAlarm");

            ContentValues cv = new ContentValues();
            cv.put(COL_ALARM_ID, current.getAlarmID());
            cv.put(COL_ALARM_ZEIT, current.getAlarmTime().toString());
            db.insertOrThrow(TABLE_ALARMS, null, cv);

            cv.clear();
            String sqlValues = "(" + groupAlarmMap.get(l).getMedsToTakeIds().get(0) + ", "+ current.getAlarmID() + ")";
            for (int i = 1; i < groupAlarmMap.get(l).getMedsToTakeIds().size(); i++){
                sqlValues += ", " + "(" + groupAlarmMap.get(l).getMedsToTakeIds().get(i) + ", "+ current.getAlarmID() + ")";
            }

            String sql = "INSERT INTO " + TABLE_MEDS_TO_TAKE + "(" + COL_MED_ID + ", " + COL_ALARM_ID + ") VALUES" + sqlValues;
            db.rawQuery(sql, null);
            Log.d("APP-DB_STORE_ALARM", sql);
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
        // TODO: Also Select notificationID of MedEinnahme
        String queryMed =
                "SELECT "
                    + COL_MED_ID
                    + ", " + COL_MED_BEZEICHNUNG
                    + ", " + COL_MED_EINHEIT
                    + ", " + COL_MED_STUECKZAHL
                    + ", " + COL_MED_EINNAHME_EINNAHME_DOSIS
                + " FROM "
                    + TABLE_MED_LIST
                    + " INNER JOIN "
                        + TABLE_MED_EINNAHME + " USING("+ COL_MED_ID +")"
                + " WHERE "
                    + COL_MED_ID + " = " + medID
                    + " AND " + COL_MED_EINNAHME_EINNAHME_ZEIT + " = '" + einnahmeZeit + "'";

        Cursor c = db.rawQuery(queryMed, null);

        if (c.moveToNext()){
            Medikament m = new Medikament(
                      c.getInt(0)
                    , c.getString(1)
                    , c.getString(2)
                    , c.getString(3)
            );


            m.getEinnahmeProtokoll().addEinnahme(LocalTime.parse(einnahmeZeit), c.getString(4));
            Log.d("APP-DatabaseAdapter",m.toString());
            c.close();
            return m;
        }
        else throw new RuntimeException("Kein Med für medID: " + medID + " und einnahmeZeit: " + einnahmeZeit + " gefunden.");
    }

    public List<Medikament> retrieveMedikamentListWithEinnahmeDosis(List<Long> medIDs, String einnahmeZeit){

        String sqlMedIDs = medIDs.get(0).toString();
        for (int i = 1; i < medIDs.size(); i++){
            sqlMedIDs += ", " + medIDs.get(i).toString();
        }

        String queryMed =
                "SELECT "
                        + COL_MED_ID
                        + ", " + COL_MED_BEZEICHNUNG
                        + ", " + COL_MED_EINHEIT
                        + ", " + COL_MED_STUECKZAHL
                        + ", " + COL_MED_EINNAHME_EINNAHME_DOSIS
                        + " FROM "
                        + TABLE_MED_LIST
                        + " INNER JOIN "
                        + TABLE_MED_EINNAHME + " USING("+ COL_MED_ID +")"
                        + " WHERE "
                        + COL_MED_ID + " IN(" + sqlMedIDs + ")"
                        + " AND " + COL_MED_EINNAHME_EINNAHME_ZEIT + " = '" + einnahmeZeit + "'";

        Cursor c = db.rawQuery(queryMed, null);

        List<Medikament> result = new ArrayList<>();
        while (c.moveToNext()){
            Medikament m = new Medikament(
                    c.getInt(0)
                    , c.getString(1)
                    , c.getString(2)
                    , c.getString(3)
            );

            m.getEinnahmeProtokoll().addEinnahme(LocalTime.parse(einnahmeZeit), c.getString(4));
            result.add(m);
            Log.d("APP-DatabaseAdapter", m.toString());
        }
        c.close();
        return result;

    }

    /**
     * Einlesen einer bestehenden MedListe aus DB.
     *
     * @return eingelesene MedList
     */
    public List<Medikament> retrieveMedList(){
        List<Medikament> retrievedMedList;

        if (db == null){
            throw new RuntimeException("No Database opened!");
        }

        try{
            Cursor medResult = db.rawQuery(
                    "SELECT "
                        + COL_MED_BEZEICHNUNG +","
                        + COL_MED_EINHEIT + ", "
                        + COL_MED_STUECKZAHL + ","
                        + COL_MED_ID
                    + " FROM "
                        + TABLE_MED_LIST,null);

            if(medResult.getCount() > 0){
                retrievedMedList = new ArrayList<>();
            }
            else return null;

            while (medResult.moveToNext()){
                Medikament m = new Medikament(
                          medResult.getInt(3)
                        , medResult.getString(0)
                        , medResult.getString(1)
                        , medResult.getString(2)
                );

                Cursor medEinnahmen = db.rawQuery("SELECT EinnahmeZeit, EinnahmeDosis FROM MedEinnahme WHERE MedID = " + medResult.getInt(3), null);

                if (medEinnahmen.getCount() == 0){
                    throw new RuntimeException("Medikament ID: " + m.getMedId() + " hat keine verbundenen MedEinnahmen in DB");
                }

                while(medEinnahmen.moveToNext()){
                    m.getEinnahmeProtokoll().addEinnahme(LocalTime.parse(medEinnahmen.getString(0)), medEinnahmen.getString(1));
                }

                medEinnahmen.close();
                retrievedMedList.add(m);
                medListStored = true;
            }
            medResult.close();
            return retrievedMedList;
        }
        catch(SQLException e){
            Log.e("DB-Q", e.getMessage());
            return null;
        }
    }

    public boolean isMedListStored() {
        return medListStored;
    }

    /**
     * Gespeicherte AlarmIDs für die übergebene MedID retournieren.
     * @param medID
     * @return
     */
    public List<Integer> getMedAlarmIDs(long medID) {
        List<Integer> result = new ArrayList<>();
        String sql = "SELECT " + COL_ALARM_ID + " FROM " + TABLE_MEDS_TO_TAKE + " WHERE " + COL_MED_ID + " = " + medID;

        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()){
            result.add(c.getInt(0));
        }
        return result;
    }
}
