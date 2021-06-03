package com.example.nutritionapp.recommendation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.MainActivity;
import com.example.nutritionapp.R;
import com.example.nutritionapp.other.NutritionElement;

import java.util.ArrayList;

import static java.lang.String.*;

public class RecommendationAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<RecommendationListItem> items;

    public RecommendationAdapter(Context context, ArrayList<RecommendationListItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.recommendation_nutritions_element, parent, false);
        return new LocalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LocalViewHolder lvh = (LocalViewHolder) holder;
        lvh.itemContent.setText(items.get(position).nutritionElement.toString());
        lvh.itemPercentage.setText(format("%d mg", items.get(position).target));

        Float nutPercentage = items.get(position).percentage;
        if (nutPercentage < 50) {
            lvh.progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        } else if (nutPercentage < 75) {
            lvh.progressBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
        } else {
            lvh.progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        }

        lvh.progressBar.setProgress(Math.min(Math.round(nutPercentage), 100));

        lvh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), RecommendationsElement.class);
                myIntent.putExtra("nutritionelement", (NutritionElement) items.get(position).nutritionElement);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(myIntent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class LocalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView itemContent;
        final ProgressBar progressBar;
        final TextView itemPercentage;

        LocalViewHolder(View itemView) {
            super(itemView);
            itemContent = itemView.findViewById(R.id.nutrition_textview);
            itemView.setOnClickListener(this);
            progressBar = itemView.findViewById(R.id.nutProgressBar);
            itemPercentage = itemView.findViewById(R.id.nutrition_target);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
