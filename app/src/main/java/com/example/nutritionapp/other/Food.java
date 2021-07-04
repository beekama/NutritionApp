package com.example.nutritionapp.other;

import android.util.JsonReader;
import android.util.Log;
import android.util.Pair;

import com.example.nutritionapp.foodJournal.addFoodsLists.ListFoodItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.HashMap;

public class Food implements Comparable{

    public static final String DB_ID_ENERGY = "1008";
    public static final String DB_ID_FIBER = "1079";
    public static final String DB_ID_FAT = "1004";
    public static final String DB_ID_CARB = "1005";
    public static final String DB_ID_PROTEIN = "1003";

    public String name;
    public String id;
    public int energy;
    public int fiber;
    public int carb;
    public int protein;
    public int fat;
    public Nutrition nutrition;
    public LocalDateTime loggedAt;

    public float associatedAmount = -1;
    public PortionTypes associatedPortionType = null;
    public float associatedPortionTypeAmount = -1;

    public Food(String name, String id){
        this.id = id;
        this.name = name;
    }

    public Food(String foodName, String foodId, Database db, LocalDateTime loggedAt) {
        this.name = foodName;
        this.id = foodId;
        if(db != null) {
            setNutritionFromDb(db);
            setPreferedPortionFromDb(db);
            setAmountByAssociatedPortionType();
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

    public void setAssociatedAmount(float associatedAmount) {
        this.associatedAmount = associatedAmount;
    }

    public float getAssociatedAmount() {
        if(associatedAmount == -1){
            throw new AssertionError("Food for Database must have set associatedAmount in gram");
        }
        return associatedAmount;
    }

    public void setAssociatedPortionType(PortionTypes associatedPortionType) {
        this.associatedPortionType = associatedPortionType;
    }

    public PortionTypes getAssociatedPortionType() {
        if(associatedPortionType == null){
            throw new AssertionError("Food for Database must have set legal associatedPortionType");
        }
        return associatedPortionType;
    }

    public void setAssociatedPortionTypeAmount(Float associatedPortionTypeAmount) {
        this.associatedPortionTypeAmount = associatedPortionTypeAmount;
    }

    public Float getAssociatedPortionTypeAmount() {
        if(this.associatedPortionTypeAmount == -1){
            throw new AssertionError("Food for Database must have set associatedPortionTypeAmount in gram");
        }
        return associatedPortionTypeAmount;
    }

    public Food deepclone() {
        LocalDateTime copy = null;
        if(this.loggedAt != null){
            copy = LocalDateTime.from(this.loggedAt);
        }
        Food clone = new Food(this.name, this.id, null, copy);
        if(this.associatedAmount != -1) {
            clone.setAssociatedAmount(this.associatedAmount);
            clone.setAssociatedPortionType(this.associatedPortionType);
        }
        clone.energy = this.energy;
        clone.fiber = this.fiber;
        clone.carb = this.carb;
        clone.protein = this.protein;
        clone.fat = this.fat;
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
            Integer fat = nutrients.get(DB_ID_FAT);
            Integer protein = nutrients.get(DB_ID_PROTEIN);
            Integer carb = nutrients.get(DB_ID_CARB);
            Integer energy = nutrients.get(DB_ID_ENERGY);
            this.fiber = Utils.zeroIfNull(fiber);
            this.fat = Utils.zeroIfNull(fat);
            this.protein = Utils.zeroIfNull(protein);
            this.carb = Utils.zeroIfNull(carb);
            this.energy = Utils.zeroIfNull(energy);
            this.nutrition = new Nutrition(nutrients);
        }else{
            this.fiber = 0;
            this.fat = 0;
            this.protein = 0;
            this.carb = 0;
            this.energy = 0;
            this.nutrition = new Nutrition();
            Log.w("FOOD_DB", "Nutrition Information was Null");
        }
    }

    public void setPreferedPortionFromDb(Database db){
        this.associatedPortionType = db.getPreferedPortionType(this);
        this.associatedPortionTypeAmount = db.getPortionAmountForPortionType(this,this.associatedPortionType);
    }

    public void setAmountByAssociatedPortionType(){     //todo rethink amounts in gereral
        if (this.associatedPortionType.equals(PortionTypes.FLUID_OUNZE) || this.associatedPortionType.equals(PortionTypes.ML) || this.associatedPortionType.equals(PortionTypes.GRAM)){
            this.associatedAmount = 100f;
        } else{
            this.associatedAmount = 1f;
        }
    }

    @Override
    public int compareTo(Object o) {
        return ((Food) o).name.compareTo(this.name);
    }
}

