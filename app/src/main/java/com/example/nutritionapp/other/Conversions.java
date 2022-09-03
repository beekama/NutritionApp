package com.example.nutritionapp.other;


public class Conversions {

    public static final int G_TO_UG = 1_000_000;
    public static final int MG_TO_UG = 1_000;
    public static final double UG_TO_MG = 0.001;
    public static final int KCAL_TO_JOULE = 4184;

    public static final String JOULE = "JOULE";
    public static final String KCAL = "KCAL";
    public static final String MICROGRAM = "UG";
    public static final String MILLIGRAM = "MG";

    public static String getNativeUnitForNutritionElementUnsafe(NutritionElement e){
        switch(e){
            case SELENIUM:
            case VITAMIN_B12:
            case VITAMIN_B6:
            case VITAMIN_B3:
            case VITAMIN_B1:
            case VITAMIN_D:
            case CALCIUM:
                return Conversions.MICROGRAM;
            default:
                return Conversions.MILLIGRAM;
        }
    }

    public static int normalize(String unitName, int inputAmount){
        /* normalize unitValues to their base values (microgram or joule) */
        switch (unitName){
            case "UG":
            case "JOULE":
                return inputAmount;
            case "G":
                return inputAmount * G_TO_UG;
            case "MG":
                return (int)(inputAmount * UG_TO_MG);
            case "MG_ATE":
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

    public static int convert(String from, String to, int presetAmount) {
        int normalized = normalize(from, presetAmount);
        boolean isEnergyUnit = from.equals("KCAL") || from.equals("JOULE");
        boolean isWeightUnit = from.equals("MG") || from.equals("MG_ATE") || from.equals("UG") || from.equals("G");
        switch (to){
            case "UG":
                if(isWeightUnit) {
                    return normalized;
                }
            case "JOULE":
                if(isEnergyUnit) {
                    return normalized;
                }
            case "G":
                if(isWeightUnit) {
                    return normalized / G_TO_UG;
                }
            case "MG": return (int)(normalized * UG_TO_MG);
            case "MG_ATE":
                if(isWeightUnit) {
                    return normalized / MG_TO_UG;
                }
            case "KCAL":
                if(isEnergyUnit) {
                    return normalized / KCAL_TO_JOULE;
                }
            default:
                throw new RuntimeException("Bad Conversion." + from + " " + to);
        }
    }

    public static double convertPortion(double amountSelected, PortionType typeSelected, PortionType gram) {
        /* TODO: how to reliably convert this like for example for "medium apple" ?? */
        return amountSelected;
    }
}
