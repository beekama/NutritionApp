package com.example.nutritionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Pair;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.recommendation.nutritionElement.RecommendationNutritionAdapter;
import com.github.mikephil.charting.charts.LineChart;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class WeightTracking extends AppCompatActivity {

    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weight_tracking);

        db = new Database(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        ImageButton toolbarBack = findViewById(R.id.toolbar_back);
        toolbar.setTitle("");
        toolbarTitle.setText("Weight Tracking");
        setSupportActionBar(toolbar);
        toolbarBack.setOnClickListener((v -> finish()));
        toolbarBack.setImageResource(R.drawable.ic_arrow_back_black_24dp);

        LineChart chartWeight = findViewById(R.id.chartWeight);
        db.createWeightsTableIfNotExist();
        LinkedHashMap<LocalDateTime, Integer> weightAll = db.getWeightAll();

        /* TODO: make chart */

        RecyclerView weights = findViewById(R.id.weightList);
        LinearLayoutManager nutritionReportLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        weights.setLayoutManager(nutritionReportLayoutManager);
        RecyclerView.Adapter<?> foodRec = new WeightTrackingWeightListAdapter(getApplicationContext(), weightAll);
        weights.setAdapter(foodRec);
    }
}