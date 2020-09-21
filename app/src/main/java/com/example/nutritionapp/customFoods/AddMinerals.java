package com.example.nutritionapp.customFoods;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Nutrition;

public class AddMinerals extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_foods_new_item_add_minerals);


        /* return */
        final ImageButton goBack = (ImageButton) findViewById(R.id.addMinerals_back_button);
        goBack.setOnClickListener((v -> {
            Intent result = new Intent();
            result.putExtra("some_key", "String data");
            setResult(Activity.RESULT_OK, result);
        }));

    }
}