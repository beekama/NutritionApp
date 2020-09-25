package com.example.nutritionapp.customFoods;

import com.example.nutritionapp.other.Food;

public class FoodOverviewItem {
    public final Food food;

    public FoodOverviewItem(Food f){
        this.food = f;
        if(f.id == null || f.id.equals("") || f.id.equals("-1")){
            throw new AssertionError("Food in overview must have id != null | empty string | -1");
        }
    }
}
