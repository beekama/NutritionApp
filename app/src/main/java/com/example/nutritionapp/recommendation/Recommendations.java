package com.example.nutritionapp.recommendation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionapp.R;
import com.example.nutritionapp.Test_chart;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.NutritionElement;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Recommendations extends AppCompatActivity {

    private Database db;
    HashMap<Integer, ArrayList<Food>> foodList;
    ArrayList<Food> allFood ;
    NutritionAnalysis nutritionAnalysis;


    LocalDate startDateParsed = LocalDate.now();
    LocalDate endDateParsed = LocalDate.now();


    public void onCreate(Bundle savedInstanceState) {
        //splash screen when needed:
        setTheme(R.style.AppTheme);

        //basic settings:
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendation);
        db = new Database(this);
        foodList = db.getLoggedFoodsByDate(startDateParsed, endDateParsed);


        //replace actionbar with custom app_toolbar:
        Toolbar tb = findViewById(R.id.toolbar);
        TextView tb_title = findViewById(R.id.toolbar_title);
        ImageButton tb_back = findViewById(R.id.toolbar_back);
        ImageButton tb_forward = findViewById(R.id.toolbar_forward);

        //visible title:
        tb_back.setImageResource(R.drawable.ic_arrow_back_black_24dp);

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

        // NutritionAnalysis-data:
        allFood = db.getFoodsFromHashmap(foodList);

        // add nutrition items:
        ArrayList<RecommendationListItem> nutritionItems = new ArrayList<>();
        ListView mainLv = findViewById(R.id.listview);
        if (!(allFood.isEmpty())) {
            nutritionAnalysis = new NutritionAnalysis(allFood);
            for (NutritionElement ne : NutritionElement.values()) {
                nutritionItems.add(new RecommendationListItem(ne.toString(), nutritionAnalysis.getNutritionPercentage().get(ne)));// ;
            }

        }

        //adapter:
        RecommendationAdapter newAdapter = new RecommendationAdapter(getApplicationContext(), nutritionItems);
        mainLv.setAdapter(newAdapter);


        //back home button:
        tb_back.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }));
    }

}


