package com.example.nutritionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.NutritionElement;
import com.example.nutritionapp.other.Utils;
import com.example.nutritionapp.recommendation.nutritionElement.RecommendationNutritionAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.min;

public class WeightTracking extends AppCompatActivity implements TransferWeight{

    private Database db;
    //observation period in days:
    private int observationPeriod = 30;
    private LocalDate currentDateParsed = LocalDate.now();
    protected LinkedHashMap<LocalDate, Integer> weightAll;
    protected LineChart chartWeight;
    protected LocalDate oldestValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weight_tracking);

        db = new Database(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        ImageButton toolbarBack = findViewById(R.id.toolbar_back);
        toolbar.setTitle("");
        toolbarTitle.setText("Weight Tracking");
        setSupportActionBar(toolbar);
        toolbarBack.setOnClickListener((v -> finish()));
        toolbarBack.setImageResource(R.drawable.ic_arrow_back_black_24dp);


        /* drop-down chart for setting period: */
        Spinner period = findViewById(R.id.spinnerPeriod);

        //setting items and values:
        String[] items = new String[]{"1 Month", "6 Month", "1 Week", "1 Year"};
        Integer[] itemTodDays = new Integer[]{31, 138, 7, 365};
        ArrayAdapter<String> pAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        period.setAdapter(pAdapter);

        period.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                observationPeriod = itemTodDays[position];
                updatePageContent();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                observationPeriod = itemTodDays[0];
            }
        });


        /* chart */
        chartWeight = findViewById(R.id.chartWeight);
        db.createWeightsTableIfNotExist();
        weightAll = db.getWeightAll();

/*        //testing
        //weightAll = new LinkedHashMap<>();
        weightAll.put(currentDateParsed.minusDays(60), 88);
        weightAll.put(currentDateParsed.minusDays(32), 55);
        weightAll.put(currentDateParsed.minusDays(7), 44);
        weightAll.put(currentDateParsed.minusDays(4), 55);
        weightAll.put(currentDateParsed, 66);
        XAxis xAxis = chartWeight.getXAxis();
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(29);*/


        chartWeight.setTouchEnabled(true);
        chartWeight.setScaleEnabled(false);
        chartWeight.setPinchZoom(false);
        chartWeight.getDescription().setEnabled(false);
        chartWeight.getLegend().setEnabled(false);
        LineData data = generateChartContent();
        chartWeight.setData(data);
        chartWeight.invalidate();


        RecyclerView weights = findViewById(R.id.weightList);
        LinearLayoutManager nutritionReportLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        weights.setLayoutManager(nutritionReportLayoutManager);
        RecyclerView.Adapter<?> foodRec = new WeightTrackingWeightListAdapter(getApplicationContext(), weightAll, this);
        weights.setAdapter(foodRec);
    }

    void updatePageContent(){
        weightAll = db.getWeightAll();
        LineData lineData = generateChartContent();
        chartWeight.setData(lineData);
        chartWeight.invalidate();
    }

    LineData generateChartContent(){
        LinkedList<Entry> values = new LinkedList<>();
        List<LocalDate> keyList = new ArrayList<>(weightAll.keySet());
        Collections.reverse(keyList);
        //for xAxis-labels
        oldestValue = currentDateParsed;

        for (LocalDate date : keyList){
            long d = ChronoUnit.DAYS.between(date, currentDateParsed);
            Entry entry = new Entry(observationPeriod-d, weightAll.get(date));
            values.addFirst(entry);
            oldestValue = date;
            /* If there is no value for the first day of the period, then add the last value before the period to create a complete chart */
            if (d >= observationPeriod-1){
                break;
            }
        }

        /*start date-label-counting on start of the period or first value (if value before period necessary for chart-completeness) */
        ArrayList<String> xAxisLabels = new ArrayList<>();
        LocalDate pStart = currentDateParsed.minusDays(observationPeriod);
        LocalDate start = oldestValue.compareTo(pStart) <0 ? oldestValue : pStart;
        for (int o = 1; o<= (int) ChronoUnit.DAYS.between(start, currentDateParsed); o++){
            String date = start.plusDays(o).format(Utils.sqliteDateFormat);
            xAxisLabels.add(date);
        }

        XAxis xAxis = chartWeight.getXAxis();
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(observationPeriod-1);
        xAxis.setLabelRotationAngle(-45);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return xAxisLabels.get((int) value);
            }
        });


        LineDataSet set = new LineDataSet(values, "weight");
        set.setDrawIcons(false);
        set.setColor(Color.GREEN);
        set.setCircleColor(Color.GREEN);
        set.setLineWidth(1f);
        set.setCircleRadius(3f);
        set.setDrawCircleHole(false);
        set.setDrawFilled(true);
        set.setFormLineWidth(1f);
        set.setFormSize(15f);
        set.setFillColor(Color.YELLOW);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);
        LineData data = new LineData(dataSets);
        return data;
    }


    @Override
    public void removeEntry(int weight, LocalDate date) {
        weightAll.remove(date);
        db.removeWeightAtDate(weight, date);
        updatePageContent();
    }

}
