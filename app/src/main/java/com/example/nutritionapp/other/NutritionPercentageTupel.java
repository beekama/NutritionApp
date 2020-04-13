package com.example.nutritionapp.other;

public class NutritionPercentageTupel implements  Comparable<NutritionPercentageTupel>{
    public NutritionElement nutritionElement;
    public float percentage;

    public NutritionPercentageTupel(NutritionElement nutEl, Float percentage) {
        this.nutritionElement = nutEl;
        this.percentage = percentage;
    }

    @Override
    public int compareTo(NutritionPercentageTupel other) {
        return Float.compare(this.percentage, other.percentage);
    }
}
