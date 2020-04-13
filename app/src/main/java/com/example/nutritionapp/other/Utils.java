package com.example.nutritionapp.other;

import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class Utils {
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

    public static SortedMap<LocalDateTime, HashMap<Integer, ArrayList<Food>>> foodGroupsByDays(HashMap<Integer, ArrayList<Food>> foodGroups) {
        SortedMap<LocalDateTime, HashMap<Integer, ArrayList<Food>>> foodByDate = new TreeMap<>();
        if(foodGroups.size() == 0){
            return foodByDate;
        }
        for(Integer groupID : foodGroups.keySet()){
            for(Food f : foodGroups.get(groupID)){
                LocalDateTime day = f.loggedAt.atStartOfDay();
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
}
