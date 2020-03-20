package com.example.myapplication;

import android.app.Activity;
import android.content.ContentValues;
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

import static android.database.sqlite.SQLiteDatabase.OPEN_READWRITE;

public class Database {

    final SQLiteDatabase db;

    /* used to trac  foods added together */
    private volatile static int groupID = 0;

    public Database(Activity srcActivity){
        String path = srcActivity.getFilesDir().getParent() + "/food.db";
        File file = new File(path);
        if(!file.exists()) {
            InputStream in = srcActivity.getResources().openRawResource(R.raw.food);

            try {
                OutputStream out = new FileOutputStream(path);
                int bytes=IOUtils.copy(in, out);
                IOUtils.closeQuietly(out);
            } catch (FileNotFoundException e) {
                Log.wtf("WTF", "OUTPUT file not found");
            } catch (IOException e){
                Log.wtf("yeah whatever", "fuck");
            }
        }

        db = SQLiteDatabase.openDatabase(path,  null, OPEN_READWRITE);
    }

    /* ################ FOOD LOGGING ############## */
    public synchronized void logExistingFoods(Food[] foods, Timestamp d){
        /* This functions add a list of foods to the journal at a given date */

        groupID++;
        for(Food f:foods){
            ContentValues values = new ContentValues();
            values.put("food_id", f.id);
            values.put("date", d.toString());
            values.put("group_id", groupID);
            db.insert("foodlog", null, values);
        }
    }

    public HashMap<Integer, ArrayList<Food>> getLoggedFoodsByDate(Timestamp start, Timestamp end){
        /* Return foods logged by dates */

        HashMap ret = new HashMap<Integer, ArrayList<Food>>();

        Cursor c = db.rawQuery("SELECT * FROM foodlog where date >= \"" + start + "\" and date <=  \"" + end + "\"", null);
        if (c.moveToFirst()){
            do {

                groupID = c.getInt(2);
                String foodId  = c.getString(0);

                if( ret.containsKey(groupID)){
                    ArrayList<Food> group = (ArrayList<Food>)ret.get(groupID);
                    group.add(getFoodById(foodId));
                }

            } while(c.moveToNext());
        }
        c.close();
        return ret;
    }

    public Food getFoodById(String foodId) {
        Cursor c = db.rawQuery("SELECT * FROM food where fdc_id = \"" + foodId + "\"", null);
        if (c.moveToFirst()){
            return new Food(c.getString(2), 100, 100, new Minerals(), new Vitamins());
        }
        throw new RuntimeException("The food didn't exists, that's unfortunate.");
    }

    public ArrayList<Food> getFoodByPartialName(String substring){
        /* This function searches for a given substring */
        return null;
    }

    public Food[] getSuggestionsForCombination(Food[] selectedSoFar){
        /* This function returns suggestions for foods to log based on previously selected combinations */
        return null;
    }

    /* ################ NEW FOODS ################## */
    public void createNewFood(String name){
        /* This function adds a new food to the database */
    }

    /* ########### Calculated from config ########### */
    public Minerals getMineralRecommendation(){
        /* Returns the correct mineral recommendation */
        return null;
    }

    public Vitamins getVitaminRecommendation(){
        /* Returns the correct vitamin recommendation */
        return null;
    }

    public int getEnergyRecommendation(){
        /* Return the recommended energy */
        return -1;
    }

    /* ########## SAVE CONFIG ############*/
    public void setPersonWeight(int weightInKg){

    }

    public void setPersonHeight(int sizeInCm){

    }

    public void setPersonGender(String gender){

    }

    public int getPersonWeight(){
        return -1;
    }

    public int getPersonHeight(){
        return -1;
    }

    public String getPersonGender(){
        return "";
    }
}
