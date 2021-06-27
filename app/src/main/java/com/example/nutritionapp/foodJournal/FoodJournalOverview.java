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
        adapter = new FoodOverviewAdapter(this, foodDataList, mainListOfFoodsWithDayHeaders, db);
        LinearLayoutManager mainListLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mainListOfFoodsWithDayHeaders.setLayoutManager(mainListLayoutManager);
        mainListOfFoodsWithDayHeaders.setAdapter(adapter);

        final Button addStuff = findViewById(R.id.add_food);
        addStuff.setOnClickListener(v -> startActivity(new Intent(v.getContext(), FoodGroupOverview.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        /* TODO implement new invalidation scheme for new entries */
    }
}