package com.example.nutritionapp.customFoods;

import com.example.nutritionapp.other.Food;

import java.util.ArrayList;

public class FoodOverviewItem {
    public final Food food;
    public boolean isGroup = false;

    public FoodOverviewItem(Food f){
        this.food = f;
        if(f.id == null || f.id.equals("") || f.id.equals("-1")){
            throw new AssertionError("Food in overview must have id != null | empty string | -1");
        }
    }

    /* FIXME: used for templated food groups */
    public FoodOverviewItem(ArrayList<Food> fl, int groupId){
        this.food = new Food("Food Group w. " + fl.size() + " items", Integer.toString(groupId));
        this.isGroup = true;
    }
}
