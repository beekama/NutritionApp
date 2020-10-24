package com.example.nutritionapp.recommendation;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.NutritionElement;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;

public class RecommendationsElement extends AppCompatActivity {
    private Database db;
    private NutritionElement nutritionElement;
    HashMap<Integer, ArrayList<Food>> foodList;
    ArrayList<Food> allFood;


    public void onCreate(Bundle savedInstanceState) {
        /* set NutritionElement */
        Bundle b = getIntent().getExtras();
        if (b!=null){
            nutritionElement =(NutritionElement) b.get("nutritionelement");
        }
        Toast t = Toast.makeText(getApplicationContext(),nutritionElement.toString(),Toast.LENGTH_LONG);
        t.show();

        //splash screen when needed:
        setTheme(R.style.AppTheme);

        //basic settings:
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendation_element);
        db = new Database(this);


        /* APP TOOLBAR */
        //replace actionbar with custom app_toolbar:
        Toolbar tb = findViewById(R.id.toolbar);
        TextView tb_title = findViewById(R.id.toolbar_title);
        ImageButton tb_back = findViewById(R.id.toolbar_back);
        ImageButton tb_forward = findViewById(R.id.toolbar_forward);

        //visible title:
        tb_back.setImageResource(R.drawable.ic_arrow_back_black_24dp);

        //back home button:
        tb_back.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }));

        //set title
        tb.setTitle("");
        tb_title.setText(nutritionElement.toString());
        setSupportActionBar(tb);

        /* LINECHART - NUTRITIONELEMENT */
        LineChart chartElement = findViewById(R.id.lineChartNutritionElement);
        //styling
        chartElement.setPinchZoom(false);
        chartElement.getDescription().setText("");
        chartElement.setDrawGridBackground(false);




/*        chartElement.setTouchEnabled(false);
        chartElement.getLegend().setEnabled(false);*/

        XAxis xAxis = chartElement.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        YAxis yAxis = chartElement.getAxisLeft();
        YAxis rAxis = chartElement.getAxisRight();
        rAxis.setDrawLabels(false);
        rAxis.setDrawGridLines(false);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setSpaceTop(15f);
        yAxis.setGranularity(1f);
        yAxis.setAxisMinimum(0);

        xAxis.setDrawLabels(false);

        //Marker
        MarkerView mv = new CustomMarkerView(this, R.layout.marker_view);
        mv.setChartView(chartElement);
        chartElement.setMarker(mv);

        //data
        setData(chartElement);
    }


    void setData(LineChart lineChart) {
        LocalDate currentDateParsed = LocalDate.now();
        ArrayList<Entry> entries = new ArrayList<>();
        /* daily NutritionAnalysis - ONE WEEK */
        for  (int i = 6; i >= 0; i--) {
            //get foods:
            ArrayList<Food> foods = db.getFoodsFromHashmap(db.getLoggedFoodsByDate(currentDateParsed, currentDateParsed));
            currentDateParsed = currentDateParsed.minusDays(1);
            //get analysed data:
            NutritionAnalysis dayNutritionAnalysis = new NutritionAnalysis(foods);
            entries.add(0,new Entry(i,dayNutritionAnalysis.getNutritionPercentage().get(nutritionElement)));
        }
        LineDataSet lineDataSet = new LineDataSet(entries, nutritionElement.toString());
        //style
        lineDataSet.setLineWidth(3f);
        lineDataSet.setCircleRadius(5f);
        LineData data = new LineData(lineDataSet);
        lineChart.setData(data);
        lineChart.invalidate();
    }

}
