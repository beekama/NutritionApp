package com.example.nutritionapp.other;

public class NutritionPercentageTuple implements  Comparable<NutritionPercentageTuple>{
    public final NutritionElement nutritionElement;
    public final float percentage;

    public NutritionPercentageTuple(NutritionElement nutEl, Float percentage) {
        this.nutritionElement = nutEl;
        this.percentage = percentage;
    }

    @Override
    public int compareTo(NutritionPercentageTuple other) {
        return Float.compare(this.percentage, other.percentage);
    }
}
