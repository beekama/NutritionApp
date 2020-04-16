package com.example.nutritionapp.foodJournal.OverviewFoodsLists;

import com.example.nutritionapp.other.Food;

import java.util.ArrayList;
import java.util.HashMap;

public class FoodOverviewListItem{
    public final String date;
    public ArrayList<Food> foods;
    public HashMap<Integer, ArrayList<Food>> foodGroups;

    public FoodOverviewListItem(String date, ArrayList<Food> foods, HashMap<Integer, ArrayList<Food>> foodGroups) {
        this.date = date;
        this.foods = foods;
        this.foodGroups = foodGroups;
    }

}
