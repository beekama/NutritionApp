package com.example.nutritionapp.foodJournal;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.foodJournal.overviewFoodsLists.FoodOverviewAdapter;
import com.example.nutritionapp.foodJournal.overviewFoodsLists.FoodOverviewListItem;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.SortedMap;

public class FoodJournalOverview extends AppCompatActivity {

    private final LocalDate now = LocalDate.now();
    private final LocalDate oldestDateShown = LocalDate.now().minusWeeks(1);

    final private Duration ONE_DAY = Duration.ofDays(1);
    final private Duration ONE_WEEK = Duration.ofDays(7);
    final private ArrayList<FoodOverviewListItem> foodDataList = new ArrayList<>();

    /* this map is used to reload invalidated data */
    /* data gets invalidated by edits or adding new journal entries */
    final private HashMap<LocalDate, FoodOverviewListItem> dataInvalidationMap = new HashMap<>();

    private FoodOverviewAdapter adapter;
    private Database db;
    private RecyclerView mainListOfFoodsWithDayHeaders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //splash screen: - show only when needed
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal);
        db = new Database(this);

        /* retrieve items */
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        ImageButton toolbarBack = findViewById(R.id.toolbar_back);
        toolbar.setTitle("");
        toolbarTitle.setText(R.string.journalToolbarText);
        setSupportActionBar(toolbar);
        toolbarBack.setOnClickListener((v -> finish()));
        toolbarBack.setImageResource(R.drawable.ic_arrow_back_black_24dp);

        /* set adapter */
        /* this is a list of layout of type journal_day_header, which contains the day-header and
        a nested sublist of the foods (food groups) on this */

        mainListOfFoodsWithDayHeaders = findViewById(R.id.mainList);
        adapter = new FoodOverviewAdapter(this, foodDataList, mainListOfFoodsWithDayHeaders, db,this, dataInvalidationMap);
        LinearLayoutManager mainListLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mainListOfFoodsWithDayHeaders.setLayoutManager(mainListLayoutManager);
        mainListOfFoodsWithDayHeaders.setAdapter(adapter);

        final Button addStuff = findViewById(R.id.add_food);
        Intent foodGroupDetails = new Intent(addStuff.getContext(), FoodGroupOverview.class);
        addStuff.setOnClickListener(v -> startActivityForResult(foodGroupDetails, Utils.FOOD_GROUP_DETAILS_ID));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.FOOD_GROUP_DETAILS_ID) {
            if (resultCode == Activity.RESULT_OK) {
                String returnValue = data.getStringExtra("dateTimeString");
                Log.wtf("TEST", returnValue);
                LocalDateTime dateTime = LocalDateTime.parse(returnValue, Utils.sqliteDatetimeFormat);
                FoodOverviewListItem dirtyItem = dataInvalidationMap.get(dateTime.toLocalDate());

                /* remove the old item */
                if(dirtyItem != null) {
                    adapter.items.remove(dirtyItem);
                }

                /* get the foods from that date from the db */
                HashMap<Integer, ArrayList<Food>> loggedFoodsByDate = db.getLoggedFoodsByDate(dateTime, dateTime);

                /* since we only queried one day we basically now have the same as one field in the result of
                       Utils.foodGroupsByDays()
                */
                for(int key : loggedFoodsByDate.keySet()) {
                    for (int i = 0; i < adapter.items.size(); i++) {

                        ArrayList<Food> fList = loggedFoodsByDate.get(key);

                        assert fList != null;
                        assert fList.size() > 0;

                        if (adapter.items.get(i).date.isBefore(dateTime.toLocalDate())){
                            adapter.items.add(i, new FoodOverviewListItem(dateTime.toLocalDate(), loggedFoodsByDate, db));
                            adapter.notifyDataSetChanged();
                            return;
                        }
                    }

                    /* add it to the end if condition above didn't trigger */
                    adapter.items.add(new FoodOverviewListItem(dateTime.toLocalDate(), loggedFoodsByDate, db));
                    adapter.notifyDataSetChanged();
                    return;
                }
            }
        }
    }
}