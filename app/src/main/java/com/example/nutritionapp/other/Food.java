package com.example.nutritionapp.other;

import android.util.JsonReader;
import android.util.Log;

import com.example.nutritionapp.foodJournal.addFoodsLists.ListFoodItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.HashMap;

public class Food {

    public static final String DB_ID_ENERGY = "1008";
    public static final String DB_ID_FIBER = "1079";

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
        if(db != null) {
            setNutritionFromDb(db);
        }
        this.loggedAt = null;
        if(loggedAt != null){
            this.loggedAt = LocalDateTime.from(loggedAt);
        }
    }

    public boolean equals(Object o){
        try {
            Food f = (Food) o;
            return this.id.equals(f.id) && !this.id.equals("");
        }catch (ClassCastException e){
            Log.wtf("WTF", "Do we even end up here?"); //TODO rethink this
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

    public Food deepclone() {
        LocalDateTime copy = null;
        if(this.loggedAt != null){
            copy = LocalDateTime.from(this.loggedAt);
        }
        Food clone = new Food(this.name, this.id, null, copy);
        if(this.associatedAmount != -1) {
            clone.setAssociatedAmount(this.associatedAmount);
        }
        clone.energy = this.energy;
        clone.fiber = this.fiber;
        clone.nutrition = new Nutrition(this.nutrition);
        return clone;
    }

    public boolean isIdValid() {
        return this.id != null && !this.id.equals("") && !this.id.equals("-1");
    }

    public JSONObject toJsonObject () throws JSONException {
        JSONObject ret = new JSONObject();
        ret.put("id", id);
        ret.put("name", name);
        ret.put("amount", associatedAmount);
        return ret;
    }

    public void setNutritionFromDb(Database db) {
        HashMap<String, Integer> nutrients ;
        nutrients = db.getNutrientsForFood(this.id);
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
    }
}

