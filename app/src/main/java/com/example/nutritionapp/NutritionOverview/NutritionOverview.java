package com.example.nutritionapp.NutritionOverview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritionapp.R;
import com.example.nutritionapp.foodJournal.AddFoodToJournal;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Utils;

import org.threeten.bp.LocalDate;

public class NutritionOverview extends AppCompatActivity {
    /* TODO display overview of nutrition based on food ids or dates */
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        // TODO set correct layout
        setContentView(R.layout.journal);

        Intent intent = getIntent();
        String startDate = intent.getStringExtra("startDate");
        String endDate = intent.getStringExtra("endDate");

        String foodIds = intent.getStringExtra("foodIds");
        String foodAmount = intent.getStringExtra("amounts");

        Database db = new Database(this);
        if (startDate != null) {
            LocalDate startDateParsed = LocalDate.parse(startDate, Utils.sqliteDatetimeFormat);
            LocalDate endDateParsed = null;
            if(endDate != null) {
                endDateParsed = LocalDate.parse(endDate, Utils.sqliteDatetimeFormat) ;
            }
            db.getLoggedFoodsByDate(startDateParsed, endDateParsed);

            // TODO display based on nutrion
        } else if (foodIds != null && foodAmount != null){
            // TODO display based on ids
            //for(String id : foodIds){
            //    db.getFoodById()
            //}
        }else {
            throw new AssertionError("No food ids and amount or dates given in intent extras!?");
        }


        Log.wtf("INFO", startDate);

    }
}
