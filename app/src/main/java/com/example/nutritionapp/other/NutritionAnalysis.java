package com.example.nutritionapp.other;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class NutritionAnalysis {
    private final ArrayList<Food> calculatedFrom;
    final private Nutrition nutritionTarget = Nutrition.getRecommendation();

    private final Nutrition nutritionActual;
    private final Nutrition nutritionMissing;
    private final int totalEnergy;
    private final HashMap<NutritionElement, Float> nutritionPercentage;

    public NutritionAnalysis(ArrayList<Food> calculatedFrom) {
        this.calculatedFrom = calculatedFrom;
        this.nutritionActual = calculateTotalNutrition(calculatedFrom);
        this.nutritionMissing = Nutrition.subtract(nutritionTarget, nutritionActual);
        this.nutritionPercentage = Nutrition.percentages(nutritionActual, nutritionTarget);
        this.totalEnergy = Nutrition.totalEnergy(calculatedFrom);
    }

    private static Nutrition calculateTotalNutrition(ArrayList<Food> calculatedFrom) {
        ArrayList<Nutrition> nutritionCalculatedFrom = new ArrayList<>();
        for (Food f : calculatedFrom) {
                Float amountGram = f.getAssociatedAmount() * f.getAssociatedPortionTypeAmount();
                nutritionCalculatedFrom.add(f.nutrition.getNutritionForAmount(amountGram));
        }
        return Nutrition.sum(nutritionCalculatedFrom);
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

    public ArrayList<NutritionPercentageTuple> getNutritionPercentageSorted() {
        ArrayList<NutritionPercentageTuple> ret = new ArrayList<>();
        for (NutritionElement key : nutritionPercentage.keySet()) {
            NutritionPercentageTuple n = new NutritionPercentageTuple(key, nutritionPercentage.get(key));
            ret.add(n);
        }
        Collections.sort(ret);
        return ret;
    }


    public HashMap<NutritionElement, Float> getNutritionPercentageMultipleDays(Integer days) {
        return Nutrition.percentages(nutritionActual, Nutrition.getRecommendationMultipleDays(days));
    }

    public ArrayList<NutritionPercentageTuple> getNutritionPercentageSortedFilterZero() {
        ArrayList<NutritionPercentageTuple> ret = new ArrayList<>();
        for (NutritionElement key : nutritionPercentage.keySet()) {
            NutritionPercentageTuple n = new NutritionPercentageTuple(key, nutritionPercentage.get(key));
            if (n.percentage == 0) {
                continue;
            }
            ret.add(n);
        }
        Collections.sort(ret);
        Collections.reverse(ret);
        return ret;
    }

    public int getTotalEnergy() {
        return totalEnergy;
    }
}
