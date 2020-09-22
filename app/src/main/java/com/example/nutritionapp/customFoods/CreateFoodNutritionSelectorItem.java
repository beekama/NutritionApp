package com.example.nutritionapp.customFoods;


import com.example.nutritionapp.other.NutritionElement;


public class CreateFoodNutritionSelectorItem implements Comparable<CreateFoodNutritionSelectorItem> {
    public final String tag;
    public final String unit;
    public NutritionElement ne = null;
    public int amount = 0;
    public final boolean inputTypeString;
    public String data = null;

    public CreateFoodNutritionSelectorItem(String name, boolean inputTypeString) {
        this.tag = name;
        this.unit = "N/A";
        this.inputTypeString = inputTypeString;
    }

    public CreateFoodNutritionSelectorItem(String name, int presetAmount, boolean inputTypeString) {
        this.tag = name;
        this.unit = "N/A";
        this.inputTypeString = inputTypeString;
        this.amount = presetAmount;
    }

    public CreateFoodNutritionSelectorItem(String name, String presetData, boolean inputTypeString) {
        this.tag = name;
        this.unit = "N/A";
        this.inputTypeString = inputTypeString;
        this.data = presetData;
    }

    public CreateFoodNutritionSelectorItem(NutritionElement ne, int presetAmount, boolean inputTypeString) {
        this.ne = ne;
        this.tag = ne.toString();
        this.unit = "N/A";
        this.inputTypeString = inputTypeString;
        this.amount = presetAmount;
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
