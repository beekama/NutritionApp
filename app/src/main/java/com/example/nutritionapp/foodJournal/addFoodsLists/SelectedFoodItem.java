package com.example.nutritionapp.foodJournal.addFoodsLists;

import android.util.Pair;

import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.PortionTypes;

public class SelectedFoodItem {
    public final Food food;

    public SelectedFoodItem(Food f, float amountInGram, PortionTypes portionTypes){
        this.food = f;
        this.food.associatedAmount = amountInGram;
        this.food.associatedPortionType = portionTypes;
    }
}
