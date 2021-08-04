package com.example.nutritionapp;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.other.Utils;
import com.github.mikephil.charting.data.Entry;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class WeightTrackingWeightListAdapter extends RecyclerView.Adapter {
    Context context;
    LinkedHashMap<LocalDate, Integer> entries;
    List<LocalDate> keys;
    TransferWeight tw;

    public WeightTrackingWeightListAdapter(Context applicationContext, LinkedHashMap<LocalDate, Integer> entries, TransferWeight tw) {
        context = applicationContext;
        this.entries = entries;
        this.tw = tw;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.wight_tracking_list_item, parent, false);
        keys = new ArrayList(entries.keySet());
        Collections.sort(keys, Collections.reverseOrder());
        return new LocalViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LocalViewHolder lvh = (LocalViewHolder) holder;
        lvh.date.setText(keys.get(position).toString());
        lvh.weight.setText(entries.get(keys.get(position)).toString());
        lvh.itemView.setOnClickListener(v -> {
            Log.w("ONC", "Short Click");
        });
        lvh.itemView.setOnLongClickListener(v -> {
            removeEntry(keys.get(position), position);
            return false;
        });

    }

    @Override
    public int getItemCount() {
        return entries.size();
    }


    void removeEntry(LocalDate entry, int position) {
        tw.removeEntry(entries.get(entry), entry);
        entries.remove(keys.get(position));
        keys = new ArrayList(entries.keySet());
        Collections.sort(keys, Collections.reverseOrder());
    }


    static class LocalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView date;
        TextView weight;

        public LocalViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            date = itemView.findViewById(R.id.trackingDate);
            weight = itemView.findViewById(R.id.trackingWeight);
        }

        @Override
        public void onClick(View v) {
        }
    }

}
