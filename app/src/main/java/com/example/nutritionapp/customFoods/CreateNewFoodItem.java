package com.example.nutritionapp.customFoods;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Database;

public class CreateNewFoodItem extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_foods_new_item);

        //replace actionbar with custom app_toolbar:
        Toolbar tb = findViewById(R.id.toolbar);
        TextView tb_title = findViewById(R.id.toolbar_title);
        ImageButton tb_back = findViewById(R.id.toolbar_back);
        //back home button:
        tb_back.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }));
        tb_back.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        tb.setTitle("");
        tb_title.setText("MY ITEMS");
        setSupportActionBar(tb);



        //addVitamins:
        final ImageButton addVitamins = (ImageButton) findViewById(R.id.ib_createItem_furtherVitamins);
        addVitamins.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), AddVitamins.class);
                startActivity(myIntent);
            }
        }));


        //addMinerals:
        final ImageButton addMinerals = (ImageButton) findViewById(R.id.ib_createItem_furtherMinerals);
        addMinerals.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), AddMinerals.class);
                startActivity(myIntent);
            }
        }));

    }}
