package com.example.nutritionapp.customFoods;


import android.text.Spannable;
import android.text.SpannableString;

import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.NutritionElement;


public class CreateFoodNutritionSelectorItem implements Comparable<CreateFoodNutritionSelectorItem> {
    public final boolean header;
    public final Spannable tag;
    public int stringID;
    public NutritionElement ne = null;
    public int amount = 0;
    public final boolean inputTypeString;
    public String data = null;

    public CreateFoodNutritionSelectorItem(int stringID, Spannable name, boolean header) {
        this.header = header;
        this.tag = name;
        this.stringID = stringID;
        this.inputTypeString = false;
    }

    public CreateFoodNutritionSelectorItem(int stringID, Spannable name, boolean inputTypeString, boolean header) {
        this.header = header;
        this.stringID = stringID;
        this.tag = name;
        this.inputTypeString = inputTypeString;
    }

    public CreateFoodNutritionSelectorItem(Database db, int stringID, Spannable name, int presetAmount, boolean inputTypeString, boolean header) {
        this.header = header;
        this.stringID = stringID;
        this.tag = name;
        this.amount = presetAmount;
        this.inputTypeString = inputTypeString;
    }

    public CreateFoodNutritionSelectorItem(int stringID, Spannable name, String presetData, boolean inputTypeString, boolean header) {
        this.header = header;
        this.stringID = stringID;
        this.tag = name;
        this.inputTypeString = inputTypeString;
        this.data = presetData;
    }

    public CreateFoodNutritionSelectorItem(Database db, int stringID, NutritionElement ne, int presetAmount, boolean inputTypeString, boolean header) {
        this.header = header;
        this.stringID = stringID;
        this.ne = ne;
        this.tag = new SpannableString(ne.toString());
        this.inputTypeString = inputTypeString;
        this.amount = presetAmount;
    }

    public CreateFoodNutritionSelectorItem(NutritionElement ne, int stringID, String presetData, boolean inputTypeString, boolean header) {
        this.header = header;
        this.stringID = stringID;
        this.ne = ne;
        this.tag = new SpannableString(ne.toString());
        this.inputTypeString = inputTypeString;
        this.data = presetData;
    }

    public CreateFoodNutritionSelectorItem(NutritionElement ne, int stringID, boolean inputTypeString, boolean header) {
        this.header = header;
        this.stringID = stringID;
        this.ne = ne;
        this.tag = new SpannableString(ne.toString());
        this.inputTypeString = inputTypeString;
    }


    @Override
    public int compareTo(CreateFoodNutritionSelectorItem other) {
        return this.tag.toString().compareTo(other.tag.toString());
    }
}
