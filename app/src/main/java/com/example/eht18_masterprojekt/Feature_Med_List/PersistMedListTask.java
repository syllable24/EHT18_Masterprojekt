package com.example.eht18_masterprojekt.Feature_Med_List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.eht18_masterprojekt.Core.GlobalListHolder;
import com.example.eht18_masterprojekt.Core.Medikament;
import com.example.eht18_masterprojekt.Feature_Database.DatabaseAdapter;

import java.util.List;

/**
 * Speichert die aktuell, in GlobalListHolder, gesetzte Medikationsliste.
 * Dabei wird die bestehende MedListe überschrieben.
 */
class PersistMedListTask extends AsyncTask {

    Context context;
    DatabaseAdapter da;

    PersistMedListTask(Context ctx, DatabaseAdapter da){
        this.context = ctx;
        this.da = da;
    }

    @Override
    protected Object doInBackground(Object... obj) {
        List<Medikament> medlist = GlobalListHolder.getMedList();
        da.storeMedList(medlist);
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Intent i = new Intent("MedList_Persist_Successful");
        context.sendBroadcast(i);
    }
}