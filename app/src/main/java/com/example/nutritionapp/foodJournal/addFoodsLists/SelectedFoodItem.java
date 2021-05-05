package com.example.nutritionapp.foodJournal.addFoodsLists;

import android.util.Pair;

import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.PortionTypes;

public class SelectedFoodItem {
    public final Food food;

    public SelectedFoodItem(Food f, Float amount, PortionTypes portionTypes){
        this.food = f;
        this.food.associatedAmount = amount;
        this.food.associatedPortionType = portionTypes;
    }
}
