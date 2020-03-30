package com.example.eht18_masterprojekt.Feature_Med_List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eht18_masterprojekt.Core.Medikament;
import com.example.eht18_masterprojekt.R;

import java.time.LocalTime;
import java.util.List;

public class MedListRecyclerViewAdapter extends RecyclerView.Adapter<MedListRecyclerViewAdapter.ViewHolder> {
    private List<Medikament> medList;
    private Context context;

    public MedListRecyclerViewAdapter(Context ctx){
        super();
        context = ctx;
    }

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
        private final LinearLayout layoutMedList;
        private final TableLayout tableMedEinnahme;

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
            layoutMedList = v.findViewById(R.id.layout_medList);
            tableMedEinnahme = v.findViewById(R.id.table_layout_einnahmen);
        }
    }

    /**
     * Spezifiziert, welches Layout xml File für die Reihen der RecyclerView verwendet werden soll
     * und baut den passenden ViewHolder dafür.
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
     * Setzen der Inhalte des übergebenen holders an der übergebenen Stelle position.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MedListRecyclerViewAdapter.ViewHolder holder, int position) {
        Medikament med = medList.get(position);
        String bezeichnung = med.getBezeichnung();
        Medikament.MedEinnahmeProtokoll medEinnahmeProtokoll = med.getEinnahmeProtokoll();

        holder.medikamentTextView.setText(bezeichnung);

        // TODO: Sortierung nach Uhrzeit einbauen
        for (Medikament.MedEinnahme e : medEinnahmeProtokoll){
            TableRow singleEinnahme = new TableRow(context);
            ImageView ivEinnahmeIcon = new ImageView(context);
            TextView tvEinnahmeBeschreibung = new TextView(context);

            LocalTime einnahmeZeit = e.getEinnahmeZeit();
            setEinnahmeIcon(ivEinnahmeIcon, einnahmeZeit);

            tvEinnahmeBeschreibung.setText(e.toString());
            tvEinnahmeBeschreibung.setTextSize(24);

            singleEinnahme.addView(ivEinnahmeIcon);
            singleEinnahme.addView(tvEinnahmeBeschreibung);

            holder.tableMedEinnahme.addView(singleEinnahme);
        }
    }

    @Override
    public int getItemCount() {
        return medList.size();
    }

    private void setEinnahmeIcon(ImageView ivEinnahmeIcon, LocalTime einnahmeZeit){
        if (einnahmeZeit.compareTo(LocalTime.of(10,0)) < 0){
            ivEinnahmeIcon.setImageResource(R.mipmap.ic_morning);
        }
        else if (einnahmeZeit.compareTo(LocalTime.of(14,0)) < 0){
            ivEinnahmeIcon.setImageResource(R.mipmap.ic_midday);
        }
        else if (einnahmeZeit.compareTo(LocalTime.of(18,0)) < 0){
            ivEinnahmeIcon.setImageResource(R.mipmap.ic_evening);
        }
        else if (einnahmeZeit.compareTo(LocalTime.of(23,59, 59)) < 0){
            ivEinnahmeIcon.setImageResource(R.mipmap.ic_night);
        }
    }
}

