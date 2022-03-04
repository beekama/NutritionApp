package com.example.nutritionapp.recommendation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.nutritionapp.recommendation.nutritionElement.RecommendationsElement;

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
        RecommendationListItem curItem = items.get(position);
        lvh.itemContent.setText(curItem.nutritionElement.getString(this.context));

        int amount = items.get(position).target;
        if (amount >= 1000) lvh.itemPercentage.setText(format("%.2f mg", amount/1000.f));
        else lvh.itemPercentage.setText(format("%d µg", amount));

        Float nutPercentage = curItem.percentage;
        if (nutPercentage < 50) {
            lvh.progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        } else if (nutPercentage < 75) {
            lvh.progressBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
        } else {
            lvh.progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        }

        lvh.progressBar.setProgress(Math.min(Math.round(nutPercentage), 100));

        /* alarm if upper intakeLimit is exceeded */
        if (curItem.target != 0) {
            int limitPercentage = curItem.upperLimit * 100 / curItem.target;
            if (curItem.percentage > limitPercentage)
                lvh.background.setBackgroundColor(context.getResources().getColor(R.color.chartRed));
        }

        lvh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), RecommendationsElement.class);
                myIntent.putExtra("nutritionelement", (NutritionElement) curItem.nutritionElement);
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
        final View background;

        LocalViewHolder(View itemView) {
            super(itemView);
            background = itemView;
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
