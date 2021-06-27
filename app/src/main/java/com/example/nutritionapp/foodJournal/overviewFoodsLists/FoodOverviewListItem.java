package com.example.nutritionapp.foodJournal.overviewFoodsLists;

import android.util.Log;

import com.example.nutritionapp.other.Food;

import java.util.ArrayList;
import java.util.HashMap;

public class FoodOverviewListItem{
    public final String date;
    public final ArrayList<Food> foods;
    public final HashMap<Integer, ArrayList<Food>> foodGroups;

    public FoodOverviewListItem(String date, ArrayList<Food> foods, HashMap<Integer, ArrayList<Food>> foodGroups) {
        this.date = date;
        this.foods = foods;
        this.foodGroups = foodGroups;
    }

    public void reload() {
        /* TODO reload data */
    }
}
