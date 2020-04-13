package com.example.nutritionapp.other;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class NutritionAnalysis {
    private ArrayList<Food> calculatedFrom;
    final private Nutrition nutritionTarget = Nutrition.getRecommendation();

    private Nutrition nutritionActual;
    private Nutrition nutritionMissing;
    private int totalEnergy;
    private HashMap<NutritionElement, Float> nutritionPercentage;

    public NutritionAnalysis(ArrayList<Food> calculatedFrom){
        this.calculatedFrom = calculatedFrom;
        this.nutritionActual = calculateTotalNutrition(calculatedFrom);
        this.nutritionMissing = Nutrition.subtract(nutritionTarget, nutritionActual);
        this.nutritionPercentage = Nutrition.percentages(nutritionActual, nutritionTarget);
        this.totalEnergy = Nutrition.totalEnergy(calculatedFrom);
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

    public ArrayList<NutritionPercentageTupel> getNutritionPercentageSorted() {
        ArrayList<NutritionPercentageTupel> ret = new ArrayList<>();
        for(NutritionElement key: nutritionPercentage.keySet()){
            NutritionPercentageTupel n = new NutritionPercentageTupel(key, nutritionPercentage.get(key));
            ret.add(n);
        }
        Collections.sort(ret);
        return ret;
    }

    public int getTotalEnergy() {
        return totalEnergy;
    }
}
