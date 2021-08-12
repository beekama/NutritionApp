package com.example.nutritionapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nutritionapp.recommendation.nutritionElement.RecommendationsElement;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.MainActivity;
import com.example.nutritionapp.R;
import com.example.nutritionapp.other.NutritionElement;

import java.util.ArrayList;

import static java.lang.String.*;

public class WeightTrackingPopupAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<Pair<String, Integer>> items;

    public WeightTrackingPopupAdapter(Context context, ArrayList<Pair<String, Integer>> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.weight_tracking_popup_item, parent, false);
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
                //todo set new item
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class LocalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
