package com.example.nutritionapp;

import android.app.Application;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalDate;

import java.lang.reflect.Array;
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
                    debugFoods.add(Food.getEmptyFood(null));
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
                    HashMap<String,Integer> nutrients = db.getNutrientsForFood(Food.getEmptyFood(LocalDate.now()).id);
                    assertNotNull(nutrients);
                    assert(!nutrients.keySet().isEmpty());
            }
        );
    }

    @Test
    public void foodQuery() {
        ActivityScenario.launch(MainActivity.class).onActivity( activity -> {
                    Database db = new Database(activity);
                    Food f = db.getFoodById(Food.getEmptyFood(LocalDate.now()).id, null);
                    assertNotNull(f);
                    assertNotNull(f.nutrition);
                }
        );
    }
}
