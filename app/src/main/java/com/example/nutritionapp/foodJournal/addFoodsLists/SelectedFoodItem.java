package com.example.nutritionapp.foodJournal.addFoodsLists;

import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.PortionType;

public class SelectedFoodItem {
    public final Food food;

    public SelectedFoodItem(Food f, double amount, PortionType portionTypes){
        this.food = f;
        this.food.associatedAmount = amount;
        this.food.associatedPortionType = portionTypes;
    }
}
