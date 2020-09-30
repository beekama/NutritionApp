package com.example.nutritionapp.recommendation;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionapp.R;
import com.example.nutritionapp.Test_chart;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.NutritionElement;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;


public class Recommendations extends AppCompatActivity {

    private Database db;
    HashMap<Integer, ArrayList<Food>> foodList;
    ArrayList<Food> allFood;

    LocalDate currentDateParsed = LocalDate.now();


    public void onCreate(Bundle savedInstanceState) {
        //splash screen when needed:
        setTheme(R.style.AppTheme);

        //basic settings:
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendation);
        db = new Database(this);
        foodList = db.getLoggedFoodsByDate(currentDateParsed, currentDateParsed);


        /* APP TOOLBAR */
        //replace actionbar with custom app_toolbar:
        Toolbar tb = findViewById(R.id.toolbar);
        TextView tb_title = findViewById(R.id.toolbar_title);
        ImageButton tb_back = findViewById(R.id.toolbar_back);
        ImageButton tb_forward = findViewById(R.id.toolbar_forward);

        //visible title:
        tb_back.setImageResource(R.drawable.ic_arrow_back_black_24dp);

        //back home button:
        tb_back.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }));

        //FOR CHART TESTING
        tb_forward.setImageResource(R.drawable.add_circle_filled);
        tb.setTitle("");
        tb_title.setText("RECOMMENDATIONS");
        setSupportActionBar(tb);
        //refresh:
        tb_forward.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // updateRecommendations(true);
                Intent myIntent = new Intent(v.getContext(), Test_chart.class);
                startActivity(myIntent);
            }
        }));


        /* BARCHART - WEEEK */
        //styling
        BarChart chartWeek = findViewById(R.id.barchartWeek);
        chartWeek.setPinchZoom(false);
        chartWeek.setDrawBarShadow(false);
        chartWeek.setDrawValueAboveBar(true);
        chartWeek.getDescription().setText(currentDateParsed.minusWeeks(1).toString() + " - " + currentDateParsed.toString());
        chartWeek.setDrawGridBackground(false);
        XAxis xAxis = chartWeek.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);
        YAxis yAxis = chartWeek.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setSpaceTop(15f);
        yAxis.setGranularity(1f);
        yAxis.setAxisMinimum(0f);

        //data
        ArrayList<Food> foods = db.getFoodsFromHashmap(db.getLoggedFoodsByDate(currentDateParsed, currentDateParsed.minusWeeks(1)));
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        if (!(foods.isEmpty())) {
            NutritionAnalysis weekNutritionAnalysis = new NutritionAnalysis(foods);
            int index =0;
            for (NutritionElement ne : NutritionElement.values()) {
                barEntries.add(new BarEntry(index++,Math.round(weekNutritionAnalysis.getNutritionPercentage().get(ne))));
            }
        }

        ArrayList<String> xAxisLabels = new ArrayList<>();
        for(NutritionElement ne : NutritionElement.values()){
            xAxisLabels.add(ne.toString());
        }
        xAxis.setValueFormatter(new ValueFormatter() {
                                    @Override
                                    public String getAxisLabel(float value, AxisBase axis) {
                                        return xAxisLabels.get((int) value);
                                    }
                                });
        BarDataSet barDataSet = new BarDataSet(barEntries, "cells");
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet);
        BarData data = new BarData(barDataSet);
        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);
        chartWeek.setData(data);
         barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        chartWeek.animateY(5000);






        /* LISTVIEW */
        // NutritionAnalysis-data:
        allFood = db.getFoodsFromHashmap(foodList);

        // add nutrition items:
        ListView mainLv = findViewById(R.id.listview);
        ArrayList<RecommendationListItem> listItems = generateAdapterContent(currentDateParsed, db);

        //adapter:
        RecommendationAdapter newAdapter = new RecommendationAdapter(getApplicationContext(), listItems);
        mainLv.setAdapter(newAdapter);

        //date textview:
        TextView currentDate = findViewById(R.id.textviewDate);
        currentDate.setText(currentDateParsed.toString());


        /* SWITCH BETWEEN DAYS */
        //dateBack:
        Button dateBack = findViewById(R.id.dateBackButton);
        dateBack.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDateParsed = currentDateParsed.minusDays(1);
                currentDate.setText(currentDateParsed.toString());
                ArrayList<RecommendationListItem> listItems = generateAdapterContent(currentDateParsed, db);
                RecommendationAdapter foredeayAdapter = new RecommendationAdapter(getApplicationContext(), listItems);
                mainLv.setAdapter(foredeayAdapter);
            }
        }));

        //dateForeward:
        Button dateForeward = findViewById(R.id.dateForewardButton);
        dateForeward.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDateParsed = currentDateParsed.plusDays(1);
                currentDate.setText(currentDateParsed.toString());  //todo only update?
                ArrayList<RecommendationListItem> listItems = generateAdapterContent(currentDateParsed, db);
                RecommendationAdapter nextdayAdapter = new RecommendationAdapter(getApplicationContext(), listItems);
                mainLv.setAdapter(nextdayAdapter);
            }
        }));



    }


    ArrayList<RecommendationListItem> generateAdapterContent(LocalDate currentDateParsed, Database db) {
        /* generate Adapter-content for RecommendationAdapter */

        ArrayList<Food> foods = db.getFoodsFromHashmap(db.getLoggedFoodsByDate(currentDateParsed, currentDateParsed));
        ArrayList<RecommendationListItem> listItems = new ArrayList<>();
        if (!(foods.isEmpty())) {
            NutritionAnalysis dayNutritionAnalysis = new NutritionAnalysis(foods);
            for (NutritionElement ne : NutritionElement.values()) {
                listItems.add(new RecommendationListItem(ne.toString(), dayNutritionAnalysis.getNutritionPercentage().get(ne)));
            }
        }
        return listItems;
    }

}


