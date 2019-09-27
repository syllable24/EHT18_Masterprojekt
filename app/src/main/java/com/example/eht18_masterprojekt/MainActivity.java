package com.example.eht18_masterprojekt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView serviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceList = findViewById(R.id.rvActivityList);

        MainActivityModel m = new MainActivityModel(getAppServices());
        MainActivity v = new MainActivity();
        MainActivityController c = new MainActivityController(m, v);

        c.updateView();
    }

    /**
     * Displays serviceList in RecyclerView.
     * @param serviceList list to Display
     */
    public void printServiceList(List<String> serviceList){
        /*
        adapter = new ServiceListRecycleViewAdapter(MainActivity.this, feedsList);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(FeedItem item) {
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_LONG).show();
            }
        });
        */
    }

    /**
     * Returns List of possible services the app provides.
     * @return string list of services
     */
    private List<String> getAppServices(){
        List<String> sampleServices = new ArrayList<>();
        sampleServices.add("Import SMS");
        sampleServices.add("View Medication");
        return sampleServices;
    }

    /*
    private class ServiceListRecyclerViewAdapter extends RecyclerView.Adapter<ServiceListRecyclerViewAdapter.ViewHolder>{
        List<String> data;

        private ServiceListRecyclerViewAdapter(List<String> data){
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.service_list_row, parent, false);
            ViewHolder viewHolder = new ViewHolder(listItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ServiceListRecyclerViewAdapter.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
    */
}
