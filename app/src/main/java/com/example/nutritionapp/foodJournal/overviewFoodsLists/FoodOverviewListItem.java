package com.example.nutritionapp.foodJournal.overviewFoodsLists;

import android.util.Log;

import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class FoodOverviewListItem{
    public final LocalDate date;
    public final ArrayList<Food> foods;
    public final HashMap<Integer, ArrayList<Food>> foodGroups;
    private final Database db;

    public FoodOverviewListItem(LocalDate date, HashMap<Integer, ArrayList<Food>> foodGroups, Database db) {

        assert foodGroups != null;
        assert db != null;

        this.date = date;
        this.db = db;
        this.foodGroups = foodGroups;

        foods = new ArrayList<>();
        for (Integer groupId : foodGroups.keySet()) {

            ArrayList<Food> foodsInGroup = foodGroups.get(groupId);
            if (foodsInGroup == null) {
                throw new AssertionError("Got null when querying for group id.");
            }

            /* set nutrition and energy */
            for (Food foodToBeSet : foodsInGroup) {
                foodToBeSet.setPreferedPortionFromDb(db);
                foodToBeSet.setNutritionFromDb(db);
            }

            /* append foods */
            foods.addAll(foodsInGroup);
        }
    }
}
