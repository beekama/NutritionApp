package com.example.nutritionapp.customFoods;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionapp.R;
import com.example.nutritionapp.foodJournal.AddFoodsLists.SelectedFoodAdapter;
import com.example.nutritionapp.foodJournal.AddFoodsLists.SelectedFoodItem;
import com.example.nutritionapp.other.Conversions;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class CreateNewFoodItem extends AppCompatActivity {
    private final ArrayList<CreateFoodNutritionSelectorItem> allItems = new ArrayList<>();
    private int servingSize;
    private Database db;

    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_foods_new_item);
        db = new Database(this);

        /* replace actionbar with custom app_toolbar */
        Toolbar tb = findViewById(R.id.toolbar);
        TextView tb_title = findViewById(R.id.toolbar_title);
        ImageButton tb_back = findViewById(R.id.toolbar_back);

        /* return  button */
        tb_back.setOnClickListener((v -> finish()));
        tb_back.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        tb.setTitle("");
        tb_title.setText("Create Item");
        setSupportActionBar(tb);

        /* add static inputs */
        ArrayList<CreateFoodNutritionSelectorItem> staticSelectors = new ArrayList<>();
        staticSelectors.add(new CreateFoodNutritionSelectorItem("Name", false));
        staticSelectors.add(new CreateFoodNutritionSelectorItem("Serving Size", false));
        staticSelectors.add(new CreateFoodNutritionSelectorItem("Energy", false));
        staticSelectors.add(new CreateFoodNutritionSelectorItem("fiber", false));

        /* add nutrition inputs */
        Nutrition n = new Nutrition();
        ArrayList<CreateFoodNutritionSelectorItem> nutritionSelectors = new ArrayList<>();
        ListView mainLv = findViewById(R.id.main_lv);
        for (NutritionElement ne : n.getElements().keySet()) {
            nutritionSelectors.add(new CreateFoodNutritionSelectorItem(ne, false));
        }
        Collections.sort(nutritionSelectors);

        allItems.addAll(staticSelectors);
        allItems.addAll(nutritionSelectors);

        CreateFoodNutritionSelectorAdapter newAdapter = new CreateFoodNutritionSelectorAdapter(getApplicationContext(), allItems);
        mainLv.setAdapter(newAdapter);

        Button cancel = findViewById(R.id.cancel);
        Button confirm = findViewById(R.id.confirm);
        cancel.setOnClickListener(v -> {
            finish();
        });
        confirm.setOnClickListener(v -> {
            Food f = collectData();
            if (f == null) {
                return;
            }
            db.createNewFood(f);
            finish();
        });

    }

    private Food collectData() {
        /* this function is sensitive to the correct ordering of the array list
           must be: name-> serving size -> everything else
         */
        Nutrition n = new Nutrition();
        Food f = new Food(null, null);
        f.nutrition = n;
        for (CreateFoodNutritionSelectorItem item : allItems) {
            if (item.ne != null) {
                f.nutrition.getElements().put(item.ne, item.amount / this.servingSize);
            } else {
                switch (item.tag) {
                    case "Service Size":
                        this.servingSize = item.amount;  /* next level hack */
                    case "Energy":
                        f.energy = (int) Conversions.normalize(item.unit, item.amount);
                    case "Fiber":
                        f.fiber = (int) Conversions.normalize(item.unit, item.amount);
                    case "Name":
                        if (item.data == null || item.data.equals("")) {
                            // Todo show warning
                            return null;
                        } else {
                            f.name = item.data;
                        }
                }
            }
        }
        f.nutrition = n;
        return f;
    }
}
