package com.example.nutritionapp.customFoods;

import com.example.nutritionapp.other.Food;

import java.util.ArrayList;

public class CustomGroupOverviewItem extends CustomOverviewItem{
    public int groupId;
    public String groupName;
    public ArrayList<Food> foodList;

    public CustomGroupOverviewItem(ArrayList<Food> fl, int groupId){
        this.groupName = "Food Group w. " + fl.size() + " items " + groupId;
        this.isGroup = true;
        this.foodList = fl;
        this.displayName = groupName;
        this.groupId = groupId;
    }
}
