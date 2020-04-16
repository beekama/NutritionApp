package com.example.nutritionapp.foodJournal.OverviewFoodsLists;

import com.example.nutritionapp.other.Food;

import java.util.ArrayList;

public class GroupFoodItem{
        public final ArrayList<Food> foods;

        public GroupFoodItem(ArrayList<Food> foods) {
            this.foods = foods;
        }

        public boolean isSection() {
            return false;
        }
}
