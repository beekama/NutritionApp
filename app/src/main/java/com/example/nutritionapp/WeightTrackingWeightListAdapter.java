package com.example.nutritionapp;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionElement;
import com.example.nutritionapp.recommendation.RecommendationNutritionAdapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class WeightTrackingWeightListAdapter extends RecyclerView.Adapter {
    Context context;
    LinkedHashMap<LocalDateTime, Integer> entries;
    public WeightTrackingWeightListAdapter(Context applicationContext, LinkedHashMap<LocalDateTime, Integer> entries) {
        context = applicationContext;
        this.entries = entries;
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
            lvh.itemView.setOnClickListener(v -> {
                Log.w("ONC", "Short Click");
            });
            lvh.itemView.setOnLongClickListener(v -> {
                Log.w("ONC", "Long Click");
                return false;
            });

        }

        @Override
        public int getItemCount() {
            return entries.size();
        }

        private static class LocalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            public LocalViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {

            }
        }
    }
