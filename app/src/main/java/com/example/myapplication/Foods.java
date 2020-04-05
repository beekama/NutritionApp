package com.example.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import java.util.HashMap;

class Food {

    private static final String DB_ID_ENERGY = "1008";
    private static final String DB_ID_FIBER = "1079";

    public String name;
    public String id;
    public int energy;
    public int fiber;
    public Minerals minerals;
    public Vitamins vitamins;
    public LocalDate loggedAt;

    public Food(String name, int energy, int fiber, Minerals minerals, Vitamins vitamins, LocalDate logTime) {
        this.name = name;
        this.energy = energy;
        this.fiber = fiber;
        this.minerals = minerals;
        this.vitamins = vitamins;
        this.loggedAt = logTime;
    }

    public Food(String foodName, String foodId, Database db, LocalDate loggedAt) {
        this.name = foodName;
        this.id = foodId;
        HashMap<String,Integer> nutrients = db.getNutrientsForFood(foodId);
        this.fiber = nutrients.get(DB_ID_FIBER);
        this.energy = nutrients.get(DB_ID_ENERGY);
        this.minerals = new Minerals(nutrients);
        this.vitamins = new Vitamins(nutrients);
        this.loggedAt = loggedAt;
    }

    public static Food getEmptyFood(LocalDate logTime){
        Food f = new Food("<Placeholder>", 0, 0, new Minerals(), new Vitamins(), logTime);
        f.id = "781105";
        return f;
    }
}

class Vitamins{
    public int A;
    public int C;
    public int D;
    public int E;
    public int K;
    public int B6;
    public int B12;

    public static final String DB_ID_VITAMIN_A = "1106";
    public static final String DB_ID_VITAMIN_C = "1162";
    public static final String DB_ID_VITAMIN_D = "1114";
    public static final String DB_ID_VITAMIN_E = "1158";
    public static final String DB_ID_VITAMIN_K = "1183";
    public static final String DB_ID_VITAMIN_B12 = "1178";

    public Vitamins(HashMap<String, Integer> nutrients) {
        this.A = Utils.zeroIfNull(nutrients.get(DB_ID_VITAMIN_A));
        this.C = Utils.zeroIfNull(nutrients.get(DB_ID_VITAMIN_C));
        this.D = Utils.zeroIfNull(nutrients.get(DB_ID_VITAMIN_D));
        this.E = Utils.zeroIfNull(nutrients.get(DB_ID_VITAMIN_E));
        this.K = Utils.zeroIfNull(nutrients.get(DB_ID_VITAMIN_K));
        this.B12 = Utils.zeroIfNull(nutrients.get(DB_ID_VITAMIN_B12));
    }

    public Vitamins(){
        /* dummy constructor for daily recommendations */
    }
}

class Minerals {
    public int iron;
    public int magnesium;
    public int zinc;
    public int calcium;
    public int potassium;

    public static final String DB_ID_IRON = "1089";
    public static final String DB_ID_MAGNESIUM = "1090";
    public static final String DB_ID_ZINC = "1095";
    public static final String DB_ID_CALCIUM = "1087";
    public static final String DB_ID_POTASSIUM = "1092";

    public Minerals(HashMap<String, Integer> nutrients) {
        this.iron = Utils.zeroIfNull(nutrients.get(DB_ID_IRON));
        this.magnesium = Utils.zeroIfNull(nutrients.get(DB_ID_MAGNESIUM));
        this.zinc = Utils.zeroIfNull(nutrients.get(DB_ID_ZINC));
        this.calcium = Utils.zeroIfNull(nutrients.get(DB_ID_CALCIUM));
        this.potassium = Utils.zeroIfNull(nutrients.get(DB_ID_POTASSIUM));
    }

    public Minerals(){
        /* dummy constructor for daily recommendations */
    }
}