package com.example.nutritionapp.other;

import org.threeten.bp.LocalDate;

import java.util.HashMap;

public class Food {

    private static final String DB_ID_ENERGY = "1008";
    private static final String DB_ID_FIBER = "1079";

    public String name;
    public String id;
    public int energy;
    public int fiber;
    public Minerals minerals;
    public Vitamins vitamins;
    public LocalDate loggedAt;

    public Food(String name, int energy, int fiber, Minerals minerals, Vitamins vitamins, LocalDate logTime) {
        this.name = name;
        this.energy = energy;
        this.fiber = fiber;
        this.minerals = minerals;
        this.vitamins = vitamins;
        this.loggedAt = logTime;
    }

    public Food(String foodName, String foodId, Database db, LocalDate loggedAt) {
        this.name = foodName;
        this.id = foodId;
        HashMap<String,Integer> nutrients = db.getNutrientsForFood(foodId);
        if(nutrients != null) {
            this.fiber = nutrients.get(DB_ID_FIBER);
            this.energy = nutrients.get(DB_ID_ENERGY);
            this.minerals = new Minerals(nutrients);
            this.vitamins = new Vitamins(nutrients);
        }else{
            this.fiber = 0;
            this.energy = 0;
            this.minerals = new Minerals();
            this.vitamins = new Vitamins();
        }
        this.loggedAt = loggedAt;
    }

    public static Food getEmptyFood(LocalDate logTime){
        Food f = new Food("<Placeholder>", 0, 0, new Minerals(), new Vitamins(), logTime);
        f.id = "781105";
        return f;
    }
}

