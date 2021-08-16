package com.example.nutritionapp;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WeightTrackingDropdownRVAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<Pair<String, Integer>> items;
    private UpdatePeriod period;

    public WeightTrackingDropdownRVAdapter(Context context, ArrayList<Pair<String, Integer>> items, UpdatePeriod period) {
        this.context = context;
        this.items = items;
        this.period = period;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.weight_tracking_dropdown_item, parent, false);
        return new LocalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LocalViewHolder lvh = (LocalViewHolder) holder;
        lvh.text.setText(items.get(position).first);

        lvh.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, items.get(position).first, Toast.LENGTH_SHORT).show();
                period.setPeriod(items.get(position));
                //todo set new item
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class LocalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView text;


        LocalViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text1);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

        }
    }
}
