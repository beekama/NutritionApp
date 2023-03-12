package com.example.nutritionapp.ui;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.nutritionapp.DividerItemDecorator;
import com.example.nutritionapp.R;
import com.example.nutritionapp.other.ActivityExtraNames;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.NutritionElement;
import com.example.nutritionapp.recommendation.nutritionElement.ExtendedBarChart;
import com.example.nutritionapp.recommendation.nutritionElement.RecommendationNutritionAdapter;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.SortedMap;


public class RecommendationNutritionElementFragment extends Fragment {

    private Database db;
    private NutritionElement nutritionElement;
    private final LocalDate currentDateParsed = LocalDate.now();
    View view;

    private static final float CHART_X_AXIS_LABEL_ROTATION = -45;
    private static final float CHART_X_AXIS_GRANULARITY = -1;
    private static final float CHART_X_AXIS_MINIMUM = 0;
    private static final float CHART_Y_AXIS_MINIMUM = 0;

    public RecommendationNutritionElementFragment() {
        // Required empty public constructor
    }


    public static RecommendationNutritionElementFragment newInstance(String param1) {
        RecommendationNutritionElementFragment fragment = new RecommendationNutritionElementFragment();
        Bundle args = new Bundle();
        args.putString(ActivityExtraNames.NUTRITION_ELEMENT, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        NutritionElement argNutritionElement;
        if (args != null && (argNutritionElement = (NutritionElement) getArguments().getSerializable(ActivityExtraNames.NUTRITION_ELEMENT)) != null){
            nutritionElement = argNutritionElement;
        }
        db = new Database(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_recommendation_nutrition_element, container, false);

        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        ImageButton toolbarBack = toolbar.findViewById(R.id.toolbar_back);
        toolbar.setTitle(nutritionElement.getString(requireContext()));
        toolbarBack.setImageResource(R.color.transparent);

        /* get nutrition recommendation as max for chart */
        Nutrition rec = Nutrition.getRecommendation();
        Integer recommendedMaxValue = rec.getElements().get(nutritionElement);
        if (recommendedMaxValue == null){
            throw new AssertionError("recommended Daily Maximum from " + nutritionElement.toString() + " is null.");
        }

        /* chart general settings */
        ExtendedBarChart barChart =  view.findViewById(R.id.barChartNutrition);
        barChart.getDescription().setText("");
        barChart.getAxisLeft().setDrawGridLinesBehindData(true);
        barChart.getLegend().setEnabled(false);
        barChart.setScaleEnabled(false);

        /* compute data for chart */
        Pair<BarData, ArrayList<String >> barDataLabelPair = getChartData();

        /* compose X-axis legend */
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(CHART_X_AXIS_GRANULARITY);
        xAxis.setAxisMinimum(CHART_X_AXIS_MINIMUM);
        xAxis.setCenterAxisLabels(true);
        xAxis.setLabelRotationAngle(CHART_X_AXIS_LABEL_ROTATION);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                /* only set labels in integer steps (aka entire days) */
                int integerValue = (int) value;
                /* case float to int and search for int in chart labels */
                /* no inspection, this is the intended behaviour */
                //noinspection SuspiciousMethodCalls
                if(barDataLabelPair.second.contains(integerValue)){
                    return barDataLabelPair.second.get(integerValue);
                }
                return "";
            }
        });

        /* yaxis */
        YAxis yAxis = barChart.getAxisLeft();
        barChart.getAxisRight().setEnabled(false);
        yAxis.setAxisMaximum(recommendedMaxValue * 1.1f);
        yAxis.setAxisMinimum(CHART_Y_AXIS_MINIMUM);

        /* indication lines */
        LimitLine l1 = new LimitLine(recommendedMaxValue, "daily recommendation");
        LimitLine l2 = new LimitLine(0, "");
        l1.setLineColor(R.color.green_dark);
        l2.setLineColor(R.color.green_dark);
        barChart.getAxisLeft().addLimitLine(l1);
        barChart.getAxisLeft().addLimitLine(l2);

        /* set computed data and invalidate */
        CombinedData data = new CombinedData();
        data.setData(barDataLabelPair.first);
        barChart.setData(data);
        barChart.invalidate();

        /* food recommendation header*/
        initFoodRecommendationsHeader(recommendedMaxValue);

        /* food recommendations list */
        initFoodRecommendationsList();

        return view;
    }

    private void initFoodRecommendationsList() {

        /* layout manager */
        RecyclerView recommendationListLayout = view.findViewById(R.id.recommendationsList);
        LinearLayoutManager nutritionReportLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recommendationListLayout.setLayoutManager(nutritionReportLayoutManager);

        /* divider */
        DividerItemDecorator dividerItemDecorator = new DividerItemDecorator(ContextCompat.getDrawable(requireContext(),R.drawable.divider), true);
        recommendationListLayout.addItemDecoration(dividerItemDecorator);

        /* list adapter */
        ArrayList<Pair<Food, Float>> listItems = generateRecommendationListContent(db.getRecommendationMap(nutritionElement));
        RecyclerView.Adapter<?> recommendationNutritionAdapter = new RecommendationNutritionAdapter(getActivity(), listItems, nutritionElement, db);
        recommendationListLayout.setAdapter(recommendationNutritionAdapter);
    }

    private void initFoodRecommendationsHeader(int recommendedMaxValue) {
        String dailyReq = getResources().getString(R.string.dailyRecommendation);
        String microGram = getResources().getString(R.string.microgram);
        TextView dailyRequirements = view.findViewById(R.id.dailyRequirements);
        dailyRequirements.setText(String.format(Locale.getDefault(), "%s %d %s ", dailyReq, recommendedMaxValue, microGram));
    }

    private ArrayList<Pair<Food, Float>> generateRecommendationListContent(SortedMap<Food, Float> map) {

        ArrayList<Pair<Food, Float>> listItems = new ArrayList<>();
        for (Food food : map.keySet()){
            listItems.add(new Pair<>(food, map.get(food)));
        }
        return listItems;
    }

    private Pair<BarData, ArrayList<String>> getChartData() {

        ArrayList<String> xAxisLabels = new ArrayList<>();
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        /* start 6 days ago */
        int START_DAYS_AGO = 6;
        for (int day = START_DAYS_AGO; day >= 0; day--) {

            LocalDate currentDay = currentDateParsed.minusDays(day);

            /* set weekday */
            String dayOfWeek = currentDay.getDayOfWeek().toString();
            xAxisLabels.add(dayOfWeek);

            /* get nutrition analysis */
            HashMap<Integer, ArrayList<Food>> foodGroups = db.getLoggedFoodsByDate(currentDay, currentDay, null);
            ArrayList<Food> foods = db.getFoodsFromHashMap(foodGroups);
            NutritionAnalysis nutritionAnalysis = new NutritionAnalysis(foods);
            Integer nutritionAmount = nutritionAnalysis.getNutritionActual().getElements().get(nutritionElement);

            /* set 0 if empty */
            if(nutritionAmount == null){
                nutritionAmount = 0;
            }

            /* set entry in bar chart */
            barEntries.add(new BarEntry(START_DAYS_AGO - day, nutritionAmount));
        }

        BarDataSet set = new BarDataSet(barEntries, nutritionElement.toString());
        BarData data = new BarData(set);
        return new Pair<>(data, xAxisLabels);
    }
}