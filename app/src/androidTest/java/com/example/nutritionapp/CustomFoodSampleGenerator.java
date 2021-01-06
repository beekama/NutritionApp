package com.example.nutritionapp;

import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionElement;

public class CustomFoodSampleGenerator {
    public static Food buildCustomFoodWithNutrition(String name) {
        String EMPTY_ID = "";
        Food f1 = new Food(name, EMPTY_ID);
        f1.energy = 1000;
        f1.fiber = 10;
        Nutrition n1 = new Nutrition();
        n1.getElements().replace(NutritionElement.CALCIUM, 50);

        n1.getElements().replace(NutritionElement.VITAMIN_C, 20);
        n1.getElements().replace(NutritionElement.VITAMIN_D, 30);
        f1.nutrition = n1;
        return f1;
    }

}
