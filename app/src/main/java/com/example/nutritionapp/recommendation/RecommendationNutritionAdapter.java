package com.example.nutritionapp.recommendation;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.R;

import java.util.ArrayList;

public class RecommendationNutritionAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<Pair<String, Float>> recFood;

    public RecommendationNutritionAdapter(Context context, ArrayList<Pair<String, Float>> list){
        this.context = context;
        this.recFood = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.recommendation_recommended_food, parent, false);
        return new LocalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LocalViewHolder lvh = (LocalViewHolder) holder;
        Pair<String, Float> pair = recFood.get(position);
        lvh.foodName.setText(pair.first);
        lvh.amount.setText(pair.second.toString());

        String microgr = context.getResources().getString(R.string.microgram);
        lvh.unity.setText(microgr + "/100mg" );
    }

    @Override
    public int getItemCount() {
        return recFood.size();
    }

    private class LocalViewHolder extends RecyclerView.ViewHolder{
        final TextView foodName;
        final TextView amount;
        final TextView unity;

        public LocalViewHolder(View view) {
            super(view);
            foodName = view.findViewById(R.id.recommendedFoodText);
            amount = view.findViewById(R.id.recommendedFoodAmount);
            unity = view.findViewById(R.id.unity);
        }
    }
}
