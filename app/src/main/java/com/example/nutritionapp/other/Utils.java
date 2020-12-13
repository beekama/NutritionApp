package com.example.nutritionapp.other;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.os.Bundle;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class Utils {

    public static final DateTimeFormatter sqliteDatetimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter sqliteDateZeroPaddedFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00");
    public static final DateTimeFormatter sqliteDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter sqliteTimeFormat = DateTimeFormatter.ofPattern("HH:mm");

    public static int zeroIfNull(Integer integer) {
        if(integer == null){
            return 0;
        }else{
            return integer;
        }
    }

    public static String foodArrayListToString(ArrayList<Food> selected) {
        StringBuilder ret = new StringBuilder();
        for(Food f : selected){
            ret.append(f.name).append("\n");
        }
        return ret.toString();
    }

    public static SortedMap<LocalDate, HashMap<Integer, ArrayList<Food>>> foodGroupsByDays(HashMap<Integer, ArrayList<Food>> foodGroups) {
        SortedMap<LocalDate, HashMap<Integer, ArrayList<Food>>> foodByDate = new TreeMap<>();
        if(foodGroups.size() == 0){
            return foodByDate;
        }
        for(Integer groupID : foodGroups.keySet()){
            for(Food f : foodGroups.get(groupID)){
                LocalDate day = LocalDate.from(f.loggedAt);
                if(!foodByDate.containsKey(day)) {
                    foodByDate.put(day, new HashMap<>());
                }
                if(foodByDate.get(day).containsKey(groupID)){
                    foodByDate.get(day).get(groupID).add(f);
                }else{
                    ArrayList<Food> tmpList = new ArrayList<>();
                    tmpList.add(f);
                    foodByDate.get(day).put(groupID, tmpList);
                }
            }
        }
        return foodByDate;
    }

    public static Bundle getDefaultTransition(Activity activity){
        return ActivityOptions.makeSceneTransitionAnimation(activity).toBundle();
    }
}
