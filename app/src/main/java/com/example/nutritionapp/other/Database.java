package com.example.nutritionapp.other;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.nutritionapp.R;
import com.example.nutritionapp.foodJournal.AddFoodsLists.SelectedFoodAdapter;
import com.example.nutritionapp.foodJournal.AddFoodsLists.SelectedFoodItem;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import static android.database.sqlite.SQLiteDatabase.OPEN_READWRITE;

public class Database {

    final String FILE_KEY = "DEFAULT";
    final SQLiteDatabase db;
    private static final DateTimeFormatter sqliteDatetimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00");

    /* used to track create_foods added together */
    private final Activity srcActivity;

    private HashMap<String,Food> foodCache = new HashMap<>();

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
    public synchronized void logExistingFoods(ArrayList<SelectedFoodItem> selectedSoFarItems, LocalDate d, Object hackyhack) {
        /* This functions add a list of create_foods to the journal at a given date */
        ArrayList<Food> selectedSoFar = new ArrayList<>();
        for (SelectedFoodItem item : selectedSoFarItems) {
            Food f = item.food;
            f.setAssociatedAmount(item.amount);
            selectedSoFar.add(item.food);
        }
        logExistingFoods(selectedSoFar, d);
    }
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
            values.put("loggedAt", d.format(sqliteDatetimeFormat));
            values.put("amountInGram", f.getAssociatedAmount());
            db.insert("foodlog", null, values);
        }
    }

    public void deleteLoggedFood(ArrayList<Food> foods, LocalDate d){
        for(Food f:foods){
            deleteLoggedFood(f, d);
        }
    }

    public synchronized void deleteLoggedFood(Food f, LocalDate d) {
        String whereClause = String.format("food_id = \"%s\" AND loggedAt = \"%s\"", f.id, d.format(sqliteDatetimeFormat));
        db.delete("foodlog", whereClause, null);
    }

    public HashMap<Integer, ArrayList<Food>> getLoggedFoodsByDate(LocalDate start, LocalDate end) {
        /* Return create_foods logged by dates */

        HashMap<Integer, ArrayList<Food>> ret = new HashMap<>();

        String startISO = start.format(sqliteDatetimeFormat);
        String endISO = end.format(sqliteDatetimeFormat);

        String table = "foodlog";
        String[] columns = {"food_id", "group_id", "loggedAt", "amountInGram"};
        String whereStm = String.format("date(loggedAt) between date(\"%s\") and date(\"%s\")" , endISO, startISO);
        if(start.equals(LocalDate.MIN) || end.equals(LocalDate.MAX)){
            whereStm = "date(loggedAt)";
        }
        Cursor c = db.query(table, columns, whereStm, null, null, null, null);

        if (c.moveToFirst()) {
            do {
                String foodId = c.getString(0);
                int groupID = c.getInt(1);
                String loggedAtISO = c.getString(2);
                int amount = c.getInt(3);

                if (ret.containsKey(groupID)) {
                    ArrayList<Food> group = (ArrayList<Food>) ret.get(groupID);
                    Food f = getFoodById(foodId, loggedAtISO);
                    f.setAssociatedAmount(amount);
                    group.add(f);
                }else{
                    ArrayList<Food> group = new ArrayList<>();
                    Food f = getFoodById(foodId, loggedAtISO);
                    f.setAssociatedAmount(amount);
                    group.add(f);
                    ret.put(groupID, group);
                }

            } while (c.moveToNext());
        }
        c.close();
        return ret;
    }

    public HashMap<String,Integer> getNutrientsForFood(String foodId){
        HashMap<String,Integer> ret = new HashMap<>();

        String table = "food_nutrient00";
        String[] columns = {"nutrient_id", "amount"};
        String whereStm = String.format("fdc_id = \"%s\"", foodId);
        Cursor nutrients = db.query(table, columns, whereStm, null, null, null, null);
        Cursor nutrientConversion;

        if (nutrients.moveToFirst()) {
            do {

                /* convert raw amount into native value */
                String nutrientID = nutrients.getString(0);
                int rawAmount = nutrients.getInt(1);

                String tableNut = "nutrient";
                String[] columnsNut = {"unit_name"};
                String whereStmNut = String.format("id = \"%s\"", nutrientID);
                nutrientConversion = db.query(tableNut, columnsNut, whereStmNut, null, null, null, null);

                if(nutrientConversion.moveToFirst()){
                    String unitName = nutrientConversion.getString(0);
                    int normalizedAmount = Conversions.normalize(unitName, rawAmount);
                    ret.put(nutrientID, normalizedAmount);
                }else{
                    nutrients.close();
                    nutrientConversion.close();
                    throw new RuntimeException("Nutrient not found?!");
                }
            } while (nutrients.moveToNext());
        }else{
            Log.w("NA", "No Nutrition found for this foodId" + foodId);
            nutrients.close();
            return null;
        }
        nutrients.close();
        nutrientConversion.close();
        return ret;
    }

    public Food getFoodById(String foodId, String loggedAtIso) {

        String table = "food";
        String[] columns = {"description"};
        String whereStm = String.format("fdc_id = \"%s\"", foodId);
        Cursor c = db.query(table, columns, whereStm, null, null, null, null);

        if(foodCache.containsKey(foodId)){
            return foodCache.get(foodId);
        }else {

            LocalDate loggedAt = null;
            if (loggedAtIso != null) {
                loggedAt = LocalDate.parse(loggedAtIso, sqliteDatetimeFormat);
            }

            if (c.moveToFirst()) {
                String foodName = c.getString(0);
                Food f = new Food(foodName, foodId, this, loggedAt);
                foodCache.put(foodId, f);
                c.close();
                return f;
            }
            c.close();
            throw new RuntimeException("The food didn't exists, that's unfortunate.");
        }
    }

    public ArrayList<Food> getFoodsByPartialName(String substring) {
        /* This function searches for a given substring */

        String table = "food";
        String[] columns = {"fdc_id", "description"};
        String whereStm = String.format("description LIKE \"%%%s%%\"", substring);
        String orderBy = String.format("description = \"%s\" DESC, description LIKE \"%s%%\" DESC", substring, substring);
        Cursor c = db.query(table, columns, whereStm, null, null, null, orderBy, null);

        ArrayList<Food> foods = new ArrayList<>();
        if(substring.isEmpty()){
            c.close();
            return foods;
        }

        if (c.moveToFirst()) {
            do {
                String foodId = c.getString(0);
                String description = c.getString(1);
                Food f = new Food(description, foodId);
                foods.add(f);
            } while (c.moveToNext());
        }
        c.close();
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

    public ArrayList<Food> getSuggestionsForCombination(ArrayList<SelectedFoodItem> selectedSoFarItems) {
        /* This function returns suggestions for create_foods to log based on previously selected combinations */

        ArrayList<Food> selectedSoFar = new ArrayList<>();
        for(SelectedFoodItem item : selectedSoFarItems){
            selectedSoFar.add(item.food);
        }

        HashMap<Integer, ArrayList<Food>> prevSelected = getLoggedFoodsByDate(LocalDate.MIN, LocalDate.MAX);
        ArrayList<SuggestionHelper> suggestionCounter= new ArrayList<>();
        ArrayList<Food> suggestions = new ArrayList<>();

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

        final ArrayList<Food> results = new ArrayList<>();
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