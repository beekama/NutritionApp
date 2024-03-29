package com.example.nutritionapp;

import android.app.Application;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.NutritionElement;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private static final int NO_REUSE_ID = -1;
    private final String defaultName = "FOOD_A";

    @Before
    public void purgeDB(){
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
            Database db = new Database(activity);
            Application app = activity.getApplication();
            db.purgeDatabase();
        });
    }

    @Test
    public void test01_checkCreateDbObject() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                Database db = new Database(activity);
                db.purgeDatabase();
                assertNotNull(db);
            }
        );
    }

    @Test
    public void test02_checkLoggingFood() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                    Database db = new Database(activity);
                    db.logExistingFoods(JournalFoodSampleGenerator.generateSampleFoodGroup(db), null, false);
                    HashMap<Integer, ArrayList<Food>> ret = db.getLoggedFoodsByDate(LocalDate.MIN, LocalDate.MAX, null);
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
                    db.createNewFood(f1, NO_REUSE_ID);
                    ArrayList<Food> foods = db.getFoodsByExactName("FOOD_A");

                    assertNotEquals("DB failed to retrieve custom food", 0, foods.size());
                    assertEquals("DB responded with more than one food.", 1, foods.size());
                    assertEquals("Name of retrieve food didn't match", foods.get(0).name, defaultName);

                    Food ret = new Food(foods.get(0).name, foods.get(0).id, db, null);

                    assertEquals("Energy of retrieved custom Food not equal", f1.energy, ret.energy);
                    assertEquals("Fiber of retrieved custom Food not equal", f1.fiber, ret.fiber);
                    assertEquals("Calcium (MG) of retrieved custom Food not equal", f1.nutrition.getElements().get(NutritionElement.CALCIUM),
                            ret.nutrition.getElements().get(NutritionElement.CALCIUM));
                    assertEquals("VITAMIN_D (MG_ATE) of retrieved custom Food not equal", f1.nutrition.getElements().get(NutritionElement.VITAMIN_D),
                    ret.nutrition.getElements().get(NutritionElement.VITAMIN_D));
                    assertEquals("VITAMIN_C (UG) of retrieved custom Food not equal", f1.nutrition.getElements().get(NutritionElement.VITAMIN_C),
                            ret.nutrition.getElements().get(NutritionElement.VITAMIN_C));
                }
        );
    }

    @Test
    public void test06_checkAddAndDeleteCustomFood() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                    Database db = new Database(activity);
                    Food f1 = CustomFoodSampleGenerator.buildCustomFoodWithNutrition(defaultName);
                    f1 = db.createNewFood(f1, NO_REUSE_ID);
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
                    f1 = db.createNewFood(f1, NO_REUSE_ID);
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
                f1WithId = db.createNewFood(f1, NO_REUSE_ID);
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
                f1 = db.createNewFood(f1, NO_REUSE_ID);
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
                    f1 = db.createNewFood(f1, NO_REUSE_ID);
                    db.markFoodAsDeactivated(f1);
                    ArrayList<Food> foods = db.getFoodsByPartialName("FOOD_A");
                    assertFalse("Food should be deactivated but was still found by db.getFoodsByPartialName", foods.contains(f1));
                    foods = db.getAllCustomFoods();
                    assertFalse("Food should be deactivated but was still found by db.getAllCustomFoods", foods.contains(f1));
                    assertNotNull("Food missing after deactivation??", db.getFoodById(f1.id));
                }
        );
    }

    @Test
    public void test11_checkFoodActivation() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                    Database db = new Database(activity);
                    Food f1 = CustomFoodSampleGenerator.buildCustomFoodWithNutrition(defaultName);
                    db.createNewFood(f1, NO_REUSE_ID);
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
                    f1.name = "FOOD_A_ABCDFGH";
                    f1 = db.createNewFood(f1, NO_REUSE_ID);
                    f1.setAssociatedAmount(100);
                    ArrayList<Food> foods = new ArrayList<>();
                    foods.add(f1);
                    db.logExistingFoods(foods, LocalDateTime.now(), false);
                    db.deleteCustomFood(f1);
                    foods = db.getFoodsByPartialName("FOOD_A_ABCDFGH");
                    assertEquals("Food should be deactivated by deletion but was still found", 0, foods.size());
                    Food f =  db.getFoodById(f1.id);
                    assertNotNull("Custom food was deleted while it was in journal", f);
                }
        );
    }
}
