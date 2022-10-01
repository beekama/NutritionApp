package com.example.nutritionapp.other;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

public class Utils {

    public static final DateTimeFormatter sqliteDatetimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter sqliteDateZeroPaddedFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00");
    public static final DateTimeFormatter sqliteDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter sqliteTimeFormat = DateTimeFormatter.ofPattern("HH:mm");


    public static final int FOOD_GROUP_DETAILS_ID = 1;
    private static final String RATIO_SEPARATOR = "/";

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
                HashMap<Integer, ArrayList<Food>> foodGroupsAtDay = foodByDate.computeIfAbsent(day, k -> new HashMap<>());

                if (foodGroupsAtDay.containsKey(groupID)) {
                    ArrayList<Food> foodListForGroupOnDay = foodGroupsAtDay.get(groupID);
                    if (foodListForGroupOnDay != null) {
                        foodListForGroupOnDay.add(f);
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
        SortedMap<Food, Float> sortedFood = new TreeMap<>((k1, k2) -> {
            float v1 = Objects.requireNonNull(treeMap.get(k1));
            float v2 = Objects.requireNonNull(treeMap.get(k2));
            if (v1 == v2){
                return 0;
            }else {
                return (v1 < v2) ? 1 : -1;
            }
        });

        sortedFood.putAll(treeMap);
        return sortedFood;
    }

    public static ArrayList<LocalDate> sortLocalDatesDesc(ArrayList<LocalDate> dates){
        Collections.sort(dates);
        return dates;
    }

    public static Bundle getDefaultTransition(Activity activity) {
        return ActivityOptions.makeSceneTransitionAnimation(activity).toBundle();
    }

    /* since month start with 0 in android */
    public static int monthDefaultToAndroid(int month){
        return month-1;
    }

    public static int monthAndroidToDefault(int month){
        return (month + 1)%13;
    }

    /* weight integer (gram) to float (kg) */
    public static float intWeightToFloat(Integer weight){
        return ((float) weight) /1000;
    }

    /* weight float (kg) to integer (gram) */
    public static int floatWeightToInt(Float weight){
        return (int) (weight*1000);
    }

    /* load string resource for dynamic string */
    public static int getStringIdentifier(Context context, String name) {
        return context.getResources().getIdentifier(name, "string", context.getPackageName());
    }

    /* get Date in Format "DAY_OF_WEEK, MMM DAY_OF_MONTH" as Spannable */
    public static Spannable getDateFormattedHeader(Context context, LocalDate logDate){
        String dayOfWeek_caps = (logDate == LocalDate.now()) ? "TODAY" : logDate.getDayOfWeek().toString();
        String dayOfWeek = context.getString(getStringIdentifier(context, dayOfWeek_caps));
        String month_caps = logDate.getMonth().toString();
        String month = context.getString(getStringIdentifier(context, month_caps)).substring(0,3);
        String dayOfMonth = Integer.toString(logDate.getDayOfMonth());

        Spannable dateHeader = new SpannableString(dayOfWeek + ", " + month + " " + dayOfMonth);
        dateHeader.setSpan(new RelativeSizeSpan(0.8f), dayOfWeek.length(), dateHeader.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return  dateHeader;
    }

    public static ArrayList<Double> amountsForPortionType(PortionType typeSelected) {
        ArrayList<Double> ret;
        switch(typeSelected){
            case FLUID_OUNCE:
            case ML:
            case GRAM:
            case PIECE:
            case TEASPOON:
            case TABLESPOON:
                Double[] smallBaseUnit = {0.5, 1.0, 2.0, 3.0, 4.0, 5.0, 10.0, 20.0, 50.0, 100.0, 200.0};
                ret = new ArrayList<>(Arrays.asList(smallBaseUnit));
                break;
            case SCOOP:
            case PACKET:
            case CUP:
            case SMALL:
            case MEDIUM:
            case LARGE:
            default:
                Double[] bigBaseUnit = {0.125, 0.25, 0.5, 0.75, 1.0, 1.5, 2.0, 3.0, 4.0, 5.0, 10.0};
                ret = new ArrayList<>(Arrays.asList(bigBaseUnit));
                break;
        }
        return ret;
    }

    public static double parseVisualizedDouble(String input){
        return Double.parseDouble(input);
    }

    public static String visualizedDouble(Double d) {
        if(d == 0.125) {
            return "⅛";
        }else if(d == 0.25){
            return "¼";
        }else if(d == 0.5){
            return "½";
        }else if(d == 0.75){
            return "¾";
        }else{
            double fraction = d - d.intValue();
            if(fraction > 0){
                return d.intValue() + " " + visualizedDouble(fraction);
            }else {
                return Integer.toString(d.intValue());
            }
        }
    }

    public static String capitalize(String string, boolean lowercaseFirst) {
        if(lowercaseFirst){
            string = string.toLowerCase();
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
