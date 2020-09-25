package com.example.nutritionapp;

import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;

import java.util.ArrayList;

public class JournalFoodSampleGenerator {
    public static ArrayList<Food> generateSampleFoodGroup(Database db){
        String[] fdc_ids = { "781092", "782010" , "785787" };
        ArrayList<Food> ret = new ArrayList<>();
        for(String id : fdc_ids){
            Food f = db.getFoodById(id);
            f.setAssociatedAmount(100);
            ret.add(f);
        }
        return  ret;
    }
}
