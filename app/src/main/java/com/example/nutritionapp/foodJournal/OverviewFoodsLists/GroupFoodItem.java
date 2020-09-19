package com.example.nutritionapp.foodJournal.OverviewFoodsLists;

import com.example.nutritionapp.other.Food;

import java.util.ArrayList;

public class GroupFoodItem{
        public final ArrayList<Food> foods;
        public int groupId;

        public GroupFoodItem(ArrayList<Food> foods, int groupId) {
            this.foods = foods;
            this.groupId = groupId;
        }

        public boolean isSection() {
            return false;
        }
}
