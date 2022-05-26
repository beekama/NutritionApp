package com.example.nutritionapp.recommendation.nutritionElement;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.DividerItemDecorator;
import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.NutritionElement;
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

public class RecommendationsElement extends AppCompatActivity {
    private Database db;
    private NutritionElement nutritionElement;
    private ArrayList<Food> allFood;
    private final LocalDate currentDateParsed = LocalDate.now();
    private int recommendation;
    private RecyclerView recList;


    public void onCreate(Bundle savedInstanceState) {
        /* set NutritionElement */
        Bundle b = getIntent().getExtras();
        if (b != null) {
            nutritionElement = (NutritionElement) b.get("nutritionelement");
        }

        //splash screen when needed:
        setTheme(R.style.AppTheme);

        //basic settings:
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendation_nutrition);
        db = new Database(this);
        Context context = getApplicationContext();


        /* APP TOOLBAR */
        //replace actionbar with custom app_toolbar:
        Toolbar tb = findViewById(R.id.toolbar);
        TextView tb_title = findViewById(R.id.toolbar_title);
        ImageButton tb_back = findViewById(R.id.toolbar_back);
        ImageButton tb_forward = findViewById(R.id.toolbar_forward);

        //visible title:
        tb_back.setImageResource(R.drawable.ic_arrow_back_black_24dp);


        //back home button:
        tb_back.setOnClickListener((v -> finishAfterTransition()));

        //set title
        tb.setTitle("");
        tb_title.setText(nutritionElement.getString(context));
        setSupportActionBar(tb);

        //recommendation:
        Nutrition rec = Nutrition.getRecommendation();
        recommendation = rec.getElements().get(nutritionElement);

        /* CHART */
        ExtendedBarChart barChart =  findViewById(R.id.barChartNutrition);
        barChart.getDescription().setText("");
        Pair<BarData, ArrayList<String >> chartData = getChartData();
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(0);
        xAxis.setCenterAxisLabels(true);
        xAxis.setLabelRotationAngle(-45);
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

        YAxis yAxis = barChart.getAxisLeft();
        barChart.getAxisRight().setEnabled(false);
        yAxis.setAxisMaximum(recommendation*1.1f);
        yAxis.setAxisMinimum(0);


        LimitLine l1 = new LimitLine(recommendation, "daily recommendation");
        l1.setLineColor(R.color.green_dark);
        LimitLine l2 = new LimitLine(0, "");
        l2.setLineColor(R.color.green_dark);
        barChart.getAxisLeft().addLimitLine(l1);
        barChart.getAxisLeft().addLimitLine(l2);
        barChart.getAxisLeft().setDrawGridLinesBehindData(true);
        barChart.getLegend().setEnabled(false);
        barChart.setScaleEnabled(false);

        CombinedData data = new CombinedData();
        data.setData(chartData.first);
        barChart.setData(data);
        barChart.invalidate();

        /* food-recommendation */
        String dailyReq = getResources().getString(R.string.dailyRecommendation);
        String microGram = getResources().getString(R.string.microgram);
        TextView dailyR = findViewById(R.id.dailyReq);
        dailyR.setText(String.format(Locale.getDefault(), "%s %d %s ", dailyReq, recommendation, microGram));

        /* FIXME: can this be removed?
        TextView recDesc = findViewById(R.id.recommendation_description);
        String header = getResources().getString(R.string.recommendationDesc);
        recDesc.setText(String.format("%s %s:\n%s %d %s.", header, nutritionElement.getString(context),dailyReq, recommendation, microGram));
        recDesc.setText(String.format("%s-rich food: ", nutritionElement.getString(context)));
        */

        recList = findViewById(R.id.RecListView);
        LinearLayoutManager nutritionReportLayoutManager = new LinearLayoutManager(RecommendationsElement.this, LinearLayoutManager.VERTICAL, false);
        recList.setLayoutManager(nutritionReportLayoutManager);

        DividerItemDecorator dividerItemDecoratior = new DividerItemDecorator(ContextCompat.getDrawable(this.getApplicationContext(),R.drawable.divider), true);
        recList.addItemDecoration(dividerItemDecoratior);

        ArrayList<Pair<Food, Float>> listItems = generateAdapterContent(db.getRecommendationMap(nutritionElement));
        RecyclerView.Adapter<?> foodRec = new RecommendationNutritionAdapter(RecommendationsElement.this, listItems, nutritionElement, db);  //!! Activityname.this instead of getapplicationcontext() necessarry for recognizing day/nightmode changes
        recList.setAdapter(foodRec);
    }

    ArrayList<Pair<Food, Float>> generateAdapterContent(SortedMap<Food, Float> map) {

        ArrayList<Pair<Food, Float>> listItems = new ArrayList<>();
        for (Food food : map.keySet()){
            listItems.add(new Pair<>(food, map.get(food)));
        }
        return listItems;
    }

    Pair<BarData, ArrayList<String>> getChartData() {
        ArrayList<String> xAxisLabels = new ArrayList<>();
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int day = 6; day >= 0; day--) {
            xAxisLabels.add(currentDateParsed.minusDays(day).getDayOfWeek().toString());
            Integer amount = 0;
            HashMap<Integer, ArrayList<Food>> foodgroups = db.getLoggedFoodsByDate(currentDateParsed.minusDays(day), currentDateParsed.minusDays(day), null);
            ArrayList<Food> foods = (foodgroups.isEmpty()) ? null : db.getFoodsFromHashMap(foodgroups);
            if (foods != null) {
                NutritionAnalysis nutritionAnalysis = new NutritionAnalysis(foods);
                Nutrition nutrition = nutritionAnalysis.getNutritionActual();
                amount = nutrition.getElements().get(nutritionElement);
            }
            barEntries.add(new BarEntry(6-day, amount));
        }

        BarDataSet set = new BarDataSet(barEntries, nutritionElement.toString());
        BarData data = new BarData(set);
        return new Pair<>(data, xAxisLabels);
    }


}
