package com.example.nutritionapp.recommendation;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.NutritionElement;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;

public class RecommendationsElement extends AppCompatActivity {
    private Database db;
    private NutritionElement nutritionElement;
    HashMap<Integer, ArrayList<Food>> foodList;
    ArrayList<Food> allFood;
    LocalDate currentDateParsed = LocalDate.now();


    public void onCreate(Bundle savedInstanceState) {
        /* set NutritionElement */
        Bundle b = getIntent().getExtras();
        if (b != null) {
            nutritionElement = (NutritionElement) b.get("nutritionelement");
        }
        Toast t = Toast.makeText(getApplicationContext(), nutritionElement.toString(), Toast.LENGTH_LONG);
        t.show();

        //splash screen when needed:
        setTheme(R.style.AppTheme);

        //basic settings:
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendation_nutrition);
        db = new Database(this);


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

        //set title
        tb.setTitle("");
        tb_title.setText(nutritionElement.toString());
        setSupportActionBar(tb);

        /* CHART */
        BarChart barChart = findViewById(R.id.barChartNutrition);
        Pair<BarData, ArrayList<String >> chartData = getChartData();
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(0);
        xAxis.setCenterAxisLabels(true);
        xAxis.setLabelRotationAngle(-90);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                try {
                    return chartData.second.get((int) value);
                } catch (IndexOutOfBoundsException ie){
                    Log.wtf("labelentry not found", Float.toString(value)); //todo: why do we get here
                    return Float.toString(value);
                }
            }
        });
        barChart.setData(chartData.first);
        barChart.invalidate();

    }

    Pair<BarData, ArrayList<String>> getChartData() {
        ArrayList<String> xAxisLabels = new ArrayList<>();
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int day = 6; day >= 0; day--) {
            xAxisLabels.add(currentDateParsed.minusDays(day).getDayOfWeek().toString());
            Integer amount = 0;
            HashMap<Integer, ArrayList<Food>> foodgroups = db.getLoggedFoodsByDate(currentDateParsed.minusDays(day), currentDateParsed.minusDays(day));
            ArrayList<Food> foods = (foodgroups.isEmpty()) ? null : db.getFoodsFromHashMap(foodgroups);
            if (foods != null) {
                NutritionAnalysis nutritionAnalysis = new NutritionAnalysis(foods);
                Nutrition nutrition = nutritionAnalysis.getNutritionActual();
                amount = nutrition.getElements().get(nutritionElement);
            }
            barEntries.add(new BarEntry(6-day, amount));
        }

        BarDataSet set = new BarDataSet(barEntries, "NutritionElementSet");
        BarData data = new BarData(set);
        return new Pair<>(data, xAxisLabels);
    }
}
