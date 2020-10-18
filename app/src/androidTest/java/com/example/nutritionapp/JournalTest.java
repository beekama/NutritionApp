package com.example.nutritionapp;

import android.app.Application;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.nutritionapp.foodJournal.AddFoodsLists.SelectedFoodItem;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.time.LocalDateTime;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class JournalTest {

    @Before
    public void purgeDB(){
        ActivityScenario.launch(MainActivity.class).onActivity(activity -> {
            Database db = new Database(activity);
            db.purgeDatabase();
            db.close();
        });
    }

    @Test
    public void journal_01_testAddFoodSameDay(){
        ActivityScenario.launch(MainActivity.class).onActivity(activity -> {
            Database db = new Database(activity);
            ArrayList<Food> foods = JournalFoodSampleGenerator.generateSampleFoodGroup(db);
            db.logExistingFoods(foods, LocalDateTime.now());
            db.close();
        });
    }

    @Test
    public void journal_02_testAddFoodPastDay(){
        ActivityScenario.launch(MainActivity.class).onActivity(activity -> {
            Database db = new Database(activity);
            ArrayList<Food> foods = JournalFoodSampleGenerator.generateSampleFoodGroup(db);

            LocalDateTime logDate = LocalDateTime.of(2020, 9, 10, 20, 10);
            LocalDateTime prev = LocalDateTime.from(logDate);
            LocalDateTime post = LocalDateTime.from(logDate);
            prev.minus(1, ChronoUnit.DAYS);
            post.plus(1, ChronoUnit.DAYS);

            db.logExistingFoods(foods, logDate);
            HashMap<Integer, ArrayList<Food>> foodGroups = db.getLoggedFoodsByDate(prev, post);

            assertEquals("Exactly one food group should be found", 1, foodGroups.keySet().size());
            boolean runOnce = true;
            for(ArrayList<Food> af : foodGroups.values()){
                assertTrue("more than one food value group",  runOnce);
                assertEquals("food group doesn't match input", af.size(), foods.size());
                runOnce = false;
            }
            db.close();
        });
    }

    @Test
    public void journal_03_testAddFoodOnMultipleDays(){
        ActivityScenario.launch(MainActivity.class).onActivity(activity -> {
            Database db = new Database(activity);
            ArrayList<Food> foods = JournalFoodSampleGenerator.generateSampleFoodGroup(db);

            LocalDateTime day1 = LocalDateTime.of(2020, 9, 1, 20, 10);
            LocalDateTime day2 = LocalDateTime.of(2020, 9, 2, 20, 10);
            LocalDateTime day3 = LocalDateTime.of(2020, 9, 3, 20, 10);

            LocalDateTime prev = LocalDateTime.from(day1);
            LocalDateTime post = LocalDateTime.from(day3);
            prev.minus(1, ChronoUnit.DAYS);
            post.plus(1, ChronoUnit.DAYS);

            db.logExistingFoods(foods, day1);
            db.logExistingFoods(foods, day2);
            db.logExistingFoods(foods, day3);

            HashMap<Integer, ArrayList<Food>> foodGroups = db.getLoggedFoodsByDate(prev, post);
            if(foodGroups.keySet().size() != 3){
                Log.wtf("WTF", db.debugDumpFoodlog());
            }

            assertEquals( "Amount of expect food groups doesn't match", 3, foodGroups.keySet().size());
            assertEquals("Amount of expect food groups doesn't match", 3, foodGroups.values().size());
            for(ArrayList<Food> af : foodGroups.values()){
                assertEquals("food group doesn't match input", af.size(), foods.size());
            }
            db.close();
        });
    }

    @Test
    public void journal_04_testAddAndRemoveFoodSameDay(){

    }

    @Test
    public void journal_05_testAddAndRemoveFoodPastDay(){
        ActivityScenario.launch(MainActivity.class).onActivity(activity -> {
            Database db = new Database(activity);
            ArrayList<Food> foods = JournalFoodSampleGenerator.generateSampleFoodGroup(db);

            LocalDateTime day1 = LocalDateTime.of(2020, 9, 1, 20, 10);
            LocalDateTime day2 = LocalDateTime.of(2020, 9, 2, 20, 10);
            LocalDateTime day3 = LocalDateTime.of(2020, 9, 3, 20, 10);

            LocalDateTime prev = LocalDateTime.from(day1);
            LocalDateTime post = LocalDateTime.from(day3);
            prev.minus(1, ChronoUnit.DAYS);
            post.plus(1, ChronoUnit.DAYS);

            db.logExistingFoods(foods, day1);
            db.logExistingFoods(foods, day2);
            db.logExistingFoods(foods, day3);

            HashMap<Integer, ArrayList<Food>> foodGroups = db.getLoggedFoodsByDate(prev, post);

            for(Integer key: foodGroups.keySet()){
                ArrayList<Food> af = foodGroups.get(key);
                db.deleteLoggedFood(af, af.get(0).loggedAt);
                assertEquals("food group doesn't match input", 0, db.getLoggedFoodByGroupId(key).size());
            }
            assertEquals("Foods weren't completely deleted from log", 0,  db.getLoggedFoodsByDate(prev, post).keySet().size());
            db.close();
        });
    }

    @Test
    public void journal_06_testFoodSearch(){
        ActivityScenario.launch(MainActivity.class).onActivity(activity -> {
            Database db = new Database(activity);
            String pattern = "raw";
            ArrayList<Food> foods = db.getFoodsByPartialName(pattern);
            assertNotEquals("food search produced empty result", 0, foods.size());
            for(Food f : foods){
                String assertMsg = String.format("Returned food didn't contain pattern (name: %s)", f.name);
                assertTrue(assertMsg, f.name.toLowerCase().contains(pattern.toLowerCase()));
            }
            db.close();
        });
    }

    @Test
    public void journal_07_testFoodSuggestionSmall(){
        ActivityScenario.launch(MainActivity.class).onActivity(activity -> {
            Database db = new Database(activity);

            ArrayList<Food> foods = JournalFoodSampleGenerator.generateSampleFoodGroup(db);
            LocalDateTime logDate = LocalDateTime.of(2020, 9, 10, 20, 10);
            db.logExistingFoods(foods, logDate);

            ArrayList<SelectedFoodItem> selected = new ArrayList<>();
            selected.add(new SelectedFoodItem(foods.get(0), 100));
            ArrayList<Food> suggested = db.getSuggestionsForCombination(selected);

            assertNotEquals("food search produced empty result", 0, suggested.size());
            assertTrue("Food 1 missing in suggestion list", suggested.contains(foods.get(1)));
            assertTrue("Food 2 missing in suggestion list", suggested.contains(foods.get(2)));

            db.close();
        });
    }
}
