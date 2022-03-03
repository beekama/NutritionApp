package com.example.nutritionapp.other;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class Nutrition {

    private HashMap<NutritionElement,Integer> elements = new HashMap<>();

    public static final String DB_ID_IRON = "1089";
    public static final String DB_ID_MAGNESIUM = "1090";
    public static final String DB_ID_ZINC = "1095";
    public static final String DB_ID_CALCIUM = "1087";
    public static final String DB_ID_POTASSIUM = "1092";
    public static final String DB_ID_SELENIUM = "1103";
    public static final String DB_ID_FOLIC_ACID = "1186";

    public static final String DB_ID_VITAMIN_A = "1106";
    public static final String DB_ID_VITAMIN_C = "1162";
    public static final String DB_ID_VITAMIN_D = "1114";
    public static final String DB_ID_VITAMIN_E = "1158";
    public static final String DB_ID_VITAMIN_K = "1183";
    public static final String DB_ID_VITAMIN_B1 = "1165";
    public static final String DB_ID_VITAMIN_B2 = "1166";
    public static final String DB_ID_VITAMIN_B3 = "1167";
    public static final String DB_ID_VITAMIN_B6 = "1175";
    public static final String DB_ID_VITAMIN_B12 = "1178";

    public final static HashMap<NutritionElement, String> nutritionElementToDatabaseId = new HashMap<>();
    static{
        nutritionElementToDatabaseId.put(NutritionElement.IRON, DB_ID_IRON);
        nutritionElementToDatabaseId.put(NutritionElement.MAGNESIUM, DB_ID_MAGNESIUM);
        nutritionElementToDatabaseId.put(NutritionElement.ZINC, DB_ID_ZINC);
        nutritionElementToDatabaseId.put(NutritionElement.CALCIUM, DB_ID_CALCIUM);
        nutritionElementToDatabaseId.put(NutritionElement.POTASSIUM, DB_ID_POTASSIUM);
        nutritionElementToDatabaseId.put(NutritionElement.SELENIUM, DB_ID_SELENIUM);
        nutritionElementToDatabaseId.put(NutritionElement.FOLIC_ACID, DB_ID_FOLIC_ACID);

        nutritionElementToDatabaseId.put(NutritionElement.VITAMIN_A, DB_ID_VITAMIN_A);
        nutritionElementToDatabaseId.put(NutritionElement.VITAMIN_B1, DB_ID_VITAMIN_B1);
        nutritionElementToDatabaseId.put(NutritionElement.VITAMIN_B2, DB_ID_VITAMIN_B2);
        nutritionElementToDatabaseId.put(NutritionElement.VITAMIN_B3, DB_ID_VITAMIN_B3);
        nutritionElementToDatabaseId.put(NutritionElement.VITAMIN_B6, DB_ID_VITAMIN_B6);
        nutritionElementToDatabaseId.put(NutritionElement.VITAMIN_B12, DB_ID_VITAMIN_B12);
        nutritionElementToDatabaseId.put(NutritionElement.VITAMIN_C, DB_ID_VITAMIN_C);
        nutritionElementToDatabaseId.put(NutritionElement.VITAMIN_D, DB_ID_VITAMIN_D);
        nutritionElementToDatabaseId.put(NutritionElement.VITAMIN_E, DB_ID_VITAMIN_E);
        nutritionElementToDatabaseId.put(NutritionElement.VITAMIN_K, DB_ID_VITAMIN_K);
    }


    public Nutrition(HashMap<String, Integer> nutrients) {

        /* Minerals */
        elements.put(NutritionElement.IRON, Utils.zeroIfNull(nutrients.get(DB_ID_IRON)));
        elements.put(NutritionElement.MAGNESIUM, Utils.zeroIfNull(nutrients.get(DB_ID_MAGNESIUM)));
        elements.put(NutritionElement.ZINC, Utils.zeroIfNull(nutrients.get(DB_ID_ZINC)));
        elements.put(NutritionElement.CALCIUM, Utils.zeroIfNull(nutrients.get(DB_ID_CALCIUM)));
        elements.put(NutritionElement.POTASSIUM, Utils.zeroIfNull(nutrients.get(DB_ID_POTASSIUM)));
        elements.put(NutritionElement.SELENIUM, Utils.zeroIfNull(nutrients.get(DB_ID_SELENIUM)));
        elements.put(NutritionElement.FOLIC_ACID, Utils.zeroIfNull(nutrients.get(DB_ID_FOLIC_ACID)));

        /* Vitamins */
        elements.put(NutritionElement.VITAMIN_A, Utils.zeroIfNull(nutrients.get(DB_ID_VITAMIN_A)));
        elements.put(NutritionElement.VITAMIN_B1, Utils.zeroIfNull(nutrients.get(DB_ID_VITAMIN_B1)));
        elements.put(NutritionElement.VITAMIN_B2, Utils.zeroIfNull(nutrients.get(DB_ID_VITAMIN_B2)));
        elements.put(NutritionElement.VITAMIN_B3, Utils.zeroIfNull(nutrients.get(DB_ID_VITAMIN_B3)));
        elements.put(NutritionElement.VITAMIN_B6, Utils.zeroIfNull(nutrients.get(DB_ID_VITAMIN_B6)));
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
        this.elements = new HashMap<>(orig.elements);
    }

    public static int databaseIdFromEnum(NutritionElement ne) {
        String dbId =  nutritionElementToDatabaseId.get(ne);
        if(dbId == null){
            Log.wtf("DB-ID NULL", ne.toString());
            return -1;
        }
        return Integer.parseInt(dbId);
    }

    public Nutrition getNutritionForAmount(float amount){
        Nutrition newNut = new Nutrition(this);
        for(NutritionElement el : newNut.elements.keySet()){
            Integer content = newNut.elements.getOrDefault(el, 0);
            float calculatedContent = content*amount/100f;
            newNut.elements.put(el, Math.round(calculatedContent));
        }
        return newNut;
    }


    public static Nutrition sum(ArrayList<Nutrition> list) {
        Nutrition ret = new Nutrition();
        /* mental  boom */
        list.forEach(n ->  n.elements.keySet().forEach(nutritionElement -> ret.elements.compute(nutritionElement, (key, curVal) -> curVal + n.elements.get(key))));
        return ret;
    }

    public static Nutrition subtract(Nutrition a, Nutrition b){
        Nutrition ret = new Nutrition();
        a.elements.forEach((nutritionElement, integer) -> ret.elements.put(nutritionElement, a.elements.get(nutritionElement) - b.elements.get(nutritionElement)));
        return ret;
    }

    public static HashMap<NutritionElement, Float> percentages(Nutrition a, Nutrition b){
        HashMap<NutritionElement, Float> ret = new HashMap<>();
        for (NutritionElement nutEl : a.elements.keySet()) {
            if(b.elements.get(nutEl) != 0){
                ret.put(nutEl, (float)a.elements.get(nutEl)*100 / (float)b.elements.get(nutEl));
            }
        }
        return ret;
    }


    public static Nutrition getRecommendation() {

        /* Returns the correct recommendation (USDA Values)*/
        Nutrition allowance = new Nutrition();

        /* all units in microgram */
        allowance.elements.put(NutritionElement.CALCIUM, 1200);
        allowance.elements.put(NutritionElement.IRON, 18);
        allowance.elements.put(NutritionElement.MAGNESIUM, 420);
        allowance.elements.put(NutritionElement.POTASSIUM, 4700);
        allowance.elements.put(NutritionElement.ZINC, 11);
        allowance.elements.put(NutritionElement.SELENIUM, 55);
        allowance.elements.put(NutritionElement.FOLIC_ACID, 400);
        allowance.elements.put(NutritionElement.VITAMIN_A, 900);
        allowance.elements.put(NutritionElement.VITAMIN_B1, 1100);
        allowance.elements.put(NutritionElement.VITAMIN_B2, 1300);
        allowance.elements.put(NutritionElement.VITAMIN_B3, 16000);
        allowance.elements.put(NutritionElement.VITAMIN_B6, 1300);
        allowance.elements.put(NutritionElement.VITAMIN_B12, 3);
        allowance.elements.put(NutritionElement.VITAMIN_C, 90000);
        allowance.elements.put(NutritionElement.VITAMIN_D, 15);
        allowance.elements.put(NutritionElement.VITAMIN_E, 15000);
        allowance.elements.put(NutritionElement.VITAMIN_K, 110);

        /* convert to native units of db */
        for(NutritionElement el : allowance.elements.keySet()){
            String nutrientNativeUnit = Database.getNutrientNativeUnit(Integer.toString(databaseIdFromEnum(el)));
            Log.wtf("TAG",  nutrientNativeUnit + " " + el);
            int converted = Conversions.convert(Conversions.MICROGRAM, nutrientNativeUnit, allowance.elements.get(el));
           allowance.elements.put(el, converted);
        }

        return allowance;
    }


    public static Nutrition getRecommendationMultipleDays(Integer days){
        Nutrition allowanceMultipleDays = getRecommendation();
        for(NutritionElement ne : allowanceMultipleDays.elements.keySet()){
            allowanceMultipleDays.elements.put(ne, allowanceMultipleDays.elements.get(ne)*days);
        }
        return allowanceMultipleDays;
    }

    public static int totalEnergy(ArrayList<Food> calculatedFrom) {
        int total = 0;
        for(Food f : calculatedFrom){
            if(f.associatedAmount > 0) {
                total += f.energy * ((f.associatedAmount*f.portionTypeInGram)/100f);
            }else{
                total += f.energy;
            }
        }
        return total;
    }

    public HashMap<NutritionElement, Integer> getElements() {
        return elements;
    }

    public void put(NutritionElement key, int value) {
        this.elements.put(key, value);
    }
}