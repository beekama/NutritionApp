package com.example.nutritionapp.other;

public class Utils {
    public static int zeroIfNull(Integer integer) {
        if(integer == null){
            return 0;
        }else{
            return integer;
        }
    }
}
