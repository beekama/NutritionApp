package com.example.nutritionapp.other;

import android.content.Context;

public enum NutritionElement {
    IRON,
    MAGNESIUM,
    ZINC,
    CALCIUM,
    POTASSIUM,
    SELENIUM,
    FOLIC_ACID,
    VITAMIN_A,
    VITAMIN_C,
    VITAMIN_B1,
    VITAMIN_B2,
    VITAMIN_B3,
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
