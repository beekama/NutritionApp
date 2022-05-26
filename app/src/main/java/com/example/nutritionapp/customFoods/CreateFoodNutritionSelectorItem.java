package com.example.nutritionapp.customFoods;


import android.text.Spannable;
import android.text.SpannableString;

import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.NutritionElement;


public class CreateFoodNutritionSelectorItem implements Comparable<CreateFoodNutritionSelectorItem> {
    public final boolean header;
    public final Spannable tag;
    public NutritionElement ne = null;
    public int amount = 0;
    public final boolean inputTypeString;
    public String data = null;

    public CreateFoodNutritionSelectorItem(Spannable name, boolean header) {
        this.header = header;
        this.tag = name;
        this.inputTypeString = false;
    }

    public CreateFoodNutritionSelectorItem(Spannable name, boolean inputTypeString, boolean header) {
        this.header = header;
        this.tag = name;
        this.inputTypeString = inputTypeString;
    }

    public CreateFoodNutritionSelectorItem(Database db, Spannable name, int presetAmount, boolean inputTypeString, boolean header) {
        this.header = header;
        this.tag = name;
        this.amount = presetAmount;
        this.inputTypeString = inputTypeString;
    }

    public CreateFoodNutritionSelectorItem(Spannable name, String presetData, boolean inputTypeString, boolean header) {
        this.header = header;
        this.tag = name;
        this.inputTypeString = inputTypeString;
        this.data = presetData;
    }

    public CreateFoodNutritionSelectorItem(Database db, NutritionElement ne, int presetAmount, boolean inputTypeString, boolean header) {
        this.header = header;
        this.ne = ne;
        this.tag = new SpannableString(ne.toString());
        this.inputTypeString = inputTypeString;
        this.amount = presetAmount;
    }

    public CreateFoodNutritionSelectorItem(NutritionElement ne, String presetData, boolean inputTypeString, boolean header) {
        this.header = header;
        this.ne = ne;
        this.tag = new SpannableString(ne.toString());
        this.inputTypeString = inputTypeString;
        this.data = presetData;
    }

    public CreateFoodNutritionSelectorItem(NutritionElement ne, boolean inputTypeString, boolean header) {
        this.header = header;
        this.ne = ne;
        this.tag = new SpannableString(ne.toString());
        this.inputTypeString = inputTypeString;
    }


    @Override
    public int compareTo(CreateFoodNutritionSelectorItem other) {
        return this.tag.toString().compareTo(other.tag.toString());
    }
}
