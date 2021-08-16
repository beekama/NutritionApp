package com.example.nutritionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Utils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.min;

public class WeightTracking extends AppCompatActivity implements TransferWeight {

    private Database db;
    //observation period in days:
    private int observationPeriod = 30;
    private LocalDate currentDateParsed = LocalDate.now();
    protected LinkedHashMap<LocalDate, Integer> weightAll;
    protected LineChart chartWeight;
    protected LocalDate oldestValue;
    private TextView dateView;
    private EditText editWeight;
    private ImageButton addWeight;
    LocalDate weightAddingDate = currentDateParsed;
    protected RecyclerView weights;
    protected RecyclerView.Adapter<?> foodRec;
    private Button period;

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
        period = findViewById(R.id.popupButton);
        period.setText("asdf");
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        PopupWindow pop = new PopupWindow(inflater.inflate(R.layout.weight_tracking_dropdown, null), WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);


        period.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.showAtLocation(v, Gravity.CENTER, 0, 0);
                pop.update(50,50,300,80);
                RecyclerView recyclerView = findViewById(R.id.periodItem);

                //PERIODS - LIST
                ArrayList<Pair<String, Integer>> periods = new ArrayList<>();
                periods.add(new Pair<>( "1 Week", 7));  //todo string to string.xml
                periods.add(new Pair<>("1 Month", 31));
                periods.add(new Pair<>("6 Month", 138));
                periods.add(new Pair<>("1 Year", 365));

                WeightTrackingDropdownAdapter adapter = new WeightTrackingDropdownAdapter(getApplicationContext(), periods);
                LinearLayoutManager periodLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(periodLayoutManager);
                recyclerView.setAdapter(adapter);
             //   popupMenu();
            }
        });

/*        //setting items and values:
        String[] items = new String[]{"1 Month", "6 Month", "1 Week", "1 Year"};
        Integer[] itemTodDays = new Integer[]{31, 138, 7, 365};
        ArrayAdapter<String> pAdapter = new ArrayAdapter<>(this, R.layout.weight_tracking_period_spinner_dropdown, items);
        pAdapter.setDropDownViewResource(R.layout.weight_tracking_period_spinner_dropdown);
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
        });*/


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


        weights = findViewById(R.id.weightList);
        LinearLayoutManager nutritionReportLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        weights.setLayoutManager(nutritionReportLayoutManager);

        DividerItemDecorator dividerItemDecoratior = new DividerItemDecorator(ContextCompat.getDrawable(this.getApplicationContext(),R.drawable.divider), true);
        weights.addItemDecoration(dividerItemDecoratior);

        foodRec = new WeightTrackingWeightListAdapter(getApplicationContext(), weightAll, this);
        weights.setAdapter(foodRec);

        /* adding weight */
        dateView = findViewById(R.id.addingValueDate);
        dateView.setText(currentDateParsed.format(Utils.sqliteDateFormat));
        dateView.setOnClickListener(v -> {
            dateUpdateDialog(currentDateParsed);
        });

        editWeight = findViewById(R.id.addingValueWeight);
        //editWeight.setText();#

        ConstraintLayout layout = findViewById(R.id.weight_tracking);
        addWeight = findViewById(R.id.addingIcon);
        addWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectData(db, editWeight, weightAll);
                editWeight.setText("");
                hideKeyboard();
                layout.clearFocus();
            }
        });
    }

    void updatePageContent() {
        LineData lineData = generateChartContent();
        chartWeight.setData(lineData);
        chartWeight.invalidate();
        foodRec.notifyDataSetChanged();

    }

    private void popupMenu(){
        PopupWindow popupWindow = new PopupWindow(this);
        LinearLayout layout = new LinearLayout(this);
        LinearLayout mainLayout = new LinearLayout(this);


        //LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //PopupWindow pop = new PopupWindow(inflater.inflate(R.layout.weight_tracking_dropdown, null), WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //pop.showAtLocation(this.findViewById(R.id.popupButton), Gravity.CENTER, 0, 0);
        RecyclerView recyclerView = findViewById(R.id.periodItem);

        //PERIODS - LIST
        ArrayList<Pair<String, Integer>> periods = new ArrayList<>();
        periods.add(new Pair<>( "1 Week", 7));  //todo string to string.xml
        periods.add(new Pair<>("1 Month", 31));
        periods.add(new Pair<>("6 Month", 138));
        periods.add(new Pair<>("1 Year", 365));

        WeightTrackingDropdownAdapter adapter = new WeightTrackingDropdownAdapter(getApplicationContext(), periods);
        LinearLayoutManager periodLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(periodLayoutManager);
        recyclerView.setAdapter(adapter);
        //todo adapter
    }

    LineData generateChartContent() {
        LinkedList<Entry> values = new LinkedList<>();
        List<LocalDate> keyList = new ArrayList<>(weightAll.keySet());
        Collections.sort(keyList, Collections.reverseOrder());
        //for xAxis-labels
        oldestValue = currentDateParsed;

        for (LocalDate date : keyList) {
            long d = ChronoUnit.DAYS.between(date, currentDateParsed);
            Entry entry = new Entry(observationPeriod - d, Utils.intWeightToFloat(weightAll.get(date)));
            values.addFirst(entry);
            oldestValue = date;
            /* If there is no value for the first day of the period, then add the last value before the period to create a complete chart */
            if (d >= observationPeriod - 1) {
                break;
            }
        }

        /*start date-label-counting on start of the period or first value (if value before period necessary for chart-completeness) */
        ArrayList<String> xAxisLabels = new ArrayList<>();
        LocalDate pStart = currentDateParsed.minusDays(observationPeriod);
        LocalDate start = oldestValue.compareTo(pStart) < 0 ? oldestValue : pStart;
        for (int o = 1; o <= (int) ChronoUnit.DAYS.between(start, currentDateParsed); o++) {
            String date = start.plusDays(o).format(Utils.sqliteDateFormat);
            xAxisLabels.add(date);
        }

        XAxis xAxis = chartWeight.getXAxis();
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(observationPeriod - 1);
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

    private void dateUpdateDialog(final LocalDate localDate) {
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            LocalDate selected = LocalDate.of(year, Utils.monthAndroidToDefault(month), dayOfMonth);
            this.dateView.setText(selected.format(Utils.sqliteDateFormat));
            if (selected != localDate) {
                weightAddingDate = selected;
            }
        }, localDate.getYear(), Utils.monthDefaultToAndroid(localDate.getMonthValue()), localDate.getDayOfMonth());
        dialog.show();
    }

    private void collectData(Database db, EditText etWeight, LinkedHashMap<LocalDate, Integer> weightAll) {
        try {
            float newWeight = Float.parseFloat(etWeight.getText().toString());
            int weightInGram = Utils.floatWeightToInt(newWeight);
            weightAll.put(weightAddingDate, weightInGram);
            db.addWeightAtDate(weightInGram, weightAddingDate);
            updatePageContent();
        } catch (NumberFormatException e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Need Numeric Value as Input for Weight - consider using a '.' instead of a ','", Toast.LENGTH_LONG);
            toast.show();
        } catch (IllegalArgumentException e){
            Toast toast = Toast.makeText(getApplicationContext(), "Value for Weight must be between 40 and 600", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void hideKeyboard() {
        View focusedView = getCurrentFocus();
        if (focusedView == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        focusedView.clearFocus();
    }

}
