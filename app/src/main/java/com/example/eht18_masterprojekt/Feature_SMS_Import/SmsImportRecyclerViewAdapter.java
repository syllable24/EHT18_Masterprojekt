package com.example.eht18_masterprojekt.Feature_SMS_Import;

import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eht18_masterprojekt.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class SmsImportRecyclerViewAdapter extends RecyclerView.Adapter<SmsImportRecyclerViewAdapter.ViewHolder> {

    private List<SMS> smsList;

    public List<SMS> getSmsList() {
        return smsList;
    }

    public void setSmsList(List<SMS> smsList) {
        this.smsList = smsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_import_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        holder.smsImportReceivedFrom.setText(smsList.get(position).getSender());
        holder.smsImportReceivedAt.setText(sdf.format(smsList.get(position).getReceivedAt()));
    }

    @Override
    public int getItemCount() {
        return smsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView smsImportReceivedAt;
        private final TextView smsImportReceivedFrom;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder'smsType View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    return;
                }
            });
            smsImportReceivedAt   = v.findViewById(R.id.tvMedikament);
            smsImportReceivedFrom = v.findViewById(R.id.tvMedikament);
        }

        public TextView getTvReceivedAt() {
            return smsImportReceivedAt;
        }
        public TextView getTvReceivedFrom() {
            return smsImportReceivedFrom;
        }
    }


}
