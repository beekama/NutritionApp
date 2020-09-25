package com.example.nutritionapp;

import android.app.Application;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.NutritionElement;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    private final String defaultName = "FOOD_A";

    @Before
    public void purgeDB(){
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
            Database db = new Database(activity);
            Application app = activity.getApplication();
            AndroidThreeTen.init(app);
            db.purgeDatabase();
            db.close();
        });
    }

    @Test
    public void test01_checkCreateDbObject() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                Database db = new Database(activity);
                db.purgeDatabase();
                assertNotNull(db);
                db.close();
            }
        );
    }

    @Test
    public void test02_checkLoggingFood() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                    Database db = new Database(activity);
                    db.logExistingFoods(JournalFoodSampleGenerator.generateSampleFoodGroup(db), null);
                    HashMap<Integer, ArrayList<Food>> ret = db.getLoggedFoodsByDate(LocalDate.MIN, LocalDate.MAX);
                    assertNotNull(ret);
                    assert(!ret.keySet().isEmpty());
                }
        );
    }

    @Test
    public void test03_nutrientQuery() {
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
    public void test04_foodQuery() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                    Database db = new Database(activity);
                    Food f = db.getFoodById("781105", null);
                    assertNotNull(f);
                    assertNotNull(f.nutrition);
                }
        );
    }

    @Test
    public void test05_addCustomAndQueryCustomFood() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                    Database db = new Database(activity);
                    Food f1 = CustomFoodSampleGenerator.buildCustomFoodWithNutrition(defaultName);
                    db.createNewFood(f1);
                    ArrayList<Food> foods = db.getFoodsByExactName("FOOD_A");
                    assertNotEquals("DB failed to retrieve custom food", 0, foods.size());
                    assertEquals("DB responded with more than one food.", 1, foods.size());
                    assertEquals("Name of retrieve food didn't match", foods.get(0).name, defaultName);
                }
        );
    }

    @Test
    public void test06_checkAddAndDeleteCustomFood() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                    Database db = new Database(activity);
                    Food f1 = CustomFoodSampleGenerator.buildCustomFoodWithNutrition(defaultName);
                    f1 = db.createNewFood(f1);
                    boolean deleted = db.deleteCustomFood(f1);
                    assertTrue("deleteCustomFood returned false", deleted);
                    Food fTest = db.getFoodById(f1.id);
                    assertNull("Custom Food was apparently not deleted from DB", fTest);
                }
        );
    }

    @Test
    public void test07_checkCustomFoodExistsQuery() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                    Database db = new Database(activity);
                    Food f1 = CustomFoodSampleGenerator.buildCustomFoodWithNutrition(defaultName);
                    f1 = db.createNewFood(f1);
                    assertTrue("checkCustomNameFoodExists returns false when it should return true",  db.checkCustomNameFoodExists(f1.name));
                    assertTrue("delete custom food return false", db.deleteCustomFood(f1));
                    assertFalse("checkCustomNameFoodExists returns true when it should return false",  db.checkCustomNameFoodExists(f1.name));
                }
        );
    }

    @Test
    public void test08_addAndEditCustomFood() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                Database db = new Database(activity);
                Food f1 = CustomFoodSampleGenerator.buildCustomFoodWithNutrition(defaultName);
                Food f1WithId;
                f1WithId = db.createNewFood(f1);
                f1.nutrition.getElements().replace(NutritionElement.CALCIUM, 7);
                db.changeCustomFood(f1WithId, f1);
                ArrayList<Food> foods = db.getFoodsByExactName("FOOD_A");
                assertEquals("DB failed to retrieve custom food", 1, foods.size());
                assertFalse("DB responded with more than one food.", foods.size() > 1);
                assertEquals("Name of retrieve food didn't match", foods.get(0).name, defaultName);
            }
        );
    }

    @Test
    public void test09_addEditDeleteCustomFood() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                Database db = new Database(activity);
                Food f1 = CustomFoodSampleGenerator.buildCustomFoodWithNutrition(defaultName);
                f1 = db.createNewFood(f1);
                f1.nutrition.getElements().replace(NutritionElement.CALCIUM, 7);
                boolean changed = db.changeCustomFood(f1, f1);
                assertTrue("Database says custom food wasn't changed when it should be", changed);
                boolean deleted = db.deleteCustomFood(f1);
                assertTrue("Database says custom food wasn't deleted when it should be", deleted);
                Food fTest = db.getFoodById(f1.id);
                assertNull("Database reports food delete but is still found by it's ID",  fTest);
                assertFalse("Custom food still found by it's name after deletion",  db.checkCustomNameFoodExists(f1.name));
            }
        );
    }

    @Test
    public void test10_checkFoodDeactivation() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                    Database db = new Database(activity);
                    Food f1 = CustomFoodSampleGenerator.buildCustomFoodWithNutrition(defaultName);
                    db.createNewFood(f1);
                    db.markFoodAsDeactivated(f1);
                    ArrayList<Food> foods = db.getFoodsByExactName("FOOD_A");
                    assertEquals("Food should be deactivated but was still found", 0, foods.size());
                }
        );
    }

    @Test
    public void test11_checkFoodActivation() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                    Database db = new Database(activity);
                    Food f1 = CustomFoodSampleGenerator.buildCustomFoodWithNutrition(defaultName);
                    db.createNewFood(f1);
                    db.markFoodAsDeactivated(f1);
                    db.markFoodAsActivated(f1);
                    ArrayList<Food> foods = db.getFoodsByExactName("FOOD_A");
                    assertEquals("Food should be activated but was not found", 1, foods.size());
                }
        );
    }

    @Test
    public void test12_checkFoodDeleteWhileInJournal() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                    Database db = new Database(activity);
                    Food f1 = CustomFoodSampleGenerator.buildCustomFoodWithNutrition(defaultName);
                    db.createNewFood(f1);
                    ArrayList<Food> foods = JournalFoodSampleGenerator.generateSampleFoodGroup(db);
                    db.logExistingFoods(foods, LocalDateTime.now());
                    db.deleteCustomFood(f1);
                    assertEquals("Food should be deactivated by deletion but was still found", 0, foods.size());
                    Food f =  db.getFoodById(f1.id);
                    assertNotNull("Custom food was deleted while it was in journal", f);
                }
        );
    }
}
