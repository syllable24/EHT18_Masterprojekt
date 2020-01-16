package com.example.eht18_masterprojekt.Feature_Med_List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.example.eht18_masterprojekt.Core.Medikament;
import com.example.eht18_masterprojekt.Feature_Database.DatabaseAdapter;
import com.example.eht18_masterprojekt.Feature_SMS_Import.ImportSmsView;
import com.example.eht18_masterprojekt.R;

import java.util.List;

public class MainActivityView extends AppCompatActivity {

    private RecyclerView rv_medList;
    private IntentFilter medListInitFilter = new IntentFilter("MedList_Init_Successful");
    private boolean regBroadcastReceiver = false;
    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            displayMedList(MedListHolder.getMedList());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean medListFound = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv_medList = findViewById(R.id.rv_med_list);

        List<Medikament> medList = queryMedList();

        if (medList.size() == 0){
            regBroadcastReceiver = true;
            userInteractStartSmsImport();
        }
        else{
            MedListHolder.setMedList(medList);
            displayMedList(medList);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (regBroadcastReceiver) {
            unregisterReceiver(br);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (regBroadcastReceiver){
            registerReceiver(br, medListInitFilter);
        }
    }

    /**
     * Anzeigen der MedList in RecyclerView
     * @param medList
     */
    private void displayMedList(List<Medikament> medList){
        Log.d("MedList", "Display Init. MedCount: " + medList.size());
        MedListRecyclerViewAdapter mra = new MedListRecyclerViewAdapter(this);
        mra.setMedList(medList);
        rv_medList.setAdapter(mra);
    }

    /**
     * Retournieren einer gespeicherten MedListe aus SQLite DB
     * @return Gespeicherte MedList
     */
    private List<Medikament> queryMedList(){
        DatabaseAdapter da = new DatabaseAdapter(this);
        da.open();
        return da.retrieveMedList();
    }

    /**
     * Anzeigen eines Dialogs, der den User Informiert, dass der SMS Import gestartet wird.
     */
    private void userInteractStartSmsImport() {

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
}
