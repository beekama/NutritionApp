package com.example.nutritionapp.customFoods;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionElement;

import java.util.ArrayList;
import java.util.Collections;

public class CustomFoodOverview extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_foods);

        /* replace actionbar with custom app_toolbar */
        Toolbar tb = findViewById(R.id.toolbar);
        TextView tb_title = findViewById(R.id.toolbar_title);
        ImageButton tb_back = findViewById(R.id.toolbar_back);
        ImageButton tb_forward = findViewById(R.id.toolbar_forward);
        tb_forward.setImageResource(R.drawable.add_circle_filled);
        tb.setTitle("");
        tb_title.setText("ITEMS");
        setSupportActionBar(tb);
        tb_back.setOnClickListener((v -> finish()));
        tb_back.setImageResource(R.drawable.ic_arrow_back_black_24dp);

        /* add new food item */
        tb_forward.setOnClickListener((v -> {
            Intent myIntent = new Intent(v.getContext(), CreateNewFoodItem.class);
            startActivity(myIntent);
        }));

        /* display existing custom foods */
        Database db = new Database(this);
        ArrayList<Food> foods = db.getAllCustomFoods();
        ArrayList<FoodOverviewItem> foodItems = new ArrayList<>();
        ListView mainLv = findViewById(R.id.main_lv);
        for (Food f : foods) {
            foodItems.add(new FoodOverviewItem(f));
        }

        /* setup adapter */
        FoodOverviewAdapter newAdapter = new FoodOverviewAdapter(getApplicationContext(), foodItems);
        mainLv.setAdapter(newAdapter);
    }
}


