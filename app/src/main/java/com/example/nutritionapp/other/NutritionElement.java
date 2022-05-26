package com.example.nutritionapp.other;

import android.content.Context;

public enum NutritionElement {
    IRON,
    MAGNESIUM,
    ZINC,
    CALCIUM,
    POTASSIUM,
    SELENIUM,
    FOLIC_ACID,                     // Folacin
    VITAMIN_A,                      // Vitamin A + BetaCarotine
    VITAMIN_C,
    VITAMIN_B1,                     // Thiamin
    VITAMIN_B2,                     // Riboflavin
    VITAMIN_B3,                     // Niacin
    VITAMIN_B6,
    VITAMIN_B12,
    VITAMIN_D,
    VITAMIN_E,
    VITAMIN_K;


    /* get colloquial Name of NutritionElement */
    public String getString(Context context){
        int resId = context.getResources().getIdentifier(this.name(), "string", context.getPackageName());
        if (resId == 0){
            //no R.string available:
            return name();
        }
        return context.getResources().getString(resId);
    }
}
