package com.example.nutritionapp.other;

import android.app.Activity;
import android.app.ActivityOptions;
import android.os.Bundle;
import android.util.Log;


import com.example.nutritionapp.recommendation.RecommendationNutritionListItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Utils {

    public static final DateTimeFormatter sqliteDatetimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter sqliteDateZeroPaddedFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00");
    public static final DateTimeFormatter sqliteDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter sqliteTimeFormat = DateTimeFormatter.ofPattern("HH:mm");

    public static int zeroIfNull(Integer integer) {
        if (integer == null) {
            return 0;
        } else {
            return integer;
        }
    }

    public static String foodArrayListToString(ArrayList<Food> selected) {
        StringBuilder ret = new StringBuilder();
        for (Food f : selected) {
            ret.append(f.name).append("\n");
        }
        return ret.toString();
    }

    public static SortedMap<LocalDate, HashMap<Integer, ArrayList<Food>>> foodGroupsByDays(HashMap<Integer, ArrayList<Food>> foodGroups) {
        SortedMap<LocalDate, HashMap<Integer, ArrayList<Food>>> foodByDate = new TreeMap<>();
        if (foodGroups.size() == 0) {
            return foodByDate;
        }

        for (Integer groupID : foodGroups.keySet()) {
            ArrayList<Food> foods = foodGroups.get(groupID);
            if (foods == null) {
                throw new AssertionError("Food group for a groupId was null, this should not be possible.");
            }
            for (Food f : foods) {

                LocalDate day = LocalDate.from(f.loggedAt);
                HashMap<Integer, ArrayList<Food>> foodGroupsAtDay = foodByDate.get(day);

                if (foodGroupsAtDay == null) {
                    foodGroupsAtDay = new HashMap<>();
                    foodByDate.put(day, foodGroupsAtDay);
                }

                if (foodGroupsAtDay.containsKey(groupID)) {
                    ArrayList<Food> foodListForGroupOnDay = foodGroupsAtDay.get(groupID);
                    if (foodListForGroupOnDay != null) {
                        foodListForGroupOnDay.add(f);
                        Log.wtf("Missing ID", "We seem to be missing a group id here..?");
                    }
                } else {
                    ArrayList<Food> tmpList = new ArrayList<>();
                    tmpList.add(f);
                    foodGroupsAtDay.put(groupID, tmpList);
                }
            }
        }

        return foodByDate;
    }

    public static SortedMap<Food, Float> sortRecommendedTreeMap(TreeMap<Food, Float> treeMap) {
        SortedMap<Food, Float> sortedFood = new TreeMap<Food, Float>( (k1,k2) -> {
            float v1 = treeMap.get(k1);
            float v2 = treeMap.get(k2);
            if (v1 == v2) return 0;
            else return (v1<v2) ? 1 : -1;
        });

        sortedFood.putAll((Map<Food, Float>)treeMap);
        return sortedFood;
    }

    public static Bundle getDefaultTransition(Activity activity) {
        return ActivityOptions.makeSceneTransitionAnimation(activity).toBundle();
    }
}
