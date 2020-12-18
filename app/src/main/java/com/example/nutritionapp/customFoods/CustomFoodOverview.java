package com.example.nutritionapp.customFoods;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;

import java.util.ArrayList;

public class CustomFoodOverview extends AppCompatActivity {

    private Database db;
    private ListView mainLv;
    final ArrayList<FoodOverviewItem> foodItems = new ArrayList<>();
    FoodOverviewAdapter foodOverviewAdapter;

    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_foods);
        mainLv = findViewById(R.id.main_lv);

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
            Intent createCustomFood = new Intent(v.getContext(), CreateNewFoodItem.class);
            startActivity(createCustomFood);
        }));

        /* display existing custom foods */
        db = new Database(this);
        updateFoodList();
        mainLv.setOnItemClickListener((adapterView, view, i, l) -> {
            FoodOverviewItem item = (FoodOverviewItem) adapterView.getAdapter().getItem(i);
            Intent editCustomFood = new Intent(view.getContext(), CreateNewFoodItem.class);
            editCustomFood.putExtra("fdc_id", item.food.id);
            startActivity(editCustomFood);
        });
        mainLv.setOnItemLongClickListener((adapterView, view, i, l) -> {
            FoodOverviewItem item = (FoodOverviewItem) adapterView.getAdapter().getItem(i);
            db.deleteCustomFood(item.food);
            foodItems.remove(item);
            foodOverviewAdapter.notifyDataSetChanged();
            return true;
        });
    }

    private void updateFoodList(){
        ArrayList<Food> displayedFoods = db.getAllCustomFoods();
        foodItems.clear();
        for (Food f : displayedFoods) {
            foodItems.add(new FoodOverviewItem(f));
        }
        foodOverviewAdapter = new FoodOverviewAdapter(getApplicationContext(), foodItems);
        mainLv.setAdapter(foodOverviewAdapter);
        mainLv.invalidate();
    }

    @Override
    public void onResume(){
        super.onResume();
        updateFoodList();
    }
}

