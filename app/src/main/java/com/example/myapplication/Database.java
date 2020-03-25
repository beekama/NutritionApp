package com.example.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.prefs.Preferences;

import static android.database.sqlite.SQLiteDatabase.OPEN_READWRITE;

public class Database {

    final SQLiteDatabase db;

    /* used to track foods added together */
    private volatile static int groupID = 0;
    private static Activity srcActivity;

    public Database(Activity srcActivity) {
        String path = srcActivity.getFilesDir().getParent() + "/food.db";
        File file = new File(path);
        if (!file.exists()) {
            InputStream in = srcActivity.getResources().openRawResource(R.raw.food);

            try {
                OutputStream out = new FileOutputStream(path);
                int bytes = IOUtils.copy(in, out);
                IOUtils.closeQuietly(out);
            } catch (FileNotFoundException e) {
                Log.wtf("WTF", "OUTPUT file not found");
            } catch (IOException e) {
                Log.wtf("yeah whatever", "fuck");
            }
        }

        db = SQLiteDatabase.openDatabase(path, null, OPEN_READWRITE);
        this.srcActivity = srcActivity;
    }

    /* ################ FOOD LOGGING ############## */
    public synchronized void logExistingFoods(Food[] foods, Timestamp d) {
        /* This functions add a list of foods to the journal at a given date */

        groupID++;
        for (Food f : foods) {
            ContentValues values = new ContentValues();
            values.put("food_id", f.id);
            values.put("date", d.toString());
            values.put("group_id", groupID);
            db.insert("foodlog", null, values);
        }
    }

    public HashMap<Integer, ArrayList<Food>> getLoggedFoodsByDate(Timestamp start, Timestamp end) {
        /* Return foods logged by dates */

        HashMap ret = new HashMap<Integer, ArrayList<Food>>();

        Cursor c = db.rawQuery("SELECT * FROM foodlog where date >= \"" + start + "\" and date <=  \"" + end + "\"", null);
        if (c.moveToFirst()) {
            do {

                groupID = c.getInt(2);
                String foodId = c.getString(0);

                if (ret.containsKey(groupID)) {
                    ArrayList<Food> group = (ArrayList<Food>) ret.get(groupID);
                    group.add(getFoodById(foodId));
                }

            } while (c.moveToNext());
        }
        c.close();
        return ret;
    }

    public Food getFoodById(String foodId) {
        Cursor c = db.rawQuery("SELECT * FROM food where fdc_id = \"" + foodId + "\"", null);
        if (c.moveToFirst()) {
            return new Food(c.getString(2), 100, 100, new Minerals(), new Vitamins());
        }
        throw new RuntimeException("The food didn't exists, that's unfortunate.");
    }

    public ArrayList<Food> getFoodByPartialName(String substring) {
        /* This function searches for a given substring */

        Cursor c = db.rawQuery("SELECT * FROM food where description = \"%" + substring + "%\"", null);
        ArrayList<Food> foods = new ArrayList<Food>();

        if (c.moveToFirst()) {
            do {
                String foodId = c.getString(0);
                String fullName = c.getString(2);
                foods.add(new Food(fullName, 0, 0, null, null));
            } while (c.moveToNext());
        }

        return foods;
    }

    public Food[] getSuggestionsForCombination(Food[] selectedSoFar) {
        /* This function returns suggestions for foods to log based on previously selected combinations */
        throw new RuntimeException("Suggestions are not yet implemented");
    }

    /* ################ NEW FOODS ################## */
    public void createNewFood(String name) {
        /* This function adds a new food to the database */
        throw new RuntimeException("Creating new Foods not implemented");
    }

    /* ########### Calculated from config ########### */
    public Minerals getMineralRecommendation() {
        /* Returns the correct mineral recommendation (USDA Values)*/

        Minerals allowance = new Minerals();
        allowance.calcium = 1200;
        allowance.iron = 18;
        allowance.magnesium = 420;
        allowance.potassium = 4700;
        allowance.zinc = 11;
        return allowance;
    }

    public Vitamins getVitaminRecommendation() {
        /* Returns the correct vitamin recommendation (USDA Values)*/

        Vitamins allowance = new Vitamins();
        allowance.A = 900;
        allowance.B12 = 3;
        allowance.C = 90000;
        allowance.D = 15;
        allowance.E = 15000;
        allowance.K = 110;
        return allowance;
    }

    /* ########## SAVE CONFIG ############*/
    public void setPersonWeight(int weightInKg) throws IllegalArgumentException {
        if (weightInKg < 40 || weightInKg > 600) {
            throw new IllegalArgumentException("Weight must be between 40 and 600");
        }
        SharedPreferences pref = srcActivity.getPreferences(srcActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("weight", weightInKg);
        editor.commit();
    }

    public void setPersonAge(int age) throws IllegalArgumentException {
        if (age < 18 || age > 150) {
            throw new IllegalArgumentException("Age must be between 18 and 150");
        }
        SharedPreferences pref = srcActivity.getPreferences(srcActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("age", age);
        editor.commit();
    }

    public void setPersonEnergyReq(int energyReq) throws IllegalArgumentException {
        if (energyReq < 1000) {
            throw new IllegalArgumentException("Energy target must be above 1000kcal");
        }
        SharedPreferences pref = srcActivity.getPreferences(srcActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("energyReq", energyReq);
        editor.commit();
    }

    public void setPersonHeight(int sizeInCm) throws IllegalArgumentException {
        if (sizeInCm < 0 || sizeInCm > 300) {
            throw new IllegalArgumentException("Height must be between 0 and 300 cm");
        }
        SharedPreferences pref = srcActivity.getPreferences(srcActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("height", sizeInCm);
        editor.commit();
    }

    public void setPersonGender(String gender) throws IllegalArgumentException {
        if (!gender.equals("male") && !gender.equals("female")) {
            throw new IllegalArgumentException("Gender must be 'male' or 'female'.");
        }
        SharedPreferences pref = srcActivity.getPreferences(srcActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("gender", gender);
        editor.commit();
    }



    public int getPersonWeight() {
        SharedPreferences pref = srcActivity.getPreferences(srcActivity.MODE_PRIVATE);
        int weight = pref.getInt("weight", -1);
        return weight;
    }

    public int getPersonHeight() {
        SharedPreferences pref = srcActivity.getPreferences(srcActivity.MODE_PRIVATE);
        int height = pref.getInt("height", -1);
        return height;
    }

    public int getPersonAge() {
        SharedPreferences pref = srcActivity.getPreferences(srcActivity.MODE_PRIVATE);
        int age = pref.getInt("age", -1);
        return age;
    }

    public int getPersonEnergyReq() {
        SharedPreferences pref = srcActivity.getPreferences(srcActivity.MODE_PRIVATE);
        int energyReq = pref.getInt("energyReq", -1);
        if (energyReq == -1) {
            energyReq = 2000; //TODO calc from other values
        }
        return energyReq;
    }

    public String getPersonGender() {
        SharedPreferences pref = srcActivity.getPreferences(srcActivity.MODE_PRIVATE);
        String gender = pref.getString("gender", "none");
        return gender;
    }
}