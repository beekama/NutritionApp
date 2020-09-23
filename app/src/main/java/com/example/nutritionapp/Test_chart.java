package com.example.nutritionapp;

import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.NutritionElement;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Test_chart extends AppCompatActivity {
    PieChart pieChart;
    PieData pieData;

    LocalDate startDateParsed = LocalDate.now();
    LocalDate endDateParsed = LocalDate.now();


    HashMap<Integer, ArrayList<Food>> foodList;
    ArrayList<Food> allFood = new ArrayList<>();
    NutritionAnalysis nutritionAnalysis;

    List<PieEntry> pieEntryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_chart);

        pieChart = findViewById(R.id.pieChar);
        pieChart.setUsePercentValues(true);


/*        pieEntryList.add(new PieEntry(10, "apfel"));
        pieEntryList.add(new PieEntry(5, "Banane"));
        pieEntryList.add(new PieEntry(7, "Birne"));*/

        //get food from database:
        Database db = new Database(this);
        foodList = db.getLoggedFoodsByDate(startDateParsed, endDateParsed);

        for (ArrayList<Food> al : foodList.values()) {

                for (Food food : al) {
                    if (food != null) {
                        allFood.add(food);
                    }
                }

        }
        if (!allFood.isEmpty()) {
            nutritionAnalysis = new NutritionAnalysis(allFood);

            //exemplary use vitamin_d:
            Float missing = nutritionAnalysis.getNutritionPercentage().get(NutritionElement.VITAMIN_D);
            pieEntryList.add(new PieEntry(1 - missing, NutritionElement.VITAMIN_D.toString()));
            pieEntryList.add(new PieEntry(missing, "missing"));

        }
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "nutrition-missing");

        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);


        pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();

    }

    void addEntryToPieEntryList(int amount, String label) {
        pieEntryList.add(new PieEntry(amount, label));
    }

}
