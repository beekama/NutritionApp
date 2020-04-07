package com.example.nutritionapp.other;

import java.util.ArrayList;

public class Utils {
    public static int zeroIfNull(Integer integer) {
        if(integer == null){
            return 0;
        }else{
            return integer;
        }
    }

    public static String foodArrayListToString(ArrayList<Food> selected) {
        String ret = "";
        for(Food f : selected){
            ret += f.name + "\n";
        }
        return ret;
    }
}
