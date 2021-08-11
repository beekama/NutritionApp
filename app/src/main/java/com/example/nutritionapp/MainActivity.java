package com.example.nutritionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nutritionapp.configuration.PersonalInformation;
import com.example.nutritionapp.customFoods.CustomFoodOverview;
import com.example.nutritionapp.foodJournal.FoodGroupOverview;
import com.example.nutritionapp.foodJournal.FoodJournalOverview;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.Utils;
import com.example.nutritionapp.recommendation.RecommendationListItem;
import com.example.nutritionapp.recommendation.RecommendationProteinListAdapter;
import com.example.nutritionapp.recommendation.Recommendations;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private List<Integer> colors = new ArrayList<>();
    private Database db;
    private LocalDateTime currentDateParsed;
    private LocalDate currentDateParsedLD;
    private ProgressBar energyBar;
    private TextView energyBarText;
    private PieChart pieChart;
    private RecyclerView chartList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity_main);

        /* load database on Application start */
        db = new Database(this);
        currentDateParsed = LocalDateTime.now();
        currentDateParsedLD = LocalDate.now();

        /* Setup Toolbar */
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);

        /* set title on actionBar - default "Nutrition App" (App-Name):
        //toolbar.setTitle("");
        //setTitle("lalala");

        /* drawer (navigation sidebar) */
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        /* Night Mode */
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        View navHeader = navigationView.getHeaderView(0);
        ImageButton switchToNightd = navHeader.findViewById(R.id.switchToNight);
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES)
            switchToNightd.setImageResource(R.mipmap.ic_sun_foreground);
        switchToNightd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources resources = getResources();
                switch (currentNightMode) {
                    case Configuration.UI_MODE_NIGHT_NO:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        //change background to ic_day:
                        switchToNightd.setImageResource(R.mipmap.ic_night_foreground);
                        break;
                    case Configuration.UI_MODE_NIGHT_YES:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        //change background to ic_night:
                        switchToNightd.setImageResource(R.mipmap.ic_night_foreground);
                        break;
                    //case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    default:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
                //finish();
                //startActivity(getIntent());
            }
        });


        /* ---- JOURNAL -------------------------------------------------------------------------------------- */
        View foodJournalButtonView = findViewById(R.id.food_journal);
        //    foodJournalButtonView.setBackgroundResource(R.drawable.button_ripple_animation_blue);


        TextView foodJournalButtonTitle = foodJournalButtonView.findViewById(R.id.button_title);
        ImageButton foodJournalButtonAdd = foodJournalButtonView.findViewById(R.id.button_add);
        ImageButton foodJournalButtonViewJournal = foodJournalButtonView.findViewById(R.id.button_view);
        //    TextView foodJournalButtonLeftTag = foodJournalButtonView.findViewById(R.id.buttonDescription);

        foodJournalButtonTitle.setText(R.string.foodJournalButtonTitle);
        //   foodJournalButtonLeftTag.setText(R.string.foodJournalButtonLeftTag);

