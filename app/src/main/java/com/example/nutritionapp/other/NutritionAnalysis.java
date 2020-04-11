package com.example.nutritionapp.other;

import java.util.ArrayList;
import java.util.HashMap;

public class NutritionAnalysis {
    private ArrayList<Food> calculatedFrom;
    final private Nutrition nutritionTarget = Nutrition.getRecommendation();

    private Nutrition nutritionActual;
    private Nutrition nutritionMissing;
    private HashMap<NutritionElement, Float> nutritionPercentage;

    public NutritionAnalysis(ArrayList<Food> calculatedFrom){
        this.calculatedFrom = calculatedFrom;
        this.nutritionActual = calculateTotalNutrition(calculatedFrom);
        this.nutritionMissing = Nutrition.subtract(nutritionTarget, nutritionActual);
        this.nutritionPercentage = Nutrition.percentages(nutritionActual, nutritionTarget);
    }

    private static Nutrition calculateTotalMinerals(ArrayList<Food> calculatedFrom) {
        ArrayList<Nutrition> mineralsCalculatedFrom = new ArrayList<>();
        for(Food f : calculatedFrom){
            mineralsCalculatedFrom.add(f.nutrition);
        }
        return Nutrition.sum(mineralsCalculatedFrom);
    }

    private static Nutrition calculateTotalNutrition(ArrayList<Food> calculatedFrom) {
        ArrayList<Nutrition> vitaminsCalculatedFrom = new ArrayList<>();
        for(Food f : calculatedFrom){
            vitaminsCalculatedFrom.add(f.nutrition);
        }
        return Nutrition.sum(vitaminsCalculatedFrom);
    }

    public Nutrition getNutritionMissing() {
        return nutritionMissing;
    }

    public Nutrition getNutritionActual() {
        return nutritionActual;
    }

    public HashMap<NutritionElement, Float> getNutritionPercentage() {
        return nutritionPercentage;
    }
}
