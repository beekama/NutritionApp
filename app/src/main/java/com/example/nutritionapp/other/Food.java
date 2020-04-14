package com.example.nutritionapp.other;

import com.example.nutritionapp.foodJournal.AddFoodsLists.ListFoodItem;

import org.threeten.bp.LocalDate;

import java.util.HashMap;

public class Food {

    private static final String DB_ID_ENERGY = "1008";
    private static final String DB_ID_FIBER = "1079";

    public String name;
    public String id;
    public int energy;
    public int fiber;
    public Nutrition nutrition;
    public LocalDate loggedAt;

    public int associatedAmount = -1;

    public Food(String name, String id){
        this.id = id;
        this.name = name;
    }

    public Food(String foodName, String foodId, Database db, LocalDate loggedAt) {
        this.name = foodName;
        this.id = foodId;
        HashMap<String,Integer> nutrients = db.getNutrientsForFood(foodId);
        if(nutrients != null) {
            this.fiber = nutrients.get(DB_ID_FIBER);
            this.energy = nutrients.get(DB_ID_ENERGY);
            this.nutrition = new Nutrition();
        }else{
            this.fiber = 0;
            this.energy = 0;
            this.nutrition = new Nutrition();
        }
        this.loggedAt = loggedAt;
    }

    public boolean equals(Object o){
        try {
            Food f = (Food) o;
            return this.id.equals(f.id) && !this.id.equals("");
        }catch (ClassCastException e){
            ListFoodItem f = (ListFoodItem) o;
            return this.equals(f.food);
        }
    }

    public void setAssociatedAmount(int associatedAmount) {
        this.associatedAmount = associatedAmount;
    }

    public int getAssociatedAmount() {
        if(associatedAmount == -1){
            throw new AssertionError("Food for Database must have set associatedAmount in gram");
        }
        return associatedAmount;
    }

}

