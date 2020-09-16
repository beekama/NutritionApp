package com.example.nutritionapp.NutritionOverview;

import android.content.Intent;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritionapp.R;
import com.example.nutritionapp.foodJournal.AddFoodToJournal;
import com.example.nutritionapp.other.Database;

import org.threeten.bp.LocalDate;

public class NutritionOverview extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal);

        Intent intent = getIntent();
        String startDate = intent.getStringExtra("startDate");
        String endDate = intent.getStringExtra("endDate");

        LocalDate startDateParsed = LocalDate.now();
        LocalDate endDateParsed = LocalDate.now();

        Database db = new Database(this);

        db.getLoggedFoodsByDate(startDateParsed, endDateParsed);

        /* set adapter */
        BaseAdapter adapter = new NutritionOverviewAdapter();
        ListView foodsFromDatabase = findViewById(R.id.listview);
        foodsFromDatabase.setAdapter(adapter);
        foodsFromDatabase.setTextFilterEnabled(true);

        final Button addStuff = findViewById(R.id.add_food);
        addStuff.setOnClickListener(v -> startActivity(new Intent(v.getContext(), AddFoodToJournal.class)));
    }
}
