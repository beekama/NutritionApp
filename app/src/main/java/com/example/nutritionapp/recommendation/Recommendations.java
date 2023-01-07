package com.example.nutritionapp.recommendation;

import android.app.DatePickerDialog;

import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.DividerItemDecorator;
import com.example.nutritionapp.R;
import com.example.nutritionapp.deprecated.PersonalInformation;
import com.example.nutritionapp.other.ActivityExtraNames;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.NutritionElement;
import com.example.nutritionapp.other.NutritionPercentageTuple;
import com.example.nutritionapp.other.Utils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Recommendations extends AppCompatActivity {

    private Database db;
    private ProgressBar energyBar;
    private TextView energyBarText;
    private RecyclerView nutritionRecommendationList;
    private TextView dateView;
    private PieChart pieChart;
    RecyclerView chartList;
    private final List<Integer> colors = new ArrayList<>();
    private LocalDate currentDateParsed = LocalDate.now();

    public void onCreate(Bundle savedInstanceState) {
        //splash screen when needed:
        setTheme(R.style.AppTheme);

        //basic settings:
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendation);
        db = new Database(this);

        /* see if a date was specified during activity start */
        String dateFromExtra = this.getIntent().getStringExtra(ActivityExtraNames.START_DATE);
        if(dateFromExtra != null){
            currentDateParsed = LocalDate.parse(dateFromExtra, Utils.sqliteDateFormat);
        }

        /* APP TOOLBAR */
        //replace actionbar with custom app_toolbar:
        Toolbar tb = findViewById(R.id.toolbar);
        TextView tb_title = findViewById(R.id.toolbar_title);
        ImageButton tb_back = findViewById(R.id.toolbar_back);
        ImageButton tb_forward = findViewById(R.id.toolbar_forward);

        //set title
        tb.setTitle("");
        tb_title.setText(R.string.recommendationTitle);
        setSupportActionBar(tb);

        //visible title:
        tb_back.setImageResource(R.drawable.ic_arrow_back_black_24dp);

        //back home button:
        tb_back.setOnClickListener((v -> finishAfterTransition()));

        /* PROGRESS BAR */
        energyBar = findViewById(R.id.energyBar);
        energyBarText = findViewById(R.id.energyBarTextAnalysis);
        nutritionRecommendationList = findViewById(R.id.listView);

        /* PieChart */
        pieChart = findViewById(R.id.piChartNutrition);
        Pair<PieData, ArrayList<Integer>> pieAndListData = generatePieChartContent(currentDateParsed, db, colors);
        visualSetupPieChart(pieAndListData, pieChart);

        /* PieChartList */
        chartList = findViewById(R.id.chartList);
        updatePageContent(currentDateParsed);

        /* NUTRITION LIST */
        LinearLayoutManager nutritionReportLayoutManager = new LinearLayoutManager(Recommendations.this, LinearLayoutManager.VERTICAL, false);
        nutritionRecommendationList.setLayoutManager(nutritionReportLayoutManager);
        ArrayList<RecommendationListItem> listItems = generateAdapterContent(currentDateParsed);

        DividerItemDecorator dividerItemDecorator = new DividerItemDecorator(ContextCompat.getDrawable(this.getApplicationContext(),R.drawable.divider), false);
        nutritionRecommendationList.addItemDecoration(dividerItemDecorator);

        RecyclerView.Adapter<?> nutritionReport = new RecommendationAdapter(Recommendations.this, listItems);
        nutritionRecommendationList.setAdapter(nutritionReport);

        /* date view */
        dateView = findViewById(R.id.date);
        dateView.setText(currentDateParsed.format(Utils.sqliteDateFormat));
        dateView.setOnClickListener(v -> {dateUpdateDialog(currentDateParsed);});
    }

    public static void visualSetupPieChart(Pair<PieData, ArrayList<Integer>> pieAndListData, PieChart pieChart) {
        pieChart.getDescription().setEnabled(false);
        PieData data = pieAndListData.first;
        pieChart.setHoleColor(Color.TRANSPARENT);
        data.setDrawValues(false);
        pieChart.setData(data);
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.invalidate();
    }

    private void updatePageContent(LocalDate localDate) {

        setProgressBar(localDate, db, energyBar, energyBarText, this);
        ArrayList<RecommendationListItem> listItems = generateAdapterContent(localDate);
        RecommendationAdapter newDayAdapter = new RecommendationAdapter(Recommendations.this, listItems);
        nutritionRecommendationList.setAdapter(newDayAdapter);

        /* TODO maybe prevent double execution of generatePieChartContent */
        Pair<PieData, ArrayList<Integer>> pieAndListData = generatePieChartContent(localDate, this.db, this.colors);
        setChartSupportingList(pieChart, pieAndListData, this, chartList);
    }

    public static void setChartSupportingList(PieChart pieChart, Pair<PieData, ArrayList<Integer>> pieAndListData, Context context, RecyclerView chartList) {
        pieChart.setData(pieAndListData.first);
        pieChart.setTouchEnabled(false);
        pieChart.invalidate();
        RecyclerView.Adapter<?> adapter = new RecommendationProteinListAdapter(context, pieAndListData.first, pieAndListData.second);
        chartList.setAdapter(adapter);
    }

    private ArrayList<RecommendationListItem> generateAdapterContent(LocalDate currentDateParsed) {

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
        Nutrition upperLimit = Nutrition.getUpperIntakeLimit();
        ArrayList<NutritionPercentageTuple> nutritionPercentages = dayNutritionAnalysis.getNutritionPercentageSortedFilterZero();
        for (NutritionPercentageTuple net : nutritionPercentages) {
            Integer nutTarget = target.getElements().get(net.nutritionElement);
            Integer nutLimit = upperLimit.getElements().get(net.nutritionElement);
            listItems.add(new RecommendationListItem(net.nutritionElement, net.percentage, nutTarget, nutLimit));
            nonZero.put(net.nutritionElement, true);
        }

        /* display zero value if desired */
        for(NutritionElement ne : nonZero.keySet()){
            if(!nonZero.get(ne)){
                Integer nutTarget = target.getElements().get(ne);
                Integer nutLimit = target.getElements().get(ne);
                listItems.add(new RecommendationListItem(ne, 0f, nutTarget, nutLimit));
            }
        }
        return listItems;
    }

    /* returns pair of PieData and allowances */
    public static Pair<PieData, ArrayList<Integer>> generatePieChartContent(LocalDate currentDateParsed, Database db, List<Integer> colors) {

        ArrayList<Food> foods = db.getFoodsFromHashMap(db.getLoggedFoodsByDate(currentDateParsed, currentDateParsed, null));

        /* loop foods and accumulate foodData */
        int carbSum = 0;
        int proteinSum = 0;
        int fatSum = 0;

        /* factor with calories per gram */
        for (Food f : foods){
            carbSum += f.carb * 4;
            proteinSum += f.protein * 4;
            fatSum += f.fat * 9;
        }

        /* calculate percentage */
        int allowanceEnergy = PersonalInformation.ENERGY_TARGET;
        carbSum = carbSum*100/allowanceEnergy;
        proteinSum = proteinSum*100/allowanceEnergy;
        fatSum = fatSum*100/allowanceEnergy;

        /* generate PieEntries for Carbs, Protein, Fat */
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(carbSum, "carbs"));
        entries.add(new PieEntry(proteinSum, "protein"));
        entries.add(new PieEntry(fatSum, "fat"));

        /* add allowances for Carbs, Protein, Fat */
        ArrayList<Integer> allowances = new ArrayList<>();
        allowances.add(PersonalInformation.CARB_TARGET);
        allowances.add(PersonalInformation.PROTEIN_TARGET);
        allowances.add(PersonalInformation.FAT_TARGET);

        /* generate DataSet from PieEntries */
        PieDataSet set = new PieDataSet(entries, "");

        // add some colors
        colors.add(Color.BLUE);
        colors.add(Color.MAGENTA);
        colors.add(Color.YELLOW);
        colors.add(Color.RED);
        set.setColors(colors);

        PieData data = new PieData(set);
        return new Pair<>(data, allowances);
    }

    public static void setProgressBar(LocalDate currentDateParsed, Database db, ProgressBar energyBar, TextView energyBarText, Context context){

        /* create Arraylist with foods of the given day */
        ArrayList<Food> foods = db.getFoodsFromHashMap(db.getLoggedFoodsByDate(currentDateParsed, currentDateParsed, null));

        int energyUsed = Nutrition.totalEnergy(foods);
        createEnergyBar(energyBar, energyBarText, context, energyUsed);
    }

    public static void createEnergyBar(ProgressBar energyBar, TextView energyBarText, Context context, int energyUsed) {
        int energyNeeded = PersonalInformation.ENERGY_TARGET; // TODO use value from database
        int energyUsedPercentage = energyUsed *100/energyNeeded;

        if(energyUsedPercentage < 75){
            energyBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        }else if(energyUsedPercentage < 125){
            energyBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
        }else{
            energyBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        }

        energyBar.setProgress(Math.min(energyUsedPercentage, 100));
        String energyBarContent = String.format(Locale.getDefault(), context.getString(R.string.energyBarFormatString), energyUsed, energyNeeded);
        energyBarText.setText(energyBarContent);
    }

    private void dateUpdateDialog(final LocalDate localDate) {
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            LocalDate selected = LocalDate.of(year, Utils.monthAndroidToDefault(month), dayOfMonth);
            this.dateView.setText(selected.format(Utils.sqliteDateFormat));
            if (selected != localDate){
                updatePageContent(selected);
            }
        }, localDate.getYear(),Utils.monthDefaultToAndroid(localDate.getMonthValue()), localDate.getDayOfMonth());
        dialog.show();
    }
}


