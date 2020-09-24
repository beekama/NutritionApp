package com.example.nutritionapp;

import android.app.Application;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionElement;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    @Test
    public void checkCreateDbObject() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
            Database db = new Database(activity);
            assertNotNull(db);
            }
        );
    }

    @Test
    public void checkLoggingFood() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                    Application app = activity.getApplication();
                    AndroidThreeTen.init(app);
                    Database db = new Database(activity);
                    ArrayList<Food> debugFoods = new ArrayList<>();
                    debugFoods.add(new Food("DEBUG", "781105"));
                    db.logExistingFoods(debugFoods, null);
                    HashMap<Integer, ArrayList<Food>> ret = db.getLoggedFoodsByDate(LocalDate.MIN, LocalDate.MAX);
                    assertNotNull(ret);
                    assert(!ret.keySet().isEmpty());
                }
        );
    }

    @Test
    public void nutrientQuery() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                    Database db = new Database(activity);
                    Application app = activity.getApplication();
                    AndroidThreeTen.init(app);
                    HashMap<String,Integer> nutrients = db.getNutrientsForFood("781105");
                    assertNotNull(nutrients);
                    assert(!nutrients.keySet().isEmpty());
            }
        );
    }

    @Test
    public void foodQuery() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                    Database db = new Database(activity);
                    Food f = db.getFoodById("781105", null);
                    assertNotNull(f);
                    assertNotNull(f.nutrition);
                }
        );
    }

    @Test
    public void addCustomAndQueryCustomFood() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                    Database db = new Database(activity);
                    String name = "FOOD_A";
                    Food f1 = CustomFoodSampleGenerator.buildCustomFoodWithNutrition(name);
                    db.createNewFood(f1);
                    ArrayList<Food> foods = db.getFoodsByPartialName("FOOD_A");
                    assertNotEquals("DB failed to retrieve custom food", 0, foods.size());
                    assertTrue("DB responded with more than one food.", foods.size() > 1);
                    assertEquals("Name of retrieve food didn't match", foods.get(0).name, name);
                }
        );
    }

    @Test
    public void checkAddAndDeleteCustomFood() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                    Database db = new Database(activity);
                    String name = "FOOD_A";
                    Food f1 = CustomFoodSampleGenerator.buildCustomFoodWithNutrition(name);
                    db.createNewFood(f1);
                    db.deleteCustomFood(f1.name);
                    ArrayList<Food> foods = db.getFoodsByPartialName("FOOD_A");
                    assertEquals("Custom Food was apparently not delete from DB", 0, foods.size());
                }
        );
    }

    @Test
    public void checkCustomFoodExistsQuery() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                    Database db = new Database(activity);
                    String name = "FOOD_A";
                    Food f1 = CustomFoodSampleGenerator.buildCustomFoodWithNutrition(name);
                    db.createNewFood(f1);
                    assertTrue("checkCustomFood exists returns false when it should return true",  db.checkCustomFoodExists(f1.name));
                    db.deleteCustomFood(f1.name);
                    assertFalse("checkCustomFood exists returns true when it should return false",  db.checkCustomFoodExists(f1.name));
                }
        );
    }

    @Test
    public void addAndEditCustomFood() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                Database db = new Database(activity);
                String name = "FOOD_A";
                Food f1 = CustomFoodSampleGenerator.buildCustomFoodWithNutrition(name);
                db.createNewFood(f1);
                f1.nutrition.getElements().replace(NutritionElement.CALCIUM, 7);
                db.createNewFood(f1);
                ArrayList<Food> foods = db.getFoodsByPartialName("FOOD_");
                assertEquals("DB failed to retrieve custom food", 0, foods.size());
                assertTrue("DB responded with more than one food.", foods.size() > 1);
                assertEquals("Name of retrieve food didn't match", foods.get(0).name, name);
            }
        );
    }

    public void addEditDeleteCustomFood() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                    // TODO
                }
        );
    }

}
