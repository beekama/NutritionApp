package com.example.nutritionapp.foodJournal;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.foodJournal.overviewFoodsLists.FoodOverviewAdapter;
import com.example.nutritionapp.foodJournal.overviewFoodsLists.FoodOverviewListItem;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //splash screen: - show only when needed
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal);
        Database db = new Database(this);

        /* retrieve items */
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        ImageButton toolbarBack = findViewById(R.id.toolbar_back);
        ImageButton addStuff = findViewById(R.id.toolbar_forward);
        toolbar.setTitle("");
        toolbarTitle.setText(R.string.journalToolbarText);
        setSupportActionBar(toolbar);
        toolbarBack.setOnClickListener(v -> finishAfterTransition());
        toolbarBack.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        addStuff.setImageResource(R.drawable.add_circle_filled);

        /* set adapter */
        /* this is a list of layout of type journal_day_header, which contains the day-header and
        a nested sublist of the foods (food groups) on this */

        RecyclerView mainListOfFoodsWithDayHeaders = findViewById(R.id.mainList);
        adapter = new FoodOverviewAdapter(this, foodDataList, mainListOfFoodsWithDayHeaders, db,this, dataInvalidationMap);
        LinearLayoutManager mainListLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mainListOfFoodsWithDayHeaders.setLayoutManager(mainListLayoutManager);
        mainListOfFoodsWithDayHeaders.setAdapter(adapter);

        Intent foodGroupDetails = new Intent(addStuff.getContext(), FoodGroupOverview.class);
        addStuff.setOnClickListener(v -> startActivityForResult(foodGroupDetails, Utils.FOOD_GROUP_DETAILS_ID));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.FOOD_GROUP_DETAILS_ID) {
            if (resultCode == Activity.RESULT_OK) {

                String returnValue = data.getStringExtra("dateTimeString");
                int groupIdValue = data.getIntExtra("groupId", -1);
                assert groupIdValue != -1;

                LocalDateTime dateTime = LocalDateTime.parse(returnValue, Utils.sqliteDatetimeFormat);
                FoodOverviewListItem dirtyItem = dataInvalidationMap.get(dateTime.toLocalDate());

                /* the group exists and was just changed */
                if(dirtyItem != null) {
                    dirtyItem.dirty = true;
                    dirtyItem.update(groupIdValue);
                    adapter.notifyDataSetChanged();
                }else{
                    adapter.reloadComputationallyExpensive();
                }

            }
        }
    }
}