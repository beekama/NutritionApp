package com.example.nutritionapp.recommendation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CpuUsageInfo;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.NutritionElement;
import com.example.nutritionapp.other.NutritionPercentageTuple;
import com.example.nutritionapp.other.Utils;
import com.example.nutritionapp.recommendation.nutritionElement.RecommendationNutritionAdapter;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;


import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Recommendations extends AppCompatActivity {



    private Database db;
    private ProgressBar energyBar;
    private TextView energyBarText;
    private RecyclerView nutritionRList;
    private TextView dateView;
    private PieChart pieChart;
    private List<Integer> colors = new ArrayList<>();


    private LocalDate currentDateParsed = LocalDate.now();


    public void onCreate(Bundle savedInstanceState) {
        //splash screen when needed:
        setTheme(R.style.AppTheme);

        //basic settings:
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendation);
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



        /* PROGRESS BAR */
        energyBar = findViewById(R.id.energyBar);
        energyBarText = findViewById(R.id.energyBarTextAnalysis);
        setProgressBar(currentDateParsed);

        /* PieChart */
        pieChart = findViewById(R.id.piChartNutrition);
        pieChart.getDescription().setEnabled(false);
        PieData data = generatePieChartContent(currentDateParsed);
        data.setDrawValues(false);
        pieChart.setData(data);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
        pieChart.setDrawEntryLabels(false);

        pieChart.invalidate();

        /* PieChartList */
        RecyclerView chartList = findViewById(R.id.chartList);
        LinearLayoutManager nutritionChartLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        chartList.setLayoutManager(nutritionChartLayoutManager);
        RecyclerView.Adapter<?> adapter = new RecommendationProteinListAdapter(getApplicationContext(), data);
        chartList.setAdapter(adapter);


        /* NUTRITION LIST */
        // add nutrition items:
        nutritionRList = findViewById(R.id.listView);
        LinearLayoutManager nutritionReportLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        nutritionRList.setLayoutManager(nutritionReportLayoutManager);
        ArrayList<RecommendationListItem> listItems = generateAdapterContent(currentDateParsed);

        RecyclerView.Adapter<?> nutritionReport = new RecommendationAdapter(getApplicationContext(), listItems);
        nutritionRList.setAdapter(nutritionReport);

        dateView = findViewById(R.id.date);
        dateView.setText(currentDateParsed.format(Utils.sqliteDateFormat));
        dateView.setOnClickListener(v -> {dateUpdateDialog(currentDateParsed);});
    }


    private void updateNutritionList(LocalDate localDate) {
        ArrayList<RecommendationListItem> listItems = generateAdapterContent(localDate);
        RecommendationAdapter newDayAdapter = new RecommendationAdapter(getApplicationContext(), listItems);
        nutritionRList.setAdapter(newDayAdapter);
    }


    ArrayList<RecommendationListItem> generateAdapterContent(LocalDate currentDateParsed) {

        /* generate Adapter-content for RecommendationAdapter */
        ArrayList<Food> foods = db.getFoodsFromHashMap(db.getLoggedFoodsByDate(currentDateParsed, currentDateParsed, null));
        ArrayList<RecommendationListItem> listItems = new ArrayList<>();

        /* track source of display elements */
        HashMap<NutritionElement, Boolean> nonZero = new HashMap<>();
        for(NutritionElement ne : NutritionElement.values()){
            nonZero.put(ne, false);
        }

        /* display sorted non-zero percentages */
        NutritionAnalysis dayNutritionAnalysis = new NutritionAnalysis(foods);
        Nutrition target = Nutrition.getRecommendation();
        ArrayList<NutritionPercentageTuple> nutritionPercentages = dayNutritionAnalysis.getNutritionPercentageSortedFilterZero();
        for (NutritionPercentageTuple net : nutritionPercentages) {
            Integer nutTarget = target.getElements().get(net.nutritionElement);
            listItems.add(new RecommendationListItem(net.nutritionElement, net.percentage, nutTarget));
            nonZero.put(net.nutritionElement, true);
        }

        /* display zero value if desired */
        for(NutritionElement ne : nonZero.keySet()){
            if(!nonZero.get(ne)){
                Integer nutTarget = target.getElements().get(ne);
                listItems.add(new RecommendationListItem(ne, 0f, nutTarget));
            }
        }

        return listItems;
    }


    PieData generatePieChartContent(LocalDate currentDateParsed) {

        ArrayList<Food> foods = db.getFoodsFromHashMap(db.getLoggedFoodsByDate(currentDateParsed, currentDateParsed, null));
        ArrayList<RecommendationListItem> listItems = new ArrayList<>();

        /* loop foods and accumulate foodData */
        int carbSum = 0;
        int proteinSum = 0;
        int fatSum = 0;
        for (Food f : foods){
            carbSum += f.carb;
            proteinSum += f.protein;
            fatSum += f.fat;
        }

        /* generate PieEntries for Carbs, Protein, Fat */
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(carbSum, "carbs"));
        entries.add(new PieEntry(proteinSum, "protein"));
        entries.add(new PieEntry(fatSum, "fat"));

        /* generate DataSet from PieEntries */
        PieDataSet set = new PieDataSet(entries, "");

        // add some colors
        colors.add(Color.BLUE);
        colors.add(Color.MAGENTA);
        colors.add(Color.YELLOW);
        colors.add(Color.RED);
        set.setColors(colors);

        PieData data = new PieData(set);
        return data;

    }




    void setProgressBar(LocalDate currentDateParsed){

        //create Arraylist with foods of the given day:
        ArrayList<Food> foods = db.getFoodsFromHashMap(db.getLoggedFoodsByDate(currentDateParsed, currentDateParsed, null));

        int energyUsed = Nutrition.totalEnergy(foods);
        int energyNeeded = 2000;
        int energyUsedPercentage = energyUsed*100/energyNeeded;

        if(energyUsedPercentage < 75){
            energyBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        }else if(energyUsedPercentage < 125){
            energyBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
        }else{
            energyBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        }

        energyBar.setProgress(Math.min(energyUsedPercentage, 100));
        String energyBarContent = String.format("Energy %d/%d", energyUsed, energyNeeded);
        energyBarText.setText(energyBarContent);
    }


    private void dateUpdateDialog(final LocalDate localDate) {
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            LocalDate selected = LocalDate.of(year, month, dayOfMonth);
            this.dateView.setText(selected.format(Utils.sqliteDateFormat));
            if (selected != localDate){
                updateNutritionList(selected);
                setProgressBar(selected);
            }
        }, localDate.getYear(),localDate.getMonthValue(), localDate.getDayOfMonth());
        dialog.show();
    }



}


