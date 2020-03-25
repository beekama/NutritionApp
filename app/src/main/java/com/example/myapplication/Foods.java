package com.example.myapplication;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

class Food {
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

    public static Food getEmptyFood(LocalDate logTime){
        Food f = new Food("<Placeholder>", 0, 0, new Minerals(), new Vitamins(), logTime);
        f.id = "336106";
        return f;
    }

}

class Vitamins{
    public int A;
    public int C;
    public int D;
    public int E;
    public int K;
    public int B6;
    public int B12;
}

class Minerals {
    public int iron;
    public int magnesium;
    public int zinc;
    public int calcium;
    public int potassium;
}