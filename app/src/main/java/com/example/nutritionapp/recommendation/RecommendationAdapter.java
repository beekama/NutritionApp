package com.example.nutritionapp.recommendation;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nutritionapp.other.ActivityExtraNames;
import com.example.nutritionapp.other.Conversions;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.recommendation.nutritionElement.RecommendationsElement;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.R;

import java.util.ArrayList;
import java.util.Locale;

import static java.lang.String.*;

public class RecommendationAdapter extends RecyclerView.Adapter {

    private final Context context;
    private final ArrayList<RecommendationListItem> items;

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
        String nutrientNativeUnit = Database.getNutrientNativeUnit(Integer.toString(Nutrition.databaseIdFromEnum(curItem.nutritionElement)));
        if (nutrientNativeUnit.equals(Conversions.MICROGRAM)){
            lvh.itemPercentage.setText(format(Locale.getDefault(), "%d µg", amount));
        }else{
            lvh.itemPercentage.setText(format(Locale.getDefault(), "%d mg", amount));
        }

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
        if (curItem.target != 0 && curItem.upperLimit != -99) {
            int limitPercentage = curItem.upperLimit * 100 / curItem.target;
            if (curItem.percentage > limitPercentage)
                lvh.background.setBackgroundColor(context.getColor(R.color.chartRed));
        }

        lvh.itemView.setOnClickListener(v -> {
            Intent myIntent = new Intent(v.getContext(), RecommendationsElement.class);
            myIntent.putExtra(ActivityExtraNames.NUTRITION_ELEMENT, curItem.nutritionElement);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            v.getContext().startActivity(myIntent);
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
