package com.example.eht18_masterprojekt.Feature_Med_List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.eht18_masterprojekt.Core.Medikament;
import com.example.eht18_masterprojekt.Feature_Alarm_Management.AlarmController;
import com.example.eht18_masterprojekt.Feature_Database.DatabaseAdapter;
import com.example.eht18_masterprojekt.Feature_SMS_Import.ImportSmsView;
import com.example.eht18_masterprojekt.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivityView extends AppCompatActivity {

    private RecyclerView rv_medList;
    private IntentFilter medListInitFilter = new IntentFilter("MedList_Init_Successful");
    private IntentFilter medListPersistedFilter = new IntentFilter("MedList_Persist_Successful");
    private boolean commandRegisterBroadcastReceiver = false;
    private boolean broadcastReceiversRegistered = false;
    private DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
    private FloatingActionButton fab;

    private MedListRecyclerViewAdapter adapter;

    /**
     * Wird aufgerufen, wenn eine neue medListe aus einer SMS importiert wurde.
     * Überschreibt eine bereits gespeicherte MedList, startet die Alarmgenerierung
     * und aktualisiert die Anzeige der MedList.
     */
    private BroadcastReceiver broadcastReceiverMedListImported = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateMedListDisplay(MedListHolder.getMedList());

            if (databaseAdapter.isMedListStored()){
                userInteractOverwriteMedList();
            }
            else {
                startPersistMedList();
            }
        }
    };

    /**
     * Wird aufgerufen, sobald eine medListe in DB gespeichert wurde und somit
     * alle Meds eine medID von der DB erhalten haben. Basierend auf der
     * globalen medListe werden die Alarme initialisiert.
     */
    private BroadcastReceiver broadcastReceiverMedListStored = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startAlarmScheduling();
        }
    };

    /**
     * Einlesen der GUI Elemente.
     */
    private void initActivity(){
        rv_medList = findViewById(R.id.rv_med_list);
        fab = findViewById(R.id.fabStartSmsImport);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!broadcastReceiversRegistered){
                    registerSmsImportBroadcastReceivers();
                }
                startSmsImport();
            }
        });
    }

    /**
     * Setzen der MedList. Zuerst wird versucht, eine MedList aus der SQLite DB auszulesen.
     * Falls das nicht möglich ist, wird der SMS Import gestartet und die dafür notwendigen
     * BroadcastReceiver registriert. Siehe: {@link #broadcastReceiverMedListImported} und
     * {@link #broadcastReceiverMedListStored}
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseAdapter.open();
        databaseAdapter.emptyDatabase();
        initActivity();
        List<Medikament> medList = queryMedList();

        if (medList.size() == 0){
            // Leeren RecyclerView-Adapter am UI Thread initialisieren, damit dieser mittels
            // Broadcast von "MedList_Init_Successful" aktualisiert werden kann.
            startSmsImport();
            initMedListDisplay(new ArrayList<Medikament>());
        }
        else{
            MedListHolder.setMedList(medList);
            initMedListDisplay(medList);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (commandRegisterBroadcastReceiver) {
            unregisterReceiver(broadcastReceiverMedListImported);
            unregisterReceiver(broadcastReceiverMedListStored);
            broadcastReceiversRegistered = false;
        }
        databaseAdapter.close();
    }

    @Override
    public void onResume(){
        super.onResume();
        if (commandRegisterBroadcastReceiver) {
            registerSmsImportBroadcastReceivers();
        }
    }

    /**
     * Registrieren der Broadcastreceiver die für den SMS Import
     * notwendig sind.
     */
    private void registerSmsImportBroadcastReceivers(){
        registerReceiver(broadcastReceiverMedListImported, medListInitFilter);
        registerReceiver(broadcastReceiverMedListStored, medListPersistedFilter);
        broadcastReceiversRegistered = true;
    }

    private void startAlarmScheduling(){
        ScheduleAlarmsTask st = new ScheduleAlarmsTask();
        st.execute();
    }

    /**
     * Startet das Speichern der aktuellen MedListe
     */
    private void startPersistMedList(){
        PersistMedListTask pt = new PersistMedListTask();
        pt.execute();
    }

    /**
     * Anzeigen der MedList in RecyclerView
     * @param medList
     */
    private void initMedListDisplay(List<Medikament> medList){
        Log.d("MedList", "Display Init. MedCount: " + medList.size());
        adapter = new MedListRecyclerViewAdapter(this);
        adapter.setMedList(medList);

        LinearLayoutManager lm = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                this,
                lm.getOrientation());
        rv_medList.setLayoutManager(lm);
        rv_medList.addItemDecoration(dividerItemDecoration);
        rv_medList.setAdapter(adapter);
    }

    private void updateMedListDisplay(List<Medikament> newList){
        adapter.setMedList(newList);
        adapter.notifyDataSetChanged();
    }

    /**
     * Retournieren einer gespeicherten MedListe aus SQLite DB
     * @return Gespeicherte MedList
     */
    private List<Medikament> queryMedList(){
        databaseAdapter.open();
        return databaseAdapter.retrieveMedList();
    }

    /**
     * Anzeigen eines Dialogs, der den User Informiert, dass der SMS Import gestartet wird.
     */
    private void startSmsImport() {
        // TODO: Modal machen
        commandRegisterBroadcastReceiver = true;
        DialogInterface.OnClickListener d = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent smsImportStarter = new Intent(MainActivityView.this, ImportSmsView.class);
                        startActivity(smsImportStarter);
                        break;
                }
            }
        };

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("Keine Med Liste vorhanden, der SMS Import wird gestartet.")
                .setPositiveButton("OK", d)
                .show();
    }

    /**
     * Anzeigen eines Dialogs, der den User fragt, ob die bestehende MedListe
     * überschrieben werden soll.
     */
    private void userInteractOverwriteMedList() {
        // TODO: Modal machen
        DialogInterface.OnClickListener d = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        startPersistMedList();
                        break;
                }
            }
        };

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("Soll die bestehende Medikationsliste überschrieben werden?")
                .setPositiveButton("Ja", d)
                .setNegativeButton("Nein", d)
                .show();
    }

    /**
     * Speichert die aktuell, in MedListHolder, gesetzte Medikationsliste.
     * Dabei wird die bestehende MedListe überschrieben.
     */
    private class PersistMedListTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            List<Medikament> medlist = MedListHolder.getMedList();
            DatabaseAdapter da = new DatabaseAdapter(MainActivityView.this);
            da.storeMedList(medlist);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Toast.makeText(MainActivityView.this, "Medikationsliste gespeichert", Toast.LENGTH_LONG).show();
            Intent i = new Intent("MedList_Persist_Successful");
            MainActivityView.this.sendBroadcast(i);
        }
    }
    private class ScheduleAlarmsTask extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] objects) {
            AlarmController ac = new AlarmController(MainActivityView.this);
            ac.createAlarms(MedListHolder.getMedList());
            return null;
        }

    }
}
