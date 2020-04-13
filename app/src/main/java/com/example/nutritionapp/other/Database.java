package com.example.nutritionapp.other;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.nutritionapp.R;

import org.apache.commons.io.IOUtils;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import static android.database.sqlite.SQLiteDatabase.OPEN_READWRITE;

public class Database {

    final String FILE_KEY = "DEFAULT";
    final SQLiteDatabase db;

    /* used to track create_foods added together */
    private static Activity srcActivity;

    private HashMap<String,Food> foodCache = new HashMap<String,Food>();

    private void copyDatabaseIfMissing(InputStream inputStream, String targetPath){
        File file = new File(targetPath);
        if (!file.exists()) {
            InputStream in = srcActivity.getResources().openRawResource(R.raw.food);

            try {
                OutputStream out = new FileOutputStream(targetPath);
                int bytes = IOUtils.copy(in, out);
                out.close();
                in.close();
            } catch (FileNotFoundException e) {
                Log.wtf("WTF", "OUTPUT file not found");
            } catch (IOException e) {
                Log.wtf("yeah whatever", "fuck");
            }
        }
    }

    public Database(Activity srcActivity) {
        this.srcActivity = srcActivity;
        InputStream srcFile = srcActivity.getResources().openRawResource(R.raw.food);
        String path = srcActivity.getFilesDir().getParent() + "/food.db";
        copyDatabaseIfMissing(srcFile, path);
        db = SQLiteDatabase.openDatabase(path, null, OPEN_READWRITE);
    }

