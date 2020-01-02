package com.example.eht18_masterprojekt.Feature_Med_List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eht18_masterprojekt.Core.Medikament;
import com.example.eht18_masterprojekt.R;

import java.util.List;

public class MedListRecyclerViewAdapter extends RecyclerView.Adapter<MedListRecyclerViewAdapter.ViewHolder> {

    private List<Medikament> medList;

    public List<Medikament> getMedList() {
        return medList;
    }

    public void setMedList(List<Medikament> medList) {
        this.medList = medList;
    }


    // Diese Klasse haltet die GUI-Elemente einer Reihe der RecyclerView und
    // spezifiziert deren onClickListener.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView medikamentTextView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    return;
                }
            });
            medikamentTextView = v.findViewById(R.id.tvMedikament);
        }

        public TextView getTextView() {
            return medikamentTextView;
        }
    }

    /**
     * Spezifiziert, welches Layout xml File für die Reihen der RecyclerView verwendet werden soll
     * und baut den passenden ViewHolder dafür.
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public MedListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.med_list_view_row, parent, false);
        return new ViewHolder(itemView);
    }

    /**
     * Setzen der Inhalte eines holder an der Stelle position.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MedListRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.medikamentTextView.setText(medList.get(position).getBezeichnung());
    }

    @Override
    public int getItemCount() {
        return medList.size();
    }
}
