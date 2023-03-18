package com.example.nutritionapp.ui;
import androidx.lifecycle.ViewModel;
import com.example.nutritionapp.foodJournal.addFoodsLists.SelectedFoodItem;
import com.example.nutritionapp.other.Database;
import java.util.ArrayList;

public class FoodGroupViewModel extends ViewModel {


    private static boolean FIRST_ADD = true;
    final ArrayList<SelectedFoodItem> selected = new ArrayList<>();

    private Database db;


    public void setDb(Database database) {
        if (db == null) {
            db = database;
        }
    }

    public Database getDb() {
        return db;
    }

    public void setFirstAdd(boolean bool) {
        FIRST_ADD = bool;
    }

    public boolean getFirstAdd() {
        return FIRST_ADD;
    }
}
