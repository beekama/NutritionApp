package com.example.nutritionapp.recommendation;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Conversions;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.NutritionElement;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;


public class Recommendations extends AppCompatActivity {

    private Database db;
    HashMap<Integer, ArrayList<Food>> foodList;
    ArrayList<Food> allFood;

    LocalDate currentDateParsed = LocalDate.now();


    public void onCreate(Bundle savedInstanceState) {
        //splash screen when needed:
        setTheme(R.style.AppTheme);

        //basic settings:
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendation);
        db = new Database(this);
        foodList = db.getLoggedFoodsByDate(currentDateParsed, currentDateParsed);


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

        //Goto Week-Chart
        tb_forward.setImageResource(R.drawable.add_circle_filled);
        tb.setTitle("");
        tb_title.setText("daily targets");
        setSupportActionBar(tb);
        //refresh:
        tb_forward.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // updateRecommendations(true);
                Intent myIntent = new Intent(v.getContext(), RecommendationsWeek.class);
                startActivity(myIntent);
            }
        }));

        /* PROGRESS BAR */
        ProgressBar energyBar = findViewById(R.id.energyBar);
        TextView energyBarText = findViewById(R.id.energyBarTextAnalysis);

        setProgressBar(currentDateParsed, db, energyBar, energyBarText);



        /* LISTVIEW */
        // NutritionAnalysis-data:
        allFood = db.getFoodsFromHashMap(foodList);

        // add nutrition items:
        ListView mainLv = findViewById(R.id.listView);
        ArrayList<RecommendationListItem> listItems = generateAdapterContent(currentDateParsed, db);

        //adapter:
        RecommendationAdapter newAdapter = new RecommendationAdapter(getApplicationContext(), listItems);
        mainLv.setAdapter(newAdapter);

        mainLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(view.getContext(), RecommendationsElement.class);
                NutritionElement nutritionElement = listItems.get(position).nutritionElement;
                myIntent.putExtra("nutritionelement", nutritionElement);
                startActivity(myIntent);
            }
        });

        //date textview:
        TextView currentDate = findViewById(R.id.textviewDate);
        //currentDate.setText(currentDateParsed.compareTo(LocalDate.now()) == 0 ? "today" : currentDateParsed.toString());
        updateDate(currentDateParsed, mainLv, currentDate);

        /* SWITCH BETWEEN DAYS */
        //dateBack:
        Button dateBack = findViewById(R.id.dateBackButton);
        dateBack.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDateParsed = currentDateParsed.minusDays(1);
                updateDate(currentDateParsed, mainLv, currentDate);
                setProgressBar(currentDateParsed,db,energyBar,energyBarText);
            }
        }));

        //dateForeward:
        Button dateForeward = findViewById(R.id.dateForewardButton);
        dateForeward.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDateParsed = currentDateParsed.plusDays(1);
                updateDate(currentDateParsed, mainLv, currentDate);
                setProgressBar(currentDateParsed,db,energyBar,energyBarText);
            }
        }));


        /* SWITCH BETWEEN WEEKS */
        //dateBack:
        Button weekBack = findViewById(R.id.dateBackButtonWeek);
        weekBack.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDateParsed = currentDateParsed.minusWeeks(1);
                updateDate(currentDateParsed, mainLv, currentDate);
                setProgressBar(currentDateParsed,db,energyBar,energyBarText);
            }
        }));

        //dateForeward:
        Button weekForeward = findViewById(R.id.dateForewardButtonWeek);
        weekForeward.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDateParsed = currentDateParsed.plusWeeks(1);
                updateDate(currentDateParsed, mainLv, currentDate);
                setProgressBar(currentDateParsed,db,energyBar,energyBarText);
            }
        }));
    }


    /* change date */
    private void updateDate(LocalDate currentDateParsed, ListView lv, TextView tv) {
        tv.setText(LocalDate.now().compareTo(currentDateParsed) == 0 ? "today" : currentDateParsed.toString());
        ArrayList<RecommendationListItem> listItems = generateAdapterContent(currentDateParsed, db);
        RecommendationAdapter newDayAdapter = new RecommendationAdapter(getApplicationContext(), listItems);
        lv.setAdapter(newDayAdapter);
    }


    ArrayList<RecommendationListItem> generateAdapterContent(LocalDate currentDateParsed, Database db) {

        /* generate Adapter-content for RecommendationAdapter */
        ArrayList<Food> foods = db.getFoodsFromHashMap(db.getLoggedFoodsByDate(currentDateParsed, currentDateParsed));
        ArrayList<RecommendationListItem> listItems = new ArrayList<>();
        if (!(foods.isEmpty())) {
            NutritionAnalysis dayNutritionAnalysis = new NutritionAnalysis(foods);
            for (NutritionElement ne : NutritionElement.values()) {
                listItems.add(new RecommendationListItem(ne, dayNutritionAnalysis.getNutritionPercentage().get(ne)));
            }
        }
        //case no foods added:
        else {
            for (NutritionElement ne : NutritionElement.values()) {
                listItems.add(new RecommendationListItem(ne, 0));
            }
        }
        return listItems;
    }

    void setProgressBar(LocalDate currentDateParsed, Database db, ProgressBar energyBar, TextView energyBarText){

        //create Arraylist with foods of the given day:
        ArrayList<Food> foods = db.getFoodsFromHashmap(db.getLoggedFoodsByDate(currentDateParsed, currentDateParsed));              //red db-access?

        int energyUsed = Conversions.jouleToKCal(Nutrition.totalEnergy(foods));
        int energyNeeded = 2000;
        int energyUsedPercentage = energyUsed*100/energyNeeded;

        if(energyUsedPercentage < 2){
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


}


