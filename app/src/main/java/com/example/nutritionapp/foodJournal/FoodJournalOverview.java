package com.example.nutritionapp.foodJournal;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritionapp.NutritionOverview.NutritionOverview;
import com.example.nutritionapp.foodJournal.OverviewFoodsLists.FoodOverviewAdapter;
import com.example.nutritionapp.foodJournal.OverviewFoodsLists.FoodOverviewListItem;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Utils;

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
    private ListView foodsFromDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //splash screen: - show only when needed
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal);
        db = new Database(this);

        /* retrieve items */
        updateFoodJournalList(false);

        /* set adapter */
        adapter = new FoodOverviewAdapter(this, foodDataList);
        foodsFromDatabase = findViewById(R.id.listview);
        foodsFromDatabase.setAdapter(adapter);
        foodsFromDatabase.setTextFilterEnabled(true);

        final Button addStuff = findViewById(R.id.add_food);
        addStuff.setOnClickListener(v -> startActivity(new Intent(v.getContext(), AddFoodToJournal.class)));

        foodsFromDatabase.setOnItemClickListener((parent, view, position, id) -> {
            FoodOverviewListItem tmpItem = (FoodOverviewListItem) parent.getItemAtPosition(position);
            Intent target = new Intent(view.getContext(), NutritionOverview.class);
            target.putExtra("startDate", tmpItem.date);
            target.putExtra("endDate", tmpItem.date);
            startActivity(target);
        });
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
            foodsFromDatabase.invalidate();
        }
    }


}

