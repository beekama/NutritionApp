package com.example.nutritionapp.other;

import android.os.Build;

import java.util.ArrayList;
import java.util.HashMap;

public class Nutrition {
    private HashMap<NutritionElement,Integer> elements = new HashMap<>();

    public static final String DB_ID_IRON = "1089";
    public static final String DB_ID_MAGNESIUM = "1090";
    public static final String DB_ID_ZINC = "1095";
    public static final String DB_ID_CALCIUM = "1087";
    public static final String DB_ID_POTASSIUM = "1092";

    public static final String DB_ID_VITAMIN_A = "1106";
    public static final String DB_ID_VITAMIN_C = "1162";
    public static final String DB_ID_VITAMIN_D = "1114";
    public static final String DB_ID_VITAMIN_E = "1158";
    public static final String DB_ID_VITAMIN_K = "1183";
    public static final String DB_ID_VITAMIN_B12 = "1178";

    public Nutrition(HashMap<String, Integer> nutrients) {

        /* Minerals */
        elements.put(NutritionElement.IRON, Utils.zeroIfNull(nutrients.get(DB_ID_IRON)));
        elements.put(NutritionElement.MAGNESIUM, Utils.zeroIfNull(nutrients.get(DB_ID_MAGNESIUM)));
        elements.put(NutritionElement.ZINC, Utils.zeroIfNull(nutrients.get(DB_ID_ZINC)));
        elements.put(NutritionElement.CALCIUM, Utils.zeroIfNull(nutrients.get(DB_ID_CALCIUM)));
        elements.put(NutritionElement.POTASSIUM, Utils.zeroIfNull(nutrients.get(DB_ID_POTASSIUM)));

        /* Vitamins */
        elements.put(NutritionElement.VITAMIN_A, Utils.zeroIfNull(nutrients.get(DB_ID_VITAMIN_A)));
        elements.put(NutritionElement.VITAMIN_B12, Utils.zeroIfNull(nutrients.get(DB_ID_VITAMIN_B12)));
        elements.put(NutritionElement.VITAMIN_C, Utils.zeroIfNull(nutrients.get(DB_ID_VITAMIN_C)));
        elements.put(NutritionElement.VITAMIN_D, Utils.zeroIfNull(nutrients.get(DB_ID_VITAMIN_D)));
        elements.put(NutritionElement.VITAMIN_E, Utils.zeroIfNull(nutrients.get(DB_ID_VITAMIN_E)));
        elements.put(NutritionElement.VITAMIN_K, Utils.zeroIfNull(nutrients.get(DB_ID_VITAMIN_K)));
    }

    public Nutrition(){
        for(NutritionElement el: NutritionElement.values()){
            elements.put(el, 0);
        }
    }

    public Nutrition(Nutrition orig){
        this.elements = new HashMap<NutritionElement, Integer>(orig.elements);
    }

    public Nutrition getNutritionForAmount(int amountInGram){
        Nutrition newNut = new Nutrition(this);
        for(NutritionElement el : newNut.elements.keySet()){
            float content = (float)newNut.elements.get(el);
            content = content*amountInGram/100f;
            newNut.elements.put(el, (int)content);
        }
        return newNut;
    }


    public static Nutrition sum(ArrayList<Nutrition> list) {
        Nutrition ret = new Nutrition();
        /* mental  boom */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            list.forEach(n ->  n.elements.keySet().forEach(nutritionElement -> ret.elements.compute(nutritionElement, (key, curVal) -> curVal + n.elements.get(key))));
        }else{
            for(Nutrition n : list){
                for(NutritionElement nutEl: n.elements.keySet()){
                    int curVal = ret.elements.get(nutEl);
                    ret.elements.put(nutEl, n.elements.get(nutEl) + curVal);
                }
            }
        }
        return ret;
    }

    public static Nutrition subtract(Nutrition a, Nutrition b){
        Nutrition ret = new Nutrition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            a.elements.forEach((nutritionElement, integer) -> ret.elements.put(nutritionElement, a.elements.get(nutritionElement) - b.elements.get(nutritionElement)));
        }else{
             for (NutritionElement nutEl : a.elements.keySet()) {
                ret.elements.put(nutEl, a.elements.get(nutEl) - b.elements.get(nutEl));
             }
        }
        return ret;
    }

    public static HashMap<NutritionElement, Float> percentages(Nutrition a, Nutrition b){
        HashMap<NutritionElement, Float> ret = new HashMap<>();
        for (NutritionElement nutEl : a.elements.keySet()) {
            if(b.elements.get(nutEl) != 0){
                ret.put(nutEl, (float)a.elements.get(nutEl) / (float)b.elements.get(nutEl));
            }
        }
        return ret;
    }


    public static Nutrition getRecommendation() {
        /* Returns the correct recommendation (USDA Values)*/
        Nutrition allowance = new Nutrition();
        allowance.elements.put(NutritionElement.CALCIUM, 1200);
        allowance.elements.put(NutritionElement.IRON, 18);
        allowance.elements.put(NutritionElement.MAGNESIUM, 420);
        allowance.elements.put(NutritionElement.POTASSIUM, 4700);
        allowance.elements.put(NutritionElement.ZINC, 11);
        allowance.elements.put(NutritionElement.VITAMIN_A, 900);
        allowance.elements.put(NutritionElement.VITAMIN_B12, 3);
        allowance.elements.put(NutritionElement.VITAMIN_C, 90000);
        allowance.elements.put(NutritionElement.VITAMIN_D, 15);
        allowance.elements.put(NutritionElement.VITAMIN_E, 15000);
        allowance.elements.put(NutritionElement.VITAMIN_K, 110);
        return allowance;
    }

    public static int totalEnergy(ArrayList<Food> calculatedFrom) {
        int total = 0;
        for(Food f : calculatedFrom){
            total += f.energy;
        }
        return total;
    }
}