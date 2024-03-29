package com.example.nutritionapp.foodJournal.overviewFoodsLists;

import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class FoodOverviewListItem{
    public final LocalDate date;
    public ArrayList<Food> foods;
    public final HashMap<Integer, ArrayList<Food>> foodGroups;
    private final Database db;
    public boolean dirty = false;

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

            /* append foods */
            foods.addAll(foodsInGroup);
        }
    }

    public void update(int groupId) {
        ArrayList<Food> tmp = new ArrayList<>();
        this.foodGroups.put(groupId, db.getLoggedFoodByGroupId(groupId));
        for(ArrayList<Food> fa : foodGroups.values()){
            tmp.addAll(fa);
        }
        this.foods = tmp;
    }
}
