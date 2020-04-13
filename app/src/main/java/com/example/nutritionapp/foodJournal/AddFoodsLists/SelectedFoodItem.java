package com.example.nutritionapp.foodJournal.AddFoodsLists;

import com.example.nutritionapp.other.Food;

public class SelectedFoodItem {
    public final int amount;
    public final Food food;

    public SelectedFoodItem(Food f, int amountInGram){
        this.food = f;
        this.amount = amountInGram;
    }
}
