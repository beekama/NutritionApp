package com.example.nutritionapp.NutritionOverview;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Database;

public class NutritionOverview extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal);

        Intent intent = getIntent();
        String startDate = intent.getStringExtra("startDate");
        String endDate = intent.getStringExtra("endDate");

        Database db = new Database(this);
        db.getLoggedFoodsByDate(startDate, endDate);

        /* set adapter */
        adapter = new NutritionOverviewAdapter(this, );
        foodsFromDatabase = findViewById(R.id.listview);
        foodsFromDatabase.setAdapter(adapter);
        foodsFromDatabase.setTextFilterEnabled(true);

        final Button addStuff = findViewById(R.id.add_food);
        addStuff.setOnClickListener(v -> startActivity(new Intent(v.getContext(), AddFoodToJournal.class)));
    }
}
