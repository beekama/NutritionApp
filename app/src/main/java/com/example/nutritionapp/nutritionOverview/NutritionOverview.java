package com.example.nutritionapp.nutritionOverview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.ActivityExtraNames;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Utils;

import java.time.LocalDate;


public class NutritionOverview extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.journal);

        Intent intent = getIntent();
        String startDate = intent.getStringExtra(ActivityExtraNames.START_DATE);
        String endDate = intent.getStringExtra(ActivityExtraNames.END_DATE);

        Database db = new Database(this);

        LocalDate endDateParsed;
        LocalDate startDateParsed;

        if (startDate != null) {
            startDateParsed = LocalDate.parse(startDate, Utils.sqliteDateFormat);
            if(endDate != null) {
                endDateParsed = LocalDate.parse(endDate, Utils.sqliteDateFormat) ;
            }else{
                endDateParsed = null;
            }
            db.getLoggedFoodsByDate(startDateParsed, endDateParsed, null);
        }else {
            throw new AssertionError("No food ids and amount or dates given in intent extras!?");
        }

        Log.wtf("INFO", startDate);
    }
}
