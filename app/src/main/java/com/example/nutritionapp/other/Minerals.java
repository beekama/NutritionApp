package com.example.nutritionapp.other;

import java.util.HashMap;

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
