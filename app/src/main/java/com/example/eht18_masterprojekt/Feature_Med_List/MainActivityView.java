package com.example.eht18_masterprojekt.Feature_Med_List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.eht18_masterprojekt.Core.Medikament;
import com.example.eht18_masterprojekt.Feature_Database.DatabaseAdapter;
import com.example.eht18_masterprojekt.Feature_Med_List.MedListHolder;
import com.example.eht18_masterprojekt.Feature_Med_List.MedListRecyclerViewAdapter;
import com.example.eht18_masterprojekt.Feature_SMS_Import.ImportSmsView;
import com.example.eht18_masterprojekt.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivityView extends AppCompatActivity {

    private RecyclerView rv_medList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean medListFound = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv_medList = findViewById(R.id.rv_med_list);

        List<Medikament> medList = queryMedList();

        if (medList.size() == 0){
            Intent smsImportStarter = new Intent(this, ImportSmsView.class);
            startActivity(smsImportStarter);
        }
        else{
            MedListRecyclerViewAdapter mra = new MedListRecyclerViewAdapter();
            mra.setMedList(medList);
            rv_medList.setAdapter(mra);
        }
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
}
