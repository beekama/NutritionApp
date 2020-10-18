package com.example.nutritionapp.recommendation;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.NutritionElement;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecommendationsWeek extends AppCompatActivity {

    private Database db;
    HashMap<Integer, ArrayList<Food>> foodList;
    ArrayList<Food> allFood;
    LocalDate currentDateParsed = LocalDate.now();

    public void onCreate(Bundle savedInstanceState) {
        //splash screen when needed:
        setTheme(R.style.AppTheme);

        //basic settings:
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendation_week);
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

        /*         BARCHART - WEEEK */
        //styling
        BarChart chartWeek = findViewById(R.id.barchartWeekW);
        chartWeek.setPinchZoom(false);
        chartWeek.setDrawBarShadow(false);
        chartWeek.setDrawValueAboveBar(true);
        chartWeek.getDescription().setText("");
        chartWeek.setDrawGridBackground(false);
        XAxis xAxis = chartWeek.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        YAxis yAxis = chartWeek.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setSpaceTop(15f);
        yAxis.setGranularity(1f);
        yAxis.setAxisMinimum(0f);
        chartWeek.animateY(1000);

        //no labels since we use legend
        xAxis.setDrawLabels(false);
        //xAxis.setLabelCount(7);

        //data
        setDataWeekChart(xAxis,chartWeek);

        //date textview:
        TextView currentDate = findViewById(R.id.textviewDateW);
        currentDate.setText(currentDateParsed.minusWeeks(1).toString() + " to " + currentDateParsed.toString());

        /* SWITCH BETWEEN DAYS */
        //dateBack:
        Button dateBack = findViewById(R.id.dateBackButton);
        dateBack.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDateParsed = currentDateParsed.minusDays(1);
                currentDate.setText(currentDateParsed.toString());
                //update weekchart:
                setDataWeekChart(xAxis,chartWeek);
            }
        }));

        //dateForeward:
        Button dateForeward = findViewById(R.id.dateForewardButton);
        dateForeward.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDateParsed = currentDateParsed.plusDays(1);
                currentDate.setText(currentDateParsed.toString());
                //update weekchart:
                setDataWeekChart(xAxis,chartWeek);
            }
        }));
    }




    void setDataWeekChart(XAxis xAxis, BarChart chartWeek){

        ArrayList<String> xAxisLabels = new ArrayList<>();
        ArrayList<IBarDataSet> barDataSets = new ArrayList<>();

        /* get foods of week from database */
        ArrayList<Food> foods = db.getFoodsFromHashmap(db.getLoggedFoodsByDate(currentDateParsed, currentDateParsed.minusWeeks(1)));

        /* add food to chart */
        if (!(foods.isEmpty())){
            NutritionAnalysis weekNutritionAnalysis = new NutritionAnalysis(foods);
            int xValue = 0;
            int[] colors = super.getApplicationContext().getResources().getIntArray(R.array.chartColorCollection);
            for (NutritionElement ne : NutritionElement.values()){
                ArrayList<BarEntry> barEntries = new ArrayList<>();
                BarEntry barEntry = new BarEntry(xValue++,weekNutritionAnalysis.getNutritionPercentageMultipleDays(7).get(ne));
                barEntries.add(barEntry);
                BarDataSet barDataSet = new BarDataSet(barEntries,ne.toString());
                //add color:
                barDataSet.setColor(colors[xValue]);
                barDataSets.add((IBarDataSet) barDataSet);
                Legend l = chartWeek.getLegend();
                l.setWordWrapEnabled(true);
                //collection labels for x-axis:
                //xAxisLabels.add(ne.toString());
            }
        }

/*        *//* set x-axis label *//*
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return xAxisLabels.get((int) value);
            }
        });*/



/*        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet);*/
        BarData data = new BarData(barDataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);
        chartWeek.setData(data);
        /*barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);*/
        data.setHighlightEnabled(false);
        chartWeek.setHighlightPerTapEnabled(false);
        chartWeek.invalidate();
    }

}
