package com.example.nutritionapp.customFoods;


import android.util.Log;

import com.example.nutritionapp.other.Conversions;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionElement;


public class CreateFoodNutritionSelectorItem implements Comparable<CreateFoodNutritionSelectorItem> {
    public final String tag;
    public String unit;
    public NutritionElement ne = null;
    public int amount = 0;
    public final boolean inputTypeString;
    public String data = null;

    public CreateFoodNutritionSelectorItem(String name, boolean inputTypeString) {
        this.tag = name;
        this.unit = "N/A";
        this.inputTypeString = inputTypeString;
    }

    public CreateFoodNutritionSelectorItem(Database db, String name, int presetAmount, boolean inputTypeString) {
        this.tag = name;
        if(name.equals("Energy")){
            this.unit = "KCAL";
            this.amount = Conversions.convert("KCAL", this.unit, presetAmount);
            Log.wtf("LOL", name + " " + presetAmount + " " + this.unit + " " + Conversions.convert("JOULE", this.unit, presetAmount));
        }else{
            this.unit = "MG";
            this.amount = Conversions.convert("MG", this.unit, presetAmount);
            Log.wtf("LOL", name + " " + presetAmount + " " + this.unit + " " + Conversions.convert("UG", this.unit, presetAmount));
        }
        this.inputTypeString = inputTypeString;
    }

    public CreateFoodNutritionSelectorItem(String name, String presetData, boolean inputTypeString) {
        this.tag = name;
        this.unit = "N/A";
        this.inputTypeString = inputTypeString;
        this.data = presetData;
    }

    public CreateFoodNutritionSelectorItem(Database db, NutritionElement ne, int presetAmount, boolean inputTypeString) {
        this.ne = ne;
        this.unit = db.getNutrientNativeUnit(Integer.toString(Nutrition.databaseIdFromEnum(ne)));
        //Log.wtf("LOL", ne.toString() + " " + presetAmount + " " + this.unit + " " + Conversions.convert("UG", this.unit, presetAmount));
        this.tag = ne.toString();
        this.inputTypeString = inputTypeString;
        this.amount = presetAmount;
        //this.amount = Conversions.convert("UG", this.unit, presetAmount);
    }

    public CreateFoodNutritionSelectorItem(NutritionElement ne, String presetData, boolean inputTypeString) {
        this.ne = ne;
        this.tag = ne.toString();
        this.unit = "N/A";
        this.inputTypeString = inputTypeString;
        this.data = presetData;
    }

    public CreateFoodNutritionSelectorItem(NutritionElement ne, boolean inputTypeString) {
        this.ne = ne;
        this.tag = ne.toString();
        this.unit = "N/A";
        this.inputTypeString = inputTypeString;
    }


    @Override
    public int compareTo(CreateFoodNutritionSelectorItem other) {
        return this.tag.compareTo(other.tag);
    }
}
