package com.example.nutritionapp.foodJournal.overviewFoodsLists;

import com.example.nutritionapp.other.Food;

import java.util.ArrayList;

public class GroupFoodItem{
        public final ArrayList<Food> foods;
        public final int groupId;

        public GroupFoodItem(ArrayList<Food> foods, int groupId) {
            this.foods = foods;
            this.groupId = groupId;
        }

        public boolean isSection() {
            return false;
        }
}
