package com.example.eht18_masterprojekt.Feature_SMS_Import;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eht18_masterprojekt.Feature_Med_List.MedListHolder;
import com.example.eht18_masterprojekt.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class SmsImportRecyclerViewAdapter extends RecyclerView.Adapter<SmsImportRecyclerViewAdapter.ViewHolder> {

    private Map<String, String> smsMap;
    private Object[] keySet;
    private Context ctx;

    SmsImportRecyclerViewAdapter(Map<String, String> smsMap, Context context){
        this.smsMap = smsMap;
        this.ctx = context;
        keySet = smsMap.keySet().toArray();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_import_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String key = (String) keySet[position];
        String[] splintInput = key.split(" ");
        String receviedFrom = splintInput[0];
        String receviedAt = splintInput[1];

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm");
        holder.smsImportReceivedFrom.setText("From: " + receviedFrom);
        holder.smsImportReceivedAt.setText("At: " + sdf.format(new Date(Long.parseLong(receviedAt))));
        holder.smsImportSmsBody.setText(smsMap.get(key));
    }

    @Override
    public int getItemCount() {
        return smsMap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView smsImportReceivedAt;
        private final TextView smsImportReceivedFrom;
        private final TextView smsImportSmsBody;

        public ViewHolder(View v) {
            super(v);
            smsImportReceivedAt   = v.findViewById(R.id.smsReceivedAt);
            smsImportReceivedFrom = v.findViewById(R.id.smsFrom);
            smsImportSmsBody  = v.findViewById(R.id.smsBody);

            // Define click listener for the ViewHolder'smsType View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogInterface.OnClickListener d = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    String key = (String) keySet[getAdapterPosition()];
                                    String[] splitKey = key.split(" ");

                                    SmsDirector sd = new SmsDirector(smsMap.get(key), splitKey[0], new Date(Long.parseLong(splitKey[1])),ctx);
                                    SMS result = sd.buildMedikamente()
                                            .buildOrdinationsInformationen()
                                            .getSms();

                                    MedListHolder.setMedList(result.getMedList());

                                    // TODO: Test me
                                    // Es sollte die ImportSMS View geschlossen werden
                                    // und die MainActivityView die importierte MedListe
                                    // anzeigen -> Review med_list_view_row.xml
                                    Intent intent = new Intent();
                                    intent.setAction("MedList_Init_Successful");
                                    ctx.sendBroadcast(intent);
                                    break;
                            }
                        }
                    };

                    String key = (String) keySet[getAdapterPosition()];
                    String[] splitKey = key.split(" ");

                    AlertDialog.Builder b = new AlertDialog.Builder(ctx);
                    b.setMessage("SMS von: " + splitKey[0] + " wirklich importieren?")
                     .setPositiveButton("Ja", d)
                     .setNegativeButton("Nein", d)
                     .show();
                }
            });
        }

        public TextView getTvReceivedAt() {
            return smsImportReceivedAt;
        }
        public TextView getTvReceivedFrom() {
            return smsImportReceivedFrom;
        }
    }
}
