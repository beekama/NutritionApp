package com.example.nutritionapp.other;

import java.util.HashMap;

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
