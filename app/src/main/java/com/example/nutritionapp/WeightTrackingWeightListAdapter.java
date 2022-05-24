package com.example.nutritionapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.other.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class WeightTrackingWeightListAdapter extends RecyclerView.Adapter {
    Context context;
    LinkedHashMap<LocalDate, Integer> entries;
    List<LocalDate> entryKeys;
    TransferWeight tw;

    private static final int HEADER_TYPE = 0;
    private static final int ITEM_TYPE = 1;

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

        /* update entryKeys after external data change */
        entryKeys = new ArrayList<>(entries.keySet());
        entryKeys.sort(Collections.reverseOrder());

        if (viewType == HEADER_TYPE){
            View view = inflater.inflate(R.layout.weight_tracking_header_item, parent, false);
            return new HeaderViewHolder(view);
        } else if(viewType == ITEM_TYPE){
            View view = inflater.inflate(R.layout.weight_tracking_list_item, parent, false);
            return new LocalViewHolder(view);
        } else {
            throw new RuntimeException("item matches no viewType. No implementation for type : " + viewType);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder){
            HeaderViewHolder hvh = (HeaderViewHolder) holder;
            hvh.date.setText(R.string.date);
            hvh.weight.setText(R.string.weight);
        } else if (holder instanceof LocalViewHolder){
            LocalViewHolder lvh = (LocalViewHolder) holder;

            //ignore header in positionCount
            int relPosition = position -1;
            lvh.date.setText(entryKeys.get(relPosition).toString());
            lvh.weight.setText(Float.toString(Utils.intWeightToFloat(entries.get(entryKeys.get(relPosition)))));
            lvh.itemView.setOnClickListener(v -> {
                Log.w("ONC", "Short Click");
            });
            lvh.itemView.setOnLongClickListener(v -> {
                removeEntry(entryKeys.get(relPosition), relPosition);
                return false;
            });

    }}

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return HEADER_TYPE;
        return ITEM_TYPE;
    }

    @Override
    public int getItemCount() {
        // listItems + header
        return entries.size() + 1;
    }


    void removeEntry(LocalDate entry, int position) {
        tw.removeEntry(entries.get(entry), entry);
        entries.remove(entryKeys.get(position));
        entryKeys = new ArrayList(entries.keySet());
        Collections.sort(entryKeys, Collections.reverseOrder());
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


    private class HeaderViewHolder extends RecyclerView.ViewHolder{
        final TextView date;
        final TextView weight;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.headerDate);
            weight = itemView.findViewById(R.id.headerWeight);
        }
    }
}
