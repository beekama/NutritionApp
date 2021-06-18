package com.example.nutritionapp.recommendation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.NutritionElement;
import com.example.nutritionapp.other.NutritionPercentageTuple;
import com.example.nutritionapp.other.Utils;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;


public class Recommendations extends AppCompatActivity {



    private Database db;
    private ProgressBar energyBar;
    private TextView energyBarText;
    private RecyclerView nutritionRList;
    private TextView dateView;

    private LocalDate currentDateParsed = LocalDate.now();


    public void onCreate(Bundle savedInstanceState) {
        //splash screen when needed:
        setTheme(R.style.AppTheme);

        //basic settings:
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendation);
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



        /* PROGRESS BAR */
        energyBar = findViewById(R.id.energyBar);
        energyBarText = findViewById(R.id.energyBarTextAnalysis);
        setProgressBar(currentDateParsed);


        /* NUTRITION LIST */
        // add nutrition items:
        nutritionRList = findViewById(R.id.listView);
        LinearLayoutManager nutritionReportLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        nutritionRList.setLayoutManager(nutritionReportLayoutManager);
        ArrayList<RecommendationListItem> listItems = generateAdapterContent(currentDateParsed);

        RecyclerView.Adapter<?> nutritionReport = new RecommendationAdapter(getApplicationContext(), listItems);
        nutritionRList.setAdapter(nutritionReport);

        dateView = findViewById(R.id.date);
        dateView.setText(currentDateParsed.format(Utils.sqliteDateFormat));
        dateView.setOnClickListener(v -> {dateUpdateDialog(currentDateParsed);});
    }


    private void updateNutritionList(LocalDate localDate) {
        ArrayList<RecommendationListItem> listItems = generateAdapterContent(localDate);
        RecommendationAdapter newDayAdapter = new RecommendationAdapter(getApplicationContext(), listItems);
        nutritionRList.setAdapter(newDayAdapter);
    }


    ArrayList<RecommendationListItem> generateAdapterContent(LocalDate currentDateParsed) {

        /* generate Adapter-content for RecommendationAdapter */
        ArrayList<Food> foods = db.getFoodsFromHashMap(db.getLoggedFoodsByDate(currentDateParsed, currentDateParsed));
        ArrayList<RecommendationListItem> listItems = new ArrayList<>();

        /* track source of display elements */
        HashMap<NutritionElement, Boolean> nonZero = new HashMap<>();
        for(NutritionElement ne : NutritionElement.values()){
            nonZero.put(ne, false);
        }

        /* display sorted non-zero percentages */
        NutritionAnalysis dayNutritionAnalysis = new NutritionAnalysis(foods);
        Nutrition target = Nutrition.getRecommendation();
        ArrayList<NutritionPercentageTuple> nutritionPercentages = dayNutritionAnalysis.getNutritionPercentageSortedFilterZero();
        for (NutritionPercentageTuple net : nutritionPercentages) {
            Integer nutTarget = target.getElements().get(net.nutritionElement);
            listItems.add(new RecommendationListItem(net.nutritionElement, net.percentage, nutTarget));
            nonZero.put(net.nutritionElement, true);
        }

        /* display zero value if desired */
        for(NutritionElement ne : nonZero.keySet()){
            if(!nonZero.get(ne)){
                Integer nutTarget = target.getElements().get(ne);
                listItems.add(new RecommendationListItem(ne, 0f, nutTarget));
            }
        }

        return listItems;
    }


    void setProgressBar(LocalDate currentDateParsed){

        //create Arraylist with foods of the given day:
        ArrayList<Food> foods = db.getFoodsFromHashMap(db.getLoggedFoodsByDate(currentDateParsed, currentDateParsed));

        int energyUsed = Nutrition.totalEnergy(foods);
        int energyNeeded = 2000;
        int energyUsedPercentage = energyUsed*100/energyNeeded;

        if(energyUsedPercentage < 75){
            energyBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        }else if(energyUsedPercentage < 125){
            energyBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
        }else{
            energyBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        }

        energyBar.setProgress(Math.min(energyUsedPercentage, 100));
        String energyBarContent = String.format("Energy %d/%d", energyUsed, energyNeeded);
        energyBarText.setText(energyBarContent);
    }


    private void dateUpdateDialog(final LocalDate localDate) {
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            LocalDate selected = LocalDate.of(year, month, dayOfMonth);
            this.dateView.setText(selected.format(Utils.sqliteDateFormat));
            if (selected != localDate){
                updateNutritionList(selected);
                setProgressBar(selected);
            }
        }, localDate.getYear(),localDate.getMonthValue(), localDate.getDayOfMonth());
        dialog.show();
    }



}


