package com.example.nutritionapp.customFoods;

import com.example.nutritionapp.other.Food;

public class CustomFoodOverviewItem extends CustomOverviewItem{
    public final Food food;
    public CustomFoodOverviewItem(Food f){
        this.food = f;
        this.isGroup = false;
        if(f.id == null || f.id.equals("") || f.id.equals("-1")){
            throw new AssertionError("Food in overview must have id != null | empty string | -1");
        }
        this.displayName = food.name;
    }
}
