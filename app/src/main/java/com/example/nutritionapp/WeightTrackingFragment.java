package com.example.nutritionapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

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


public class WeightTrackingFragment extends Fragment implements TransferWeight, UpdatePeriod {

    private Database db;
    private Pair<String, Integer> observationPeriod;
    private final LocalDate currentDateParsed = LocalDate.now();
    protected TreeMap<LocalDate, Integer> weightAll;
    protected LineChart chartWeight;
    protected LocalDate oldestValue;
    private TextView dateView;
    private EditText editWeight;
    LocalDate weightAddingDate = currentDateParsed;
    protected RecyclerView weights;
    protected RecyclerView.Adapter<?> foodRec;
    private Button period;
    protected ArrayList<Pair<String, Integer>> periods;


    public WeightTrackingFragment() {
        // Required empty public constructor
    }

    public static WeightTrackingFragment newInstance(String param1, String param2) {
        WeightTrackingFragment fragment = new WeightTrackingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new Database((MainActivity) getActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weight_tracking, container, false);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);


        observationPeriod = new Pair<>(getString(R.string.oneWeek), 7);
        /* Standard Periods */
        periods = new ArrayList<>();
        periods.add(new Pair<>(getString(R.string.oneWeek), 7));
        periods.add(new Pair<>(getString(R.string.oneMonth), 31));
        periods.add(new Pair<>(getString(R.string.sixMonth), 138));
        periods.add(new Pair<>(getString(R.string.oneYear), 365));

        /* drop-down chart for setting period: */
        period = view.findViewById(R.id.popupButton);
        period.setBackgroundResource(R.drawable.spinner_outline);
        period.setText(observationPeriod.first);
        period.setOnClickListener(v -> popupMenu(toolbar));

        /* chart */
        chartWeight = view.findViewById(R.id.chartWeight);
        db.createWeightsTableIfNotExist();
        weightAll = db.getWeightAll();

        chartWeight.setTouchEnabled(true);
        chartWeight.setScaleEnabled(false);
        chartWeight.setPinchZoom(false);
        chartWeight.getDescription().setEnabled(false);
        chartWeight.getLegend().setEnabled(false);
        LineData data = generateChartContent();
        chartWeight.setData(data);
        chartWeight.invalidate();


        weights = view.findViewById(R.id.weightList);
        LinearLayoutManager nutritionReportLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        weights.setLayoutManager(nutritionReportLayoutManager);

        DividerItemDecorator dividerItemDecorator = new DividerItemDecorator(ContextCompat.getDrawable(getContext(), R.drawable.divider), true);
        weights.addItemDecoration(dividerItemDecorator);

        foodRec = new WeightTrackingWeightListAdapter(getContext(), weightAll, this);
        weights.setAdapter(foodRec);

        /* adding weight */
        dateView = view.findViewById(R.id.addingValueDate);
        dateView.setText(currentDateParsed.format(Utils.sqliteDateFormat));
        dateView.setOnClickListener(v -> {
            dateUpdateDialog(currentDateParsed);
        });

        editWeight = view.findViewById(R.id.addingValueWeight);
        editWeight.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                collectData(db, editWeight, weightAll);
                editWeight.setText("");
                hideKeyboard();
                return true;
            }
            return false;
        });

        return view;
    }

    void updatePageContent() {
        LineData lineData = generateChartContent();
        chartWeight.setData(lineData);
        chartWeight.invalidate();
        foodRec.notifyDataSetChanged();
    }

    private void popupMenu(ViewGroup parent) {

        final View popupMenuView = LayoutInflater.from(getContext()).inflate(R.layout.weight_tracking_timeframe_dropdown, parent, false);
        final PopupWindow popupWindow = new PopupWindow(popupMenuView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        RecyclerView recyclerView = popupMenuView.findViewById(R.id.periodItem);

        WeightTrackingDropdownRVAdapter adapter = new WeightTrackingDropdownRVAdapter(getContext(), periods, this);
        LinearLayoutManager periodLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(periodLayoutManager);
        recyclerView.setAdapter(adapter);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(period);
    }

    LineData generateChartContent() {
        LinkedList<Entry> values = new LinkedList<>();
        List<LocalDate> keyList = new ArrayList<>(weightAll.keySet());
        keyList.sort(Collections.reverseOrder());

        /* for xAxis-labels */
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

        /* start date-label-counting on start of the period or first value (if value before period necessary for chart-completeness) */
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
        DatePickerDialog dialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            LocalDate selected = LocalDate.of(year, Utils.monthAndroidToDefault(month), dayOfMonth);
            this.dateView.setText(selected.format(Utils.sqliteDateFormat));
            if (selected != localDate) {
                weightAddingDate = selected;
            }
        }, localDate.getYear(), Utils.monthDefaultToAndroid(localDate.getMonthValue()), localDate.getDayOfMonth());
        dialog.show();
    }

    private void collectData(Database db, EditText editTextWeight, TreeMap<LocalDate, Integer> weightAll) {
        try {
            String fixedSeparatorWeight = editTextWeight.getText().toString().replace(",", ".");
            float newWeight = Float.parseFloat(fixedSeparatorWeight);
            int weightInGram = Utils.floatWeightToInt(newWeight);
            weightAll.put(weightAddingDate, weightInGram);
            db.addWeightAtDate(weightInGram, weightAddingDate);
            updatePageContent();
        } catch (NumberFormatException e) {
            Toast toast = Toast.makeText(getContext(), "Need Numeric Value as Input for Weight - consider using a '.' instead of a ','", Toast.LENGTH_LONG);
            toast.show();
        } catch (IllegalArgumentException e) {
            Toast toast = Toast.makeText(getContext(), "Value for Weight must be between 40 and 600", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void setPeriod(Pair<String, Integer> period) {
        observationPeriod = period;
        updatePageContent();
        this.period.setText(observationPeriod.first);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if (view == null) {
            view = new View(getActivity());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}