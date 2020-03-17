package com.example.myapplication;

import java.util.Date;

public class Database {

    /* ################ FOOD LOGGING ############## */
    public void logExistingFoods(Food[] foods, Date d){
        /* This functions add a list of foods to the journal at a given date */
    }

    public Food[] getFoodByPartialName(String substring){
        /* This function searches for a given substring */
        return null;
    }

    public Food[] getSuggestionsForCombination(Food[] selectedSoFar){
        /* This function returns suggestions for foods to log based on previously selected combinations */
        return null;
    }

    /* ################ NEW FOODS ################## */
    public void addNewFood(String name){
        /* This function adds a new food to the database */
    }

    /* ########### Calculated from config ########### */
    public Minerals getMineralRecommendation(){
        /* Returns the correct mineral recommendation */
        return null;
    }

    public Vitamins getVitaminRecommendation(){
        /* Returns the correct vitamin recommendation */
        return null;
    }

    public int getEnergyRecommendation(){
        /* Return the recommended energy */
        return -1;
    }

    /* ########## SAVE CONFIG ############*/
    public void setPersonWeight(int weightInKg){

    }

    public void setPersonHeight(int sizeInCm){

    }

    public void setPersonGender(String gender){

    }

    public int getPersonWeight(){
        return -1;
    }

    public int getPersonHeight(){
        return -1;
    }

    public String getPersonGender(){
        return "";
    }
}