    /* ################ FOOD LOGGING ############## */
    public synchronized void logExistingFoods(ArrayList<Food> foods, LocalDate d) {
        /* This functions add a list of create_foods to the journal at a given date */

        if(d == null){
            d = LocalDate.now();
        }

        Random random = new Random();
        int groupID =  random.nextInt(1000000);
        for (Food f : foods) {
            ContentValues values = new ContentValues();
            values.put("food_id", f.id);
            values.put("date", d.toString());
            values.put("group_id", groupID);
            values.put("loggedAt", d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00")));
            db.insert("foodlog", null, values);
        }
    }

    public HashMap<Integer, ArrayList<Food>> getLoggedFoodsByDate(LocalDate start, LocalDate end) {
        /* Return create_foods logged by dates */

        HashMap ret = new HashMap<Integer, ArrayList<Food>>();

        String startISO = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00"));
        String endISO = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00"));

        String sqlRaw = "SELECT food_id, group_id, loggedAt FROM foodlog where date(loggedAt) between date(\"" + endISO + "\") and date(\"" + startISO + "\")";
        if(start.equals(LocalDate.MIN) || end.equals(LocalDate.MAX)){
            Log.w("ERROR", "SQLITE can't handle theses Dates");
            sqlRaw = "SELECT food_id, group_id, loggedAt FROM foodlog where date(loggedAt)";
        }

        Cursor c = db.rawQuery(sqlRaw, null);
        if (c.moveToFirst()) {
            do {
                String foodId = c.getString(0);
                int groupID = c.getInt(1);
                String loggedAtISO = c.getString(2);

                if (ret.containsKey(groupID)) {
                    ArrayList<Food> group = (ArrayList<Food>) ret.get(groupID);
                    group.add(getFoodById(foodId, loggedAtISO));
                }else{
                    ArrayList<Food> group = new ArrayList<Food>();
                    group.add(getFoodById(foodId, loggedAtISO));
                    ret.put(groupID, group);
                }

            } while (c.moveToNext());
        }
        c.close();
        return ret;
    }

    public HashMap<String,Integer> getNutrientsForFood(String foodId){
        HashMap<String,Integer> ret = new HashMap<String,Integer>();
        Cursor nutrients = db.rawQuery("SELECT * FROM food_nutrient00 where fdc_id = \"" + foodId + "\"", null);
        if (nutrients.moveToFirst()) {
            do {
                String nutrientID = nutrients.getString(2);
                int rawAmount = nutrients.getInt(3);
                /* convert raw amount into native value */
                Cursor nutrientConversion = db.rawQuery("SELECT * FROM nutrient where id = \"" + nutrientID + "\"", null);
                if(nutrientConversion.moveToFirst()){
                    String unitName = nutrientConversion.getString(2);
                    int normalizedAmount = Conversions.normalize(unitName, rawAmount);
                    ret.put(nutrientID, normalizedAmount);
                }else{
                    throw new RuntimeException("nutrient not found");
                }
            } while (nutrients.moveToNext());
        }else{
            Log.w("NA", "No Nutrition found for this foodId" + foodId);
            return null;
        }
        return ret;
    }

    public Food getFoodById(String foodId, String loggedAtIso) {
        Cursor c = db.rawQuery("SELECT description FROM food where fdc_id = \"" + foodId + "\"", null);

        if(foodCache.containsKey(foodId)){
            return foodCache.get(foodId);
        }else {

            LocalDate loggedAt = null;
            if (loggedAtIso != null) {
                loggedAt = LocalDate.parse(loggedAtIso, DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00"));
            }

            if (c.moveToFirst()) {
                String foodName = c.getString(0);
                Food f = new Food(foodName, foodId, this, loggedAt);
                foodCache.put(foodId, f);
                return f;
            }
            throw new RuntimeException("The food didn't exists, that's unfortunate.");
        }
    }

    public ArrayList<Food> getFoodsByPartialName(String substring) {
        /* This function searches for a given substring */

        Cursor c = db.rawQuery("SELECT * FROM food where description LIKE \"%" + substring + "%\" LIMIT 20", null);
        ArrayList<Food> foods = new ArrayList<Food>();

        if(substring.isEmpty()){
            return foods;
        }

        if (c.moveToFirst()) {
            do {
                String foodId = c.getString(0);
                String fullName = c.getString(2);
                Food f = new Food(fullName, 0, 0, null, null);
                f.id = foodId;
                foods.add(f);
            } while (c.moveToNext());
        }

        return foods;
    }

    private class SuggestionHelper implements Comparable<SuggestionHelper>{
        public int counter;
        public Food food;
        public SuggestionHelper(Food food){
            this.counter = 1;
            this.food = food;
        }

        @Override
        public int compareTo(SuggestionHelper s) {
            return Integer.compare(this.counter, s.counter);
        }
    }

    public ArrayList<Food> getSuggestionsForCombination(ArrayList<Food> selectedSoFar) {
        /* This function returns suggestions for create_foods to log based on previously selected combinations */

        HashMap<Integer, ArrayList<Food>> prevSelected = getLoggedFoodsByDate(LocalDate.MIN, LocalDate.MAX);
        ArrayList<SuggestionHelper> suggestionCounter= new ArrayList<SuggestionHelper>();
        ArrayList<Food> suggestions = new ArrayList<Food>();

        /* try all group keys */
        for(Integer key : prevSelected.keySet()){
            /* check if any of the create_foods in the group is already selected */
            for(Food f : prevSelected.get(key)){
                if(selectedSoFar.contains(f)) {
                    /* if any is selected, add all but the already selected food */
                    for(Food toBeAddedFood: prevSelected.get(key)) {
                        if(selectedSoFar.contains(toBeAddedFood)){
                            continue;
                        }
                        SuggestionHelper current = new SuggestionHelper(toBeAddedFood);
                        if (suggestionCounter.contains(current)) {
                            suggestionCounter.get(suggestionCounter.indexOf(current)).counter += 1;
                        } else {
                            suggestionCounter.add(current);
                        }
                    }
                    break;
                }
            }
        }

        int limit = 10;
        if(limit >= suggestionCounter.size()){
            limit = suggestionCounter.size();
        }

        final ArrayList<Food> results = new ArrayList<Food>();
        Collections.sort(suggestionCounter);
        for(int i = 0; i < limit; i++){
            results.add(suggestionCounter.get(i).food);
        }
        return results;
    }

    /* ################ NEW FOODS ################## */
    public void createNewFood(String name) {
        /* This function adds a new food to the database */
        throw new RuntimeException("Creating new Foods not implemented");
    }

    /* ########### Calculated from config ########### */
    /* ########## SAVE CONFIG ############*/
    public void setPersonWeight(int weightInKg) throws IllegalArgumentException {
        if (weightInKg < 40 || weightInKg > 600) {
            throw new IllegalArgumentException("Weight must be between 40 and 600");
        }
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, srcActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("weight", weightInKg);
        editor.commit();
    }

    public void setPersonAge(int age) throws IllegalArgumentException {
        if (age < 18 || age > 150) {
            throw new IllegalArgumentException("Age must be between 18 and 150");
        }
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, srcActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("age", age);
        editor.commit();
    }

    public void setPersonEnergyReq(int energyReq) throws IllegalArgumentException {
        if (energyReq < 1000) {
            throw new IllegalArgumentException("Energy target must be above 1000kcal");
        }
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, srcActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("energyReq", energyReq);
        editor.commit();
    }

    public void setPersonHeight(int sizeInCm) throws IllegalArgumentException {
        if (sizeInCm < 0 || sizeInCm > 300) {
            throw new IllegalArgumentException("Height must be between 0 and 300 cm");
        }
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, srcActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("height", sizeInCm);
        editor.commit();
    }

    public void setPersonGender(String gender) throws IllegalArgumentException {
        if (!gender.equals("male") && !gender.equals("female")) {
            throw new IllegalArgumentException("Gender must be 'male' or 'female'.");
        }
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, srcActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("gender", gender);
        editor.commit();
    }



    public int getPersonWeight() {
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, srcActivity.MODE_PRIVATE);
        int weight = pref.getInt("weight", -1);
        return weight;
    }

    public int getPersonHeight() {
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, srcActivity.MODE_PRIVATE);
        int height = pref.getInt("height", -1);
        return height;
    }

    public int getPersonAge() {
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, srcActivity.MODE_PRIVATE);
        int age = pref.getInt("age", -1);
        return age;
    }

    public int getPersonEnergyReq() {
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, srcActivity.MODE_PRIVATE);
        int energyReq = pref.getInt("energyReq", -1);
        if (energyReq == -1) {
            energyReq = 2000; //TODO calc from other values
        }
        return energyReq;
    }

    public String getPersonGender() {
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, srcActivity.MODE_PRIVATE);
        String gender = pref.getString("gender", "none");
        return gender;
    }

    public void close() {
        db.close();
    }

}