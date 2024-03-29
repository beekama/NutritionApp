package com.example.nutritionapp.other;

import java.util.ArrayList;
import java.util.HashMap;

public class CuratedFoods {
    static final HashMap<NutritionElement, String[]> curated = new HashMap<>();

    static final String[] VITAMIN_A_LIST  = { "Carrot", "Sweet Potato", "Spinach", "Broccoli", "Pumpkin", "Lettuce" };
    static final String[] VITAMIN_C_LIST  = { "Kiwi", "Orange", "Banana", "Apple", "Peas"};
    static final String[] VITAMIN_B6_LIST = { "Salmon" };
    static final String[] VITAMIN_D_LIST  = { "Egg", "Cod", "Sun" };
    static final String[] VITAMIN_E_LIST  = { "Sunflower Seeds", "Almonds", "Avocado", "Peanuts" };
    static final String[] VITAMIN_K_LIST  = { "Lettuce", "Spinach" };
    static final String[] IRON_LIST       = { "Oatmeal", "Tofu", "Red Meat", "Dark Chocolate" };
    static final String[] MAGNESIUM_LIST  = { "Oatmeal", "Banana", "Dark Chocolate", "Nuts", "Tofu" };
    static final String[] CALCIUM_LIST    = { "Mild", "Cheese", "Yogurt" };
    static final String[] SELENIUM_LIST   = { "Carrot" };
    static final String[] POTASSIUM_LIST  = { "Apple" };

    static{
        curated.put(NutritionElement.VITAMIN_A,  VITAMIN_A_LIST );
        curated.put(NutritionElement.VITAMIN_C,  VITAMIN_C_LIST );
        curated.put(NutritionElement.VITAMIN_D,  VITAMIN_D_LIST );
        curated.put(NutritionElement.VITAMIN_E,  VITAMIN_E_LIST );
        curated.put(NutritionElement.VITAMIN_K,  VITAMIN_K_LIST );
        curated.put(NutritionElement.VITAMIN_B6, VITAMIN_B6_LIST);
        curated.put(NutritionElement.IRON,       IRON_LIST      );
        curated.put(NutritionElement.MAGNESIUM,  MAGNESIUM_LIST );
        curated.put(NutritionElement.CALCIUM,    CALCIUM_LIST   );
        curated.put(NutritionElement.POTASSIUM,  POTASSIUM_LIST );
        curated.put(NutritionElement.SELENIUM,   SELENIUM_LIST  );
    }
    public static ArrayList<Food> foodsForNutrition(NutritionElement el){
        ArrayList<Food> foods = new ArrayList<>();
        return foods;
    }
}
