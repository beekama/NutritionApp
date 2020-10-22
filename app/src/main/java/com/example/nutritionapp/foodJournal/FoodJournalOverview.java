package com.example.nutritionapp.foodJournal;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionapp.NutritionOverview.NutritionOverview;
import com.example.nutritionapp.foodJournal.OverviewFoodsLists.FoodOverviewAdapter;
import com.example.nutritionapp.foodJournal.OverviewFoodsLists.FoodOverviewListItem;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.SortedMap;

public class FoodJournalOverview extends AppCompatActivity {

    private LocalDate now = LocalDate.now();
    private LocalDate oldestDateShown = LocalDate.now().minusWeeks(1);

    final private Duration ONE_DAY = Duration.ofDays(1);
    final private Duration ONE_WEEK = Duration.ofDays(7);
    final private ArrayList<FoodOverviewListItem> foodDataList = new ArrayList<>();

    private FoodOverviewAdapter adapter;
    private Database db;
    private ListView mainListOfFoodsWithDayHeaders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //splash screen: - show only when needed
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal);
        db = new Database(this);

        /* retrieve items */
        updateFoodJournalList(false);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        ImageButton toolbarBack = findViewById(R.id.toolbar_back);
        toolbar.setTitle("");
        toolbarTitle.setText("JOURNAL");
        setSupportActionBar(toolbar);
        toolbarBack.setOnClickListener((v -> finish()));
        toolbarBack.setImageResource(R.drawable.ic_arrow_back_black_24dp);

        /* set adapter */
        /* this is a list of layout of type journal_dayheader, which contains the dayheader and
        a nested sublist of the foods (foodgroups) on this */
        adapter = new FoodOverviewAdapter(this, foodDataList);
        mainListOfFoodsWithDayHeaders = findViewById(R.id.listview);
        mainListOfFoodsWithDayHeaders.setAdapter(adapter);
        mainListOfFoodsWithDayHeaders.setTextFilterEnabled(true);

        final Button addStuff = findViewById(R.id.add_food);
        addStuff.setOnClickListener(v -> startActivity(new Intent(v.getContext(), AddFoodToJournal.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFoodJournalList(true);
    }

    private void updateFoodJournalList(boolean runInvalidation) {
        HashMap<Integer, ArrayList<Food>> foodGroups = db.getLoggedFoodsByDate(now, oldestDateShown);
        SortedMap<LocalDate, HashMap<Integer, ArrayList<Food>>> foodGroupsByDay = Utils.foodGroupsByDays(foodGroups);
        foodDataList.clear();

        /* generate reversed list */
        ArrayList<LocalDate> keyListReversed = new ArrayList<>();
        keyListReversed.addAll(foodGroupsByDay.keySet());
        Collections.reverse(keyListReversed);

        for(LocalDate day : keyListReversed){
            HashMap<Integer, ArrayList<Food>> localFoodGroups = foodGroupsByDay.get(day);
            String dateString = day.format(DateTimeFormatter.ISO_DATE);
            ArrayList<Food> foodListForGroupOnDay = new ArrayList<>();
            for(Integer groupId : localFoodGroups.keySet()){
                foodListForGroupOnDay.addAll(localFoodGroups.get(groupId));
            }
            FoodOverviewListItem nextItem = new FoodOverviewListItem(dateString, foodListForGroupOnDay, localFoodGroups);
            foodDataList.add(nextItem);
        }
        if(runInvalidation) {
            adapter.notifyDataSetInvalidated();
            mainListOfFoodsWithDayHeaders.invalidate();
            mainListOfFoodsWithDayHeaders.setAdapter(adapter);
        }
    }


}

