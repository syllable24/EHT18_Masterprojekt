package com.example.eht18_masterprojekt.Feature_Database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(DatabaseAdapter.DB_CREATE);
        }
        catch(SQLException e){
            Log.e("DB", e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            Log.e("DB", "Upgrade from V: " + oldVersion + " to V" + newVersion);
            db.execSQL("");
        }
        catch(SQLException e){
            Log.e("DB", e.getMessage());
        }
    }
}
