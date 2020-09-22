package com.example.nutritionapp.other;

import android.util.Log;

import com.example.nutritionapp.foodJournal.AddFoodsLists.ListFoodItem;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import java.util.HashMap;

public class Food {

    private static final String DB_ID_ENERGY = "1008";
    private static final String DB_ID_FIBER = "1079";

    public String name;
    public String id;
    public int energy;
    public int fiber;
    public Nutrition nutrition;
    public LocalDateTime loggedAt;

    public int associatedAmount = -1;

    public Food(String name, String id){
        this.id = id;
        this.name = name;
    }

    public Food(String foodName, String foodId, Database db, LocalDateTime loggedAt) {
        this.name = foodName;
        this.id = foodId;
        HashMap<String, Integer> nutrients = null;
        if(db != null) {
            nutrients = db.getNutrientsForFood(foodId);
        }
        if(nutrients != null) {
            Integer fiber = nutrients.get(DB_ID_FIBER);
            Integer energy = nutrients.get(DB_ID_ENERGY);
            this.fiber = Utils.zeroIfNull(fiber);
            this.energy = Utils.zeroIfNull(energy);
            this.nutrition = new Nutrition(nutrients);
        }else{
            this.fiber = 0;
            this.energy = 0;
            this.nutrition = new Nutrition();
            Log.w("FOOD_DB", "Nutrition Information was Null");
        }
        this.loggedAt = loggedAt;
    }

    public boolean equals(Object o){
        try {
            Food f = (Food) o;
            return this.id.equals(f.id) && !this.id.equals("");
        }catch (ClassCastException e){
            ListFoodItem f = (ListFoodItem) o;
            return this.equals(f.food);
        }
    }

    public void setAssociatedAmount(int associatedAmount) {
        this.associatedAmount = associatedAmount;
    }

    public int getAssociatedAmount() {
        if(associatedAmount == -1){
            throw new AssertionError("Food for Database must have set associatedAmount in gram");
        }
        return associatedAmount;
    }

}

