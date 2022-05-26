package com.example.nutritionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class WeightTracking extends AppCompatActivity implements TransferWeight,  UpdatePeriod{

    private Database db;
    //observation period in days:
    private Pair<String, Integer> observationPeriod;
    private final LocalDate currentDateParsed = LocalDate.now();
    protected TreeMap<LocalDate, Integer> weightAll;
    protected LineChart chartWeight;
    protected LocalDate oldestValue;
    private TextView dateView;
    private EditText editWeight;
    private ImageButton addWeight;
    LocalDate weightAddingDate = currentDateParsed;
    protected RecyclerView weights;
    protected RecyclerView.Adapter<?> foodRec;
    private Button period;
    protected ArrayList<Pair<String, Integer>> periods;

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
        toolbarTitle.setText(R.string.weightTracking);
        setSupportActionBar(toolbar);
        toolbarBack.setOnClickListener((v -> finishAfterTransition()));
        toolbarBack.setImageResource(R.drawable.ic_arrow_back_black_24dp);

        observationPeriod = new Pair<>(getString(R.string.oneWeek), 7);
        //PERIODS - LIST
        periods = new ArrayList<>();
        periods.add(new Pair<>(getString(R.string.oneWeek), 7));
        periods.add(new Pair<>(getString(R.string.oneMonth), 31));
        periods.add(new Pair<>(getString(R.string.sixMonth), 138));
        periods.add(new Pair<>(getString(R.string.oneYear), 365));

        /* drop-down chart for setting period: */
        period = findViewById(R.id.popupButton);
        period.setBackgroundResource(R.drawable.spinner_outline);
        period.setText(observationPeriod.first);


        period.setOnClickListener(v -> popupMenu());

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
        editWeight.setInputType(InputType.TYPE_CLASS_NUMBER);
        editWeight.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                collectData(db, editWeight, weightAll);
                editWeight.setText("");
                hideKeyboard();
                return true;
            }
            return false;
        });

        ConstraintLayout layout = findViewById(R.id.weight_tracking);
    }

    void updatePageContent() {
        LineData lineData = generateChartContent();
        chartWeight.setData(lineData);
        chartWeight.invalidate();
        foodRec.notifyDataSetChanged();
    }

    private void popupMenu(){

        /*  FIXME: what is this null parameter pass here?? */
        final View popupMenuView = LayoutInflater.from(this).inflate(R.layout.weight_tracking_dropdown, null);
        final PopupWindow popupWindow = new PopupWindow(popupMenuView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        RecyclerView recyclerView = popupMenuView.findViewById(R.id.periodItem);

        WeightTrackingDropdownRVAdapter adapter = new WeightTrackingDropdownRVAdapter(getApplicationContext(), periods, this);
        LinearLayoutManager periodLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(periodLayoutManager);
        recyclerView.setAdapter(adapter);
        //todo adapter

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(period);
       // popupWindow.showAtLocation(popupMenuView, Gravity.TOP | Gravity.RIGHT, 0, 0);
    }

    LineData generateChartContent() {
        LinkedList<Entry> values = new LinkedList<>();
        List<LocalDate> keyList = new ArrayList<>(weightAll.keySet());
        keyList.sort(Collections.reverseOrder());
        //for xAxis-labels
        oldestValue = currentDateParsed;

        for (LocalDate date : keyList) {
            long d = ChronoUnit.DAYS.between(date, currentDateParsed);
            Entry entry = new Entry(observationPeriod.second - d, Utils.intWeightToFloat(weightAll.get(date)));
            values.addFirst(entry);
            oldestValue = date;
            /* If there is no value for the first day of the period, then add the last value before the period to create a complete chart */
            if (d >= observationPeriod.second - 1) {
                break;
            }
        }

        /*start date-label-counting on start of the period or first value (if value before period necessary for chart-completeness) */
        ArrayList<String> xAxisLabels = new ArrayList<>();
        LocalDate pStart = currentDateParsed.minusDays(observationPeriod.second);
        LocalDate start = oldestValue.compareTo(pStart) < 0 ? oldestValue : pStart;
        for (int o = 1; o <= (int) ChronoUnit.DAYS.between(start, currentDateParsed); o++) {
            String date = start.plusDays(o).format(Utils.sqliteDateFormat);
            xAxisLabels.add(date);
        }

        XAxis xAxis = chartWeight.getXAxis();
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(observationPeriod.second - 1);
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
        return new LineData(dataSets);
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

    private void collectData(Database db, EditText etWeight, TreeMap<LocalDate, Integer> weightAll) {
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

    @Override
    public void setPeriod(Pair<String, Integer> period) {
        observationPeriod = period;
        updatePageContent();
        this.period.setText(observationPeriod.first);
    }
}
