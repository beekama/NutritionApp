package com.example.nutritionapp.other;

public class Conversions {

    public static final int G_TO_UG = 1_000_000;
    public static final int MG_TO_UG = 1_000;
    public static final int KCAL_TO_JOULE = 4184;

    public static int normalize(String unitName, int inputAmount){
        /* normalize unitValues to their base values (microgramm or joule) */
        switch (unitName){
            case "UG":
            case "JOULE":
                return inputAmount;
            case "G":
                return inputAmount * G_TO_UG;
            case "MG":
                return inputAmount * MG_TO_UG;
            case "KCAL":
                return inputAmount * KCAL_TO_JOULE;
            case "N/A":
                /* N/A is ok for now */
                return inputAmount;
            default:
                throw new RuntimeException("Unknown unitName: " + unitName);
        }
    }

    public static int jouleToKCal(int joule){
        return joule/KCAL_TO_JOULE;
    }
}
