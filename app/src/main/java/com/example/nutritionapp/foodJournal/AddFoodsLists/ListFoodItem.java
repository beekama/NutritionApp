package com.example.nutritionapp.foodJournal.AddFoodsLists;

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


    public boolean equals(Object o){
        try{
            Food f = (Food) o;
            return this.food.equals(f);
        }catch (ClassCastException e){
            ListFoodItem item = (ListFoodItem) o;
            return this.food.equals(item.food);
        }
    }
}
