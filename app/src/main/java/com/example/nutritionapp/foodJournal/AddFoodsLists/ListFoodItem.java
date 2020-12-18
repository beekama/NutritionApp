package com.example.nutritionapp.foodJournal.addFoodsLists;

import com.example.nutritionapp.other.Food;

public class ListFoodItem implements GroupListItem {
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


    public boolean equals(Food f) {
        return this.food.equals(f);
    }

    public boolean equals(ListFoodItem item) {
        return this.food.equals(item.food);
    }
}