/*        foodJournalButtonView.setOnClickListener(v -> {
            Intent journal = new Intent(v.getContext(), FoodJournalOverview.class);
            startActivity(journal, Utils.getDefaultTransition(this));
        });*/

        foodJournalButtonAdd.setOnClickListener(v -> {
            Intent add = new Intent(v.getContext(), FoodGroupOverview.class);
            startActivity(add, Utils.getDefaultTransition(this));
        });

        foodJournalButtonViewJournal.setOnClickListener(v -> {
            Intent viewJournal = new Intent(v.getContext(), FoodJournalOverview.class);
            startActivity(viewJournal, Utils.getDefaultTransition(this));
        });


        /* ---- CONFIGURATION -------------------------------------------------------------------------------------- */
        View configButtonView = findViewById(R.id.config);
        //  configButtonView.setBackgroundResource(R.drawable.button_ripple_animation_orange);

        TextView configButtonTitle = configButtonView.findViewById(R.id.button_title);
        TextView configButtonLeftTag = configButtonView.findViewById(R.id.buttonDescription);

        configButtonTitle.setText(R.string.configButtonTitle);
        configButtonLeftTag.setText(R.string.configButtonLeftTag);

        configButtonView.setOnClickListener(v -> {
            Intent configuration = new Intent(v.getContext(), PersonalInformation.class);
            startActivity(configuration, Utils.getDefaultTransition(this));
        });

        /* ---- CUSTOM FOOD CREATION -------------------------------------------------------------------------------------- */
        View createCustomFoodsView = findViewById(R.id.create_foods);
        //   createCustomFoodsView.setBackgroundResource(R.drawable.button_ripple_animation_purple);

        TextView createCustomFoodButtonTitle = createCustomFoodsView.findViewById(R.id.button_title);
        TextView createCustomFoodTagLef = createCustomFoodsView.findViewById(R.id.buttonDescription);

        createCustomFoodButtonTitle.setText(R.string.createFoodsButton);
        createCustomFoodTagLef.setText(R.string.createFoodsButtonLeftText);

        createCustomFoodsView.setOnClickListener(v -> {
            Intent createCustomFood = new Intent(v.getContext(), CustomFoodOverview.class);
            startActivity(createCustomFood, Utils.getDefaultTransition(this));
        });

        /* ---- RECOMMENDATION -------------------------------------------------------------------------------------- */
        View showAnalysisButtonView = findViewById(R.id.recommendations);
        //  showAnalysisButtonView.setBackgroundResource(R.drawable.button_ripple_animation_red);

        TextView analysisButtonTitle = showAnalysisButtonView.findViewById(R.id.button_title);
        Button showAnalysisButton = showAnalysisButtonView.findViewById(R.id.button_recommendation);
        energyBar = showAnalysisButtonView.findViewById(R.id.progressbar_main);
        energyBarText = showAnalysisButtonView.findViewById(R.id.progressbarTV_main);
        // TextView analysisButtonDescription = showAnalysisButtonView.findViewById(R.id.buttonDescription);

        analysisButtonTitle.setText(R.string.analysisButtonTitle);
        showAnalysisButton.setText("show Analysis");
        //  analysisButtonDescription.setText(R.string.analysisButtonLeftTag);

        //get logged foods of day:
        setEngergyBar();

        showAnalysisButtonView.setOnClickListener(v -> {
            Intent analysis = new Intent(v.getContext(), Recommendations.class);
            startActivity(analysis, Utils.getDefaultTransition(this));
        });

        /* PieChart */
        Pair<PieData, List> pieAndListData = generatePieChartContent(currentDateParsedLD);        //todo: fix or change needed from localdate to localdatetime

        pieChart = findViewById(R.id.piChartNutrition);
        pieChart.getDescription().setEnabled(false);
        PieData data = pieAndListData.first;
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTouchEnabled(false);
        data.setDrawValues(false);
        pieChart.setData(data);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
        pieChart.setDrawEntryLabels(false);

        pieChart.invalidate();


        /* PieChartList */
        chartList = findViewById(R.id.chartList);
        List<Integer> allowances = pieAndListData.second;
        LinearLayoutManager nutritionChartLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        chartList.setLayoutManager(nutritionChartLayoutManager);
        RecyclerView.Adapter<?> adapter = new RecommendationProteinListAdapter(getApplicationContext(), data, allowances);
        chartList.setAdapter(adapter);

    }

    protected void onResume() {
        super.onResume();
        //engergyBar
        setEngergyBar();
        //protein chart
        Pair<PieData, List> pieAndListData = generatePieChartContent(currentDateParsedLD);
        pieAndListData.first.setDrawValues(false);
        pieChart.setData(pieAndListData.first);
        pieChart.invalidate();
        RecyclerView.Adapter<?> adapter = new RecommendationProteinListAdapter(getApplicationContext(), pieAndListData.first, pieAndListData.second);
        chartList.setAdapter(adapter);
    }

    /* goto selected item in navigation sidebar */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_foodJournal:
                Intent journal = new Intent(this, FoodJournalOverview.class);
                startActivity(journal, Utils.getDefaultTransition(this));
                break;
            case R.id.nav_configuration:
                Intent configuration = new Intent(this, PersonalInformation.class);
                startActivity(configuration, Utils.getDefaultTransition(this));
                break;
            case R.id.nav_customFoods:
                Intent createCustomFood = new Intent(this, CustomFoodOverview.class);
                startActivity(createCustomFood, Utils.getDefaultTransition(this));
                break;
            case R.id.nav_analysis:
                Intent analysis = new Intent(this, Recommendations.class);
                startActivity(analysis, Utils.getDefaultTransition(this));
                break;
            case R.id.nav_about:
                Intent about = new Intent(this, AboutPage.class);
                startActivity(about);
                break;
            case R.id.nav_weight_tracking:
                Intent weight = new Intent(this, WeightTracking.class);
                startActivity(weight);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* toggle navigation sidebar */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void setEngergyBar() {
        ArrayList<Food> foodsOfDay = db.getFoodsFromHashMap(db.getLoggedFoodsByDate(currentDateParsed, currentDateParsed));

        NutritionAnalysis nutAnalysis = new NutritionAnalysis(foodsOfDay);
        int energyNeeded = 2000;
        int energyUsedPercentage = nutAnalysis.getTotalEnergy() * 100 / energyNeeded;


        if (energyUsedPercentage < 75) {
            energyBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        } else if (energyUsedPercentage < 125) {
            energyBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
        } else {
            energyBar.setProgressTintList(ColorStateList.valueOf(0xFFCC282e));      //todo choose color
        }

        energyBar.setProgress(Math.min(energyUsedPercentage, 100));
        String energyBarContent = String.format("Energy %d/%d", nutAnalysis.getTotalEnergy(), energyNeeded);
        energyBarText.setText(energyBarContent);
    }

    /* returns pair of PieData and allowances */
    Pair<PieData, List> generatePieChartContent(LocalDate currentDateParsed) {

        ArrayList<Food> foods = db.getFoodsFromHashMap(db.getLoggedFoodsByDate(currentDateParsed, currentDateParsed, null));
        ArrayList<RecommendationListItem> listItems = new ArrayList<>();

        /* loop foods and accumulate foodData */
        int carbSum = 0;
        int proteinSum = 0;
        int fatSum = 0;
        // factor with calories per gram
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

}
