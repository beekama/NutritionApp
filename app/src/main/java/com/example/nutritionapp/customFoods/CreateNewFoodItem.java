package com.example.nutritionapp.customFoods;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Conversions;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionElement;
import com.example.nutritionapp.other.Utils;

import java.util.ArrayList;
import java.util.Collections;

public class CreateNewFoodItem extends AppCompatActivity {
    private static final int CREATE_NEW_ID = -1;
    private final ArrayList<CreateFoodNutritionSelectorItem> allItems = new ArrayList<>();
    private int servingSize;
    private Database db;
    private boolean editMode;
    private Food editModeOrigFood;
    RecyclerView mainRv;


    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_foods_new_item);
        db = new Database(this);

        /* replace actionbar with custom app_toolbar */
        Toolbar tb = findViewById(R.id.toolbar);
        TextView tb_title = findViewById(R.id.toolbar_title);
        ImageButton tb_back = findViewById(R.id.toolbar_back);
        ImageButton submit = findViewById(R.id.toolbar_forward);

        /* return  button */
        tb_back.setOnClickListener((v -> finish()));
        tb_back.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        tb.setTitle("");
        tb_title.setText("Create Item");
        submit.setImageResource(R.drawable.ic_done_black_24dp);
        setSupportActionBar(tb);

        Nutrition n;
        Food editFood = null;
        String fdc_id = this.getIntent().getStringExtra("fdc_id");
        if(fdc_id != null){
            Log.wtf("YES", "EDIT MODE");
            this.editMode = true;
            editFood = db.getFoodById(fdc_id, null);
            if(editFood == null){
                throw new AssertionError("DB Return null for existing custom food id: " + fdc_id);
            }
            editModeOrigFood = editFood.deepclone();
            n = editFood.nutrition;
        }else{
            n = new Nutrition();
        }

        /* add static inputs */
        ArrayList<CreateFoodNutritionSelectorItem> staticSelectors = new ArrayList<>();
        staticSelectors.add(new CreateFoodNutritionSelectorItem("General Food Information", true));
        if(this.editMode){
            staticSelectors.add(new CreateFoodNutritionSelectorItem("Name", editFood.name, true, false));
            CreateFoodNutritionSelectorItem servingSize = new CreateFoodNutritionSelectorItem(db, "Serving Size", 100, false, false);
            servingSize.unit = "G";
            staticSelectors.add(servingSize);
            CreateFoodNutritionSelectorItem energyItemEdit = new CreateFoodNutritionSelectorItem(db, "Energy", editFood.energy, false, false);
            CreateFoodNutritionSelectorItem fiberItemEdit = new CreateFoodNutritionSelectorItem(db, "Fiber", editFood.fiber, false, false);
            energyItemEdit.unit = "KCAL";
            fiberItemEdit.unit = "MG";
            staticSelectors.add(energyItemEdit);
            staticSelectors.add(fiberItemEdit);
        }else {
            staticSelectors.add(new CreateFoodNutritionSelectorItem("Name", true, false));
            staticSelectors.add(new CreateFoodNutritionSelectorItem("Serving Size", false, false));
            CreateFoodNutritionSelectorItem energyItem = new CreateFoodNutritionSelectorItem("Energy", false, false);
            CreateFoodNutritionSelectorItem fiberItem = new CreateFoodNutritionSelectorItem("Fiber", false, false);
            energyItem.unit = "KCAL";
            fiberItem.unit = "MG";
            staticSelectors.add(energyItem);
            staticSelectors.add(fiberItem);
        }

        staticSelectors.add(new CreateFoodNutritionSelectorItem("Nutrients", true));

        ArrayList<CreateFoodNutritionSelectorItem> nutritionSelectors = new ArrayList<>();
        mainRv = findViewById(R.id.createFoodNewItem_rv);
        mainRv.addItemDecoration(new DividerItemDecoration(mainRv.getContext(), DividerItemDecoration.VERTICAL));
        for (NutritionElement ne : n.getElements().keySet()) {
            if(this.editMode){
                Integer presetAmount = n.getElements().get(ne);
                nutritionSelectors.add(new CreateFoodNutritionSelectorItem(db, ne, Utils.zeroIfNull(presetAmount), false, false));
            }else{
                nutritionSelectors.add(new CreateFoodNutritionSelectorItem(db, ne, 0, false, false));
            }
        }
        Collections.sort(nutritionSelectors);

        /* setup adapter */
        allItems.addAll(staticSelectors);
        allItems.addAll(nutritionSelectors);
        RecyclerView.Adapter<?> adapter = new CreateFoodNutritionSelectorAdapter(this, allItems);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mainRv.setLayoutManager(linearLayoutManager);
        mainRv.setAdapter(adapter);

        /* setup buttons */
        submit.setOnClickListener(v -> {
            Food f = collectData();
            if (f == null) {
                return;
            }
            if(this.editMode){
                f.id = editModeOrigFood.id;
                db.changeCustomFood(this.editModeOrigFood, f);
            }else {
                db.createNewFood(f, CREATE_NEW_ID);
            }
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
                if(item.amount == -1){
                    continue;
                }
                int servingSizeTmp = 1;
                if(this.servingSize != 0){
                    servingSizeTmp = servingSize;
                }
                f.nutrition.getElements().put(item.ne, item.amount / servingSizeTmp);
            } else {
                if(item.amount == -1){
                    item.amount = 0;
                }
                switch (item.tag) {
                    case "Service Size":
                        this.servingSize = item.amount;  /* next level hack */
                        break;
                    case "Energy":
                        f.energy = Conversions.convert(item.unit, "KCAL", item.amount);
                        break;
                    case "Fiber":
                        f.fiber = item.amount;
                        break;
                    case "Name":
                        if (item.data == null || item.data.equals("")) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Name must be set.", Toast.LENGTH_LONG);
                            toast.show();
                            return null;
                        }else if (editModeOrigFood == null && db.checkCustomNameFoodExists(item.data)) {
                            Toast toast = Toast.makeText(getApplicationContext(), "A food with this name already exists.", Toast.LENGTH_LONG);
                            toast.show();
                            return null;
                        } else {
                            f.name = item.data;
                        }
                        break;
                }
            }
        }
        f.nutrition = n;
        return f;
    }
}
