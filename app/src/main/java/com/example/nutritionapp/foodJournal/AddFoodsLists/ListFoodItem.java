package com.example.nutritionapp.foodJournal.AddFoodsLists;

import com.example.nutritionapp.other.Food;

public class ListFoodItem implements GenericListItem{
        public final Food food;

        public ListFoodItem(Food food) {
            this.food = food;
        }

        public boolean isSection() {
            return false;
        }

        public String getTitle() {
            return food.name;
        }
}
