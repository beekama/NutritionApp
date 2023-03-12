package com.example.nutritionapp.ui;

import static com.example.nutritionapp.other.Utils.navigate;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.MainActivity;
import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.deprecated.Recommendations;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StartPageFragment extends Fragment {
    private final List<Integer> colors = new ArrayList<>();

    public StartPageFragment() {
        super(R.layout.fragment_startpage);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){ //todo change back
        /* load database */
        Database db = new Database(getActivity());
        LocalDate currentDateParsed = LocalDate.now();



        /* Toolbar */
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        ImageButton toolbarRight = toolbar.findViewById(R.id.toolbar_forward);
        toolbarRight.setImageResource(android.R.color.transparent);
        toolbar.setTitle(R.string.mainActivityToolbarTitle);
        ImageButton toolbarBack = toolbar.findViewById(R.id.toolbar_back);
        toolbarBack.setImageResource(R.color.transparent);

        /* ---- JOURNAL ------*/
        View foodJournalButtonView = view.findViewById(R.id.food_journal);
        TextView foodJournalButtonTitle = foodJournalButtonView.findViewById(R.id.recommendation_button_title);
        ImageButton foodJournalButtonAdd = foodJournalButtonView.findViewById(R.id.button_add);
        ImageButton foodJournalButtonViewJournal = foodJournalButtonView.findViewById(R.id.button_view_journal);

        foodJournalButtonTitle.setText(R.string.foodJournalButtonTitle);

        foodJournalButtonAdd.setOnClickListener(v -> {
            navigate(FoodGroupFragment.class, (MainActivity) requireActivity());
        });

        foodJournalButtonViewJournal.setOnClickListener(v -> {
            navigate(JournalFragment.class, (MainActivity) requireActivity());
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
            Class<ConfigurationFragment> fragmentClass = ConfigurationFragment.class;
            navigate(fragmentClass, (MainActivity) requireActivity());
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
            Class<CustomFoodFragment> fragmentClass = CustomFoodFragment.class;
            navigate(fragmentClass, (MainActivity) requireActivity());
        });

        /* ---- RECOMMENDATION ---- */
        View recommendationTileView = view.findViewById(R.id.recommendations);
        TextView analysisButtonTitle = recommendationTileView.findViewById(R.id.recommendation_button_title);
        Button showAnalysisButton = recommendationTileView.findViewById(R.id.button_recommendation);

        ProgressBar energyBar = recommendationTileView.findViewById(R.id.progressbar_main);
        TextView energyBarText = recommendationTileView.findViewById(R.id.progressbarTV_main);

        analysisButtonTitle.setText(R.string.analysisButtonTitle);
        showAnalysisButton.setText(R.string.showAnalysis);

        /* get logged foods of day */
        Recommendations.setProgressBar(currentDateParsed, db, energyBar, energyBarText, getContext());

        recommendationTileView.setOnClickListener(v -> {
            navigate(RecommendationFragment.class, (MainActivity) requireActivity());
        });

        showAnalysisButton.setOnClickListener(v -> {
            navigate(RecommendationFragment.class, (MainActivity) requireActivity());
        });

        /* PieChart */
        PieChart pieChart = view.findViewById(R.id.piChartNutrition);
        Pair<PieData, ArrayList<Integer>> pieAndListData = Recommendations.generatePieChartContent(currentDateParsed, db, this.colors);
        Recommendations.visualSetupPieChart(pieAndListData, pieChart);

        /* PieChartList with percent protein, carbs, fat*/
        RecyclerView chartList = view.findViewById(R.id.chartList);
        chartList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        Recommendations.setChartSupportingList(pieChart, pieAndListData, getContext(), chartList);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }
}
