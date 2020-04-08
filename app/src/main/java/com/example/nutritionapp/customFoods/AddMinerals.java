package com.example.nutritionapp.customFoods;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritionapp.R;

public class AddMinerals extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_foods_new_item_add_minerals);

        //goBack to create_Item:
        final ImageButton goBack = (ImageButton) findViewById(R.id.addMinerals_back_button);
        goBack.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }));

        //break:
        //todo: warning
    }
}