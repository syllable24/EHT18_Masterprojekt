package com.example.eht18_masterprojekt.Feature_Database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "MedList";
    public static final int DB_VERSION = 6;

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

    public static final String TABLE_MEDS_TO_TAKE = "MedsToTake";
    public static final String COL_MEDS_TO_TAKE_ID = "ID";

    public static final String DB_CREATE_TABLE_MED_LIST = "CREATE TABLE " +TABLE_MED_LIST+ "(" +COL_MED_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+COL_MED_BEZEICHNUNG+" TEXT, "+COL_MED_EINHEIT+" TEXT, "+COL_MED_STUECKZAHL+" int);";
    public static final String DB_CREATE_TABLE_MED_EINNAHME = "CREATE TABLE " +TABLE_ALARMS+"("+COL_ALARM_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+COL_ALARM_ZEIT+" TEXT);";
    public static final String DB_CREATE_TABLE_ALARMS = "CREATE TABLE " +TABLE_MED_EINNAHME+ "(" + COL_MED_EINNAHME_MED_ID + " INTEGER NOT NULL, " + COL_MED_EINNAHME_EINNAHME_ZEIT + " TEXT, " + COL_MED_EINNAHME_EINNAHME_DOSIS +  " TEXT, FOREIGN KEY(" + COL_MED_EINNAHME_MED_ID + ") REFERENCES " + TABLE_MED_LIST + "(" + COL_MED_ID + "));";
    public static final String DB_CREATE_TABLE_MEDS_TO_TAKE = "CREATE TABLE " +TABLE_MEDS_TO_TAKE+"("+COL_MEDS_TO_TAKE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +COL_ALARM_ID+ " INTEGER, "+ COL_MED_ID +" INTEGER, FOREIGN KEY("+ COL_ALARM_ID +") REFERENCES "+TABLE_ALARMS+"("+COL_ALARM_ID+"), FOREIGN KEY("+COL_MED_ID+") REFERENCES "+TABLE_MED_LIST+"("+COL_MED_ID+"));";



    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.setVersion(DB_VERSION);
            db.execSQL(DB_CREATE_TABLE_ALARMS);
            db.execSQL(DB_CREATE_TABLE_MED_LIST);
            db.execSQL(DB_CREATE_TABLE_MED_EINNAHME);
            db.execSQL(DB_CREATE_TABLE_MEDS_TO_TAKE);
        }
        catch(SQLException e){
            Log.e("DB", e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            Log.d("DB", "Upgrade from V: " + oldVersion + " to V" + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MED_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MED_EINNAHME);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDS_TO_TAKE);
            onCreate(db);
        }
        catch(SQLException e){
            Log.e("DB", e.getMessage());
        }
    }
}
