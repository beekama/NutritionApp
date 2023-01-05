package com.example.nutritionapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.MainActivity;
import com.example.nutritionapp.R;
import com.example.nutritionapp.WeightTracking;
import com.example.nutritionapp.configuration.PersonalInformation;
import com.example.nutritionapp.customFoods.CustomFoodOverview;
import com.example.nutritionapp.foodJournal.FoodGroupOverview;
import com.example.nutritionapp.foodJournal.FoodJournalOverview;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.LocaleHelper;
import com.example.nutritionapp.other.Utils;
import com.example.nutritionapp.recommendation.RecommendationProteinListAdapter;
import com.example.nutritionapp.recommendation.Recommendations;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StartPageFragment extends Fragment {
    private Database db;
    private LocalDate currentDateParsed;
    private final List<Integer> colors = new ArrayList<>();
    private DrawerLayout drawer;
    private ProgressBar energyBar;
    private TextView energyBarText;
    private PieChart pieChart;
    private RecyclerView chartList;

    public StartPageFragment() {
        super(R.layout.fragment_startpage);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){ //todo change back
        /* load database */
        db = new Database((MainActivity) getActivity());
        currentDateParsed = LocalDate.now();

        /* try to set language according to db, if empty set db to system-default */
        //todo



        /* ---- JOURNAL ------*/
        View foodJournalButtonView = view.findViewById(R.id.food_journal);
        TextView foodJournalButtonTitle = foodJournalButtonView.findViewById(R.id.recommendation_button_title);
        ImageButton foodJournalButtonAdd = foodJournalButtonView.findViewById(R.id.button_add);
        ImageButton foodJournalButtonViewJournal = foodJournalButtonView.findViewById(R.id.button_view_journal);

        foodJournalButtonTitle.setText(R.string.foodJournalButtonTitle);

        foodJournalButtonAdd.setOnClickListener(v -> {
            Intent add = new Intent(v.getContext(), FoodGroupOverview.class);
            startActivity(add);
        });

        foodJournalButtonViewJournal.setOnClickListener(v -> {
            Intent viewJournal = new Intent(v.getContext(), FoodJournalOverview.class);
            startActivity(viewJournal);
        });

        /* ---- CONFIGURATION ----- */
        View configButtonView = view.findViewById(R.id.config);
        /* FIXME: Click Animation for TILE Issue#38 */
        // configButtonView.setBackgroundResource(R.drawable.button_ripple_animation_orange);

        TextView configButtonTitle = configButtonView.findViewById(R.id.recommendation_button_title);
        TextView configurationButtonLeftTag = configButtonView.findViewById(R.id.buttonDescription);

        configButtonTitle.setText(R.string.configButtonTitle);
        configurationButtonLeftTag.setText(R.string.configButtonLeftTag);

        configButtonView.setOnClickListener(v -> {
            Intent configuration = new Intent(v.getContext(), PersonalInformation.class);
            startActivity(configuration);
        });

        /* ---- CUSTOM FOOD CREATION ---- */
        View createCustomFoodsView = view.findViewById(R.id.create_foods);
        /* FIXME: Click Animation for TILE Issue#38 */
        // createCustomFoodsView.setBackgroundResource(R.drawable.button_ripple_animation_purple);

        TextView createCustomFoodButtonTitle = createCustomFoodsView.findViewById(R.id.recommendation_button_title);
        TextView createCustomFoodTagLef = createCustomFoodsView.findViewById(R.id.buttonDescription);

        createCustomFoodButtonTitle.setText(R.string.createFoodsButton);
        createCustomFoodTagLef.setText(R.string.createFoodsButtonLeftText);

        createCustomFoodsView.setOnClickListener(v -> {
            Intent createCustomFood = new Intent(v.getContext(), CustomFoodOverview.class);
            startActivity(createCustomFood);
        });

        /* ---- RECOMMENDATION ---- */
        View recommendationTileView = view.findViewById(R.id.recommendations);
        TextView analysisButtonTitle = recommendationTileView.findViewById(R.id.recommendation_button_title);
        Button showAnalysisButton = recommendationTileView.findViewById(R.id.button_recommendation);

        energyBar = recommendationTileView.findViewById(R.id.progressbar_main);
        energyBarText = recommendationTileView.findViewById(R.id.progressbarTV_main);

        analysisButtonTitle.setText(R.string.analysisButtonTitle);
        showAnalysisButton.setText(R.string.showAnalysis);

        /* get logged foods of day */
        Recommendations.setProgressBar(currentDateParsed, this.db, this.energyBar, this.energyBarText, getContext());

        recommendationTileView.setOnClickListener(v -> {
            Intent analysis = new Intent(v.getContext(), Recommendations.class);
            startActivity(analysis);
        });

        showAnalysisButton.setOnClickListener(v -> {
            Intent analysis = new Intent(v.getContext(), Recommendations.class);
            startActivity(analysis);
        });

        /* PieChart */
        pieChart = view.findViewById(R.id.piChartNutrition);
        Pair<PieData, ArrayList<Integer>> pieAndListData = Recommendations.generatePieChartContent(currentDateParsed, this.db, this.colors);
        Recommendations.visualSetupPieChart(pieAndListData, pieChart);

        /* PieChartList with percent protein, carbs, fat*/
        chartList = view.findViewById(R.id.chartList);
        chartList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        Recommendations.setChartSupportingList(pieChart, pieAndListData, getContext(), chartList);
    }


}
