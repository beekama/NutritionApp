package com.example.nutritionapp.other;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.nutritionapp.R;
import com.example.nutritionapp.foodJournal.addFoodsLists.SelectedFoodItem;

import org.apache.commons.io.IOUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import static android.database.sqlite.SQLiteDatabase.NO_LOCALIZED_COLLATORS;
import static android.database.sqlite.SQLiteDatabase.OPEN_READWRITE;

import androidx.fragment.app.Fragment;

public class Database {

    public static final String ASSIGNED_PORTION = "assigned_portion";
    private static final int DEFAULT_MIN_CUSTOM_ID = 100000000;
    private static final String DATABASE_NAME = "food.db";

    private static final String CUSTOM_FOOD_ACTIVE_TYPE = "app_custom";
    private static final String JOURNAL_TABLE = "foodlog";
    private static final String FOOD_TABLE = "food";
    private static final String CALORIE_TARGET_BY_DATE_TABLE = "calorieTargetByDate";
    private static final String FOOD_TEMPLATES = "food_templates";
    private static final String FOOD_PREFERENCES = "food_preferences";
    private static final String CURATED_FOODS = "curated_food";
    private static final String FOOD_PORTIONS = "food_portions";

    private static final String DEFAULT_NUTRIENT_DB = "food_nutrient_00";

    final int IS_TEMPLATE_MINUS_ONE = -1;

    final String FILE_KEY = "DEFAULT";
    final String WEIGHTS = "weightByDate";

    private static SQLiteDatabase db = null;
    private static final HashMap<Integer, ArrayList<Food>> foodGroupResult = new HashMap<>();
    private static final HashMap<String, HashMap<String, Integer>> foodNutritionResults = new HashMap<>();
    private static final ArrayList<Integer> fdcIdToDbNumber = new ArrayList<>();
    private static final ArrayList<String> foodNutrientTableIds = new ArrayList<>();
    private final HashMap<String, Food> foodCache = new HashMap<>();
    private final Activity srcActivity;
    private final Fragment srcFragment;
    private static final HashMap<String, String> nutritionNativeUnitMap = new HashMap<>();
    String targetPath;

    public Database(Activity srcActivity) {
        this.srcActivity = srcActivity;
        this.srcFragment = null;
        if (db == null) {
            createDatabase(true);
            db = SQLiteDatabase.openDatabase(targetPath, null, NO_LOCALIZED_COLLATORS | OPEN_READWRITE);
        }
        if (fdcIdToDbNumber.isEmpty()) {
            generateNutritionTableSelectionMap();
        }
    }

    public Database(Fragment srcFragment) {
        this.srcActivity = null;
        this.srcFragment = srcFragment;
        if (db == null) {
            createDatabase(true);
            db = SQLiteDatabase.openDatabase(targetPath, null, NO_LOCALIZED_COLLATORS | OPEN_READWRITE);
        }
        if (fdcIdToDbNumber.isEmpty()) {
            generateNutritionTableSelectionMap();
        }
    }

    @SuppressLint("ApplySharedPref")
    public void purgeDatabase() {
        db.close();
        createDatabase(true);
        db = SQLiteDatabase.openDatabase(targetPath, null, OPEN_READWRITE);
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    private void createDatabase(boolean forceOverwrite) {
        targetPath = srcActivity.getFilesDir().getParent() + "/" + DATABASE_NAME;
        File file = new File(targetPath);
        if (forceOverwrite && file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                throw new AssertionError("Database could not be deleted.");
            }
        }
        if (!file.exists()) {
            InputStream in = srcActivity.getResources().openRawResource(R.raw.food);
            try {
                OutputStream out = new FileOutputStream(targetPath);
                IOUtil.copy(in, out);
                out.close();
                in.close();
            } catch (FileNotFoundException e) {
                throw new AssertionError("Database resource 'food.db' not found.", e);
            } catch (IOException e) {
                throw new AssertionError("Failed to copy 'food.db' to internal storage.", e);
            }
        }
    }

    private void generateNutritionTableSelectionMap() {
        /* This function populates an array with information about which
        fdc_id can be found in which food_nutrient_table.
         */


        String[] columns = {"name"};
        Cursor c = db.query("sqlite_master", columns, "type = \"table\" AND name LIKE \"food_nutrient_%\"", null, null, null, null);
        while (c.moveToNext()) {
            String tmpDbName = c.getString(0);
            if (tmpDbName.contains("custom")) {
                continue;
            }
            foodNutrientTableIds.add(tmpDbName);
        }
        c.close();

        Collections.sort(foodNutrientTableIds);
        for (String table : foodNutrientTableIds) {
            String[] columnsNut = {"fdc_id"};
            Cursor nutC = db.query(table, columnsNut, null, null, null, null, "fdc_id ASC", "1");
            if (nutC.moveToNext()) {
                int minFdcId = nutC.getInt(0);
                fdcIdToDbNumber.add(minFdcId);
            } else {
                nutC.close();
                throw new AssertionError("Food Nutrition tables missing?!");
            }
            nutC.close();
        }
    }

    private String getNutrientTableForFdcId(String foodId) {

        if (Integer.parseInt(foodId) > DEFAULT_MIN_CUSTOM_ID) {
            return "food_nutrient_custom";
        }
        int count = 0;
        for (Integer el : fdcIdToDbNumber) {
            if (Integer.parseInt(foodId) < el) {
                /* the number in the fdcIdToDbNumber is the lowest id in the respective db  */
                /* so if we are below that number we must select the previous db            */
                if (count == 0) {
                    /* this means we have DB so low it isn't even in the first db   */
                    /* in this case we return the first db, if we returned a bad db */
                    /* the app would crash, which seems a bit extreme in this case  */
                    return DEFAULT_NUTRIENT_DB;
                }
                break;
            } else {
                count++;
            }
        }
        Log.w("WARNING", "ID " + foodId + " has no associated Database. (nut good)");
        return String.format(Locale.ENGLISH, "food_nutrient_%02d", count - 1);
    }

    /* ################ FOOD LOGGING ############## */
    public synchronized int logExistingFoods(ArrayList<SelectedFoodItem> selectedSoFarItems, LocalDateTime d, Object hackyHack, boolean isTemplate) {
        /* This functions add a list of create_foods to the journal at a given date */
        if (hackyHack != null) {
            Log.w("WARN", "HackyHack parameter should always be null");
        }
        ArrayList<Food> selectedSoFar = new ArrayList<>();
        for (SelectedFoodItem item : selectedSoFarItems) {
            selectedSoFar.add(item.food);
        }
        return logExistingFoods(selectedSoFar, d, isTemplate);
    }

    public synchronized int logExistingFoods(ArrayList<Food> foods, LocalDateTime d, boolean isTemplate) {
        /* This functions add a list of create_foods to the journal at a given date */

        if (d == null) {
            d = LocalDateTime.now();
        }

        Random random = new Random(); /* TODO this is bs, will eventually lead to a id conflict Issue#39 */
        int groupID = random.nextInt(1000000);
        for (Food f : foods) {
            Log.wtf("FOOD", d.format(Utils.sqliteDatetimeFormat));
            ContentValues values = new ContentValues();
            values.put("food_id", f.id);
            values.put("date", d.toString());
            values.put("group_id", groupID);
            if(isTemplate) {
                values.put("loggedAt", IS_TEMPLATE_MINUS_ONE);
                Log.wtf("Template", "Logging new Food-Template");
            }else{
                values.put("loggedAt", d.format(Utils.sqliteDatetimeFormat));
            }
            values.put("amount", f.getAssociatedAmount());
            values.put("portion_type", f.getAssociatedPortionType().toString());
            db.insert(JOURNAL_TABLE, null, values);
        }
        return groupID;
    }

    public synchronized void updateFoodGroup(ArrayList<SelectedFoodItem> updatedListWithAmounts, int groupId, LocalDateTime loggedAt, boolean isTemplate) {

        /* flush the cache */
        foodGroupResult.remove(groupId);

        String[] whereArgs = {Integer.toString(groupId)};
        db.delete(JOURNAL_TABLE, "group_id = ?", whereArgs);

        for (SelectedFoodItem fItem : updatedListWithAmounts) {
            Food f = fItem.food;
            ContentValues values = new ContentValues();
            values.put("food_id", f.id);

            if(isTemplate){
                values.put("date", IS_TEMPLATE_MINUS_ONE);
                values.put("loggedAt", Integer.toString(IS_TEMPLATE_MINUS_ONE));
            }else {
                values.put("date", loggedAt.toString());
                values.put("loggedAt", loggedAt.format(Utils.sqliteDatetimeFormat));
            }

            values.put("group_id", groupId);
            values.put("amount", f.getAssociatedAmount());
            values.put("portion_type", f.getAssociatedPortionType().toString());

            db.insert(JOURNAL_TABLE, null, values);
        }
    }

    public void deleteLoggedFood(ArrayList<Food> foods, LocalDateTime d) {
        for (Food f : foods) {
            deleteLoggedFood(f, d);
        }
    }

    public synchronized void deleteLoggedFood(Food f, LocalDateTime d) {
        String[] whereArgs = {f.id, d.format(Utils.sqliteDatetimeFormat)};
        int rowsDeleted = db.delete(JOURNAL_TABLE, "food_id = ? AND loggedAt = ?", whereArgs);
        if (rowsDeleted <= 0) {
            throw new AssertionError("Foods could not be deleted, id = " + whereArgs[0] + ", loggedAt = " + whereArgs[1]);
        }
    }

    public boolean checkFoodExists(Food f) {
        return getFoodById(f.id) != null;
    }

    public void invalidateFoodIdInCache(String foodID) {
        foodCache.remove(foodID);
        foodNutritionResults.remove(foodID);

        /* find groups that need to be invalidated */
        Food searchHelper = new Food(foodID, foodID);
        foodGroupResult.entrySet().removeIf(e -> e.getValue().contains(searchHelper));
    }

    public Food getFoodById(String foodId) {
        return getFoodById(foodId, null);
    }

    public Food getFoodById(String foodId, String loggedAtIso) {

        String[] columns = {"description"};
        String[] whereArgs = {foodId};
        Cursor c = db.query(FOOD_TABLE, columns, "fdc_id = ?", whereArgs, null, null, null);

        String altDescription = getLocalizedDescriptionForId(foodId);

        final LocalDateTime loggedAt;
        if (loggedAtIso != null && !loggedAtIso.equals(Integer.toString(IS_TEMPLATE_MINUS_ONE))) {
            loggedAt = LocalDateTime.parse(loggedAtIso, Utils.sqliteDatetimeFormat);
        } else {
            loggedAt = null;
        }

        if (foodCache.containsKey(foodId)) {
            Food f = Objects.requireNonNull(foodCache.get(foodId)).deepclone();

            /* check for bad cache states (the Issue#20 Situation) */
            boolean allNutritionValuesZero = f.nutrition.getElements().values().stream().noneMatch(i -> i != 0);
            if(f.energy == 0 && f.fiber == 0 && allNutritionValuesZero){
                Log.wtf("NutritionCache", "Found potentially broken cache entry, retrying nutrition update for: " + f.id);
                invalidateFoodIdInCache(f.id);
                f = getFoodById(f.id);
            }

            f.loggedAt = loggedAt;
            return f;
        } else {

            if (c.moveToFirst()) {
                String foodName;
                if (altDescription == null) {
                    foodName = c.getString(0);
                } else {
                    foodName = altDescription;
                }
                Food f = new Food(foodName, foodId, this, loggedAt);
                foodCache.put(foodId, f);
                c.close();
                return f;
            }
            c.close();
            return null;
        }

    }

    private String getLocalizedDescriptionForId(String foodId) {
        if (getLanguagePref() == null || getLanguagePref().equals("en")) {
            return null;
        }
        String[] columns = {"description"};
        String[] whereArgs = {foodId};
        String locTable = "localization_" + getLanguagePref();
        String altDescription = null;
        try (Cursor loc = db.query(locTable, columns, "fdc_id = ?", whereArgs, null, null, null)) {
            if (loc.moveToFirst()) {
                altDescription = loc.getString(0);
            }
        } catch (SQLException ignored) {

        }
        return altDescription;
    }

    public LinkedHashMap<Integer, ArrayList<Food>> getLoggedFoodsBeforeDate(LocalDate end, int limit) {
        LinkedHashMap<Integer, ArrayList<Food>> mainResults = getLoggedFoodsByDate(LocalDate.MIN, LocalDate.from(end), Integer.toString(limit));
        int lastGroupId = -1;
        for (int key : mainResults.keySet()) {
            lastGroupId = key;
        }
        if (lastGroupId >= 0) {
            mainResults.put(lastGroupId, getLoggedFoodByGroupId(lastGroupId));
        }
        return mainResults;
    }

    public LinkedHashMap<Integer, ArrayList<Food>> getLoggedFoodsByDate(LocalDateTime start, LocalDateTime end) {
        return getLoggedFoodsByDate(LocalDate.from(start), LocalDate.from(end), null);
    }

    public LinkedHashMap<Integer, ArrayList<Food>> getTemplateFoodGroups() {
        return getLoggedFoodsByDate(null, null, null);
    }

    public LinkedHashMap<Integer, ArrayList<Food>> getLoggedFoodsByDate(LocalDate start, LocalDate end, String limit) {
        /* Return create_foods logged by dates */

        LinkedHashMap<Integer, ArrayList<Food>> ret = new LinkedHashMap<>();

        boolean getTemplatedFoodGroups = (start == null && end == null);
        if (end == null && !getTemplatedFoodGroups) {
            end = start.plusDays(1);
        }

        String startISO;
        String endISO;
        String whereStm;
        String[] columns = {"food_id", "group_id", "loggedAt", "amount", "portion_type"};

        if(!getTemplatedFoodGroups){
            startISO = start.format(Utils.sqliteDateZeroPaddedFormat);
            endISO = end.format(Utils.sqliteDateZeroPaddedFormat);
            if (start.equals(LocalDate.MIN)) {
                startISO = "0000-01-01 00:00:00";
            }
            whereStm = String.format("date(loggedAt) between date(\"%s\") and date(\"%s\")", startISO, endISO);
            if (start.equals(LocalDate.MIN) && end.equals(LocalDate.MAX)) {
                whereStm = "date(loggedAt)";
            }
        }else{
            whereStm = "loggedAt == -1";
        }

        Cursor c = db.query(JOURNAL_TABLE, columns, whereStm, null, null, null, "date(loggedAt) DESC", limit);

        if (c.moveToFirst()) {
            do {
                String foodId = c.getString(0);
                int groupID = c.getInt(1);
                String loggedAtISO = c.getString(2);
                float amount = c.getFloat(3);
                PortionType portionType = PortionType.valueOf(c.getString(4));

                ArrayList<Food> group = ret.get(groupID);
                if (group != null) {
                    Food f = getFoodById(foodId, loggedAtISO);
                    if (f != null) {
                        f.setAssociatedAmount(amount);
                        f.setAssociatedPortionType(portionType);
                        f.setPortionTypeInGram(getPortionToGramRatio(f, portionType));
                        group.add(f);
                    }
                } else {
                    group = new ArrayList<>();
                    Food f = getFoodById(foodId, loggedAtISO);
                    if (f != null) {
                        f.setAssociatedAmount(amount);
                        f.setAssociatedPortionType(portionType);
                        f.setPortionTypeInGram(getPortionToGramRatio(f, portionType));
                        group.add(f);
                    }
                    ret.put(groupID, group);
                }

            } while (c.moveToNext());
        }
        c.close();
        return ret;
    }

    public ArrayList<Food> getFoodsFromHashMap(HashMap<Integer, ArrayList<Food>> groupedFood) {
        /* This function returns ArrayList of all foods from a given HashMap */

        ArrayList<Food> foodList = new ArrayList<>();
        for (ArrayList<Food> arrayList : groupedFood.values()) {
            foodList.addAll(arrayList);
        }

        return foodList;
    }


    public ArrayList<Food> getFoodsByExactName(String name) {
        /* function currently only used for unit testing */

        String[] columns = {"fdc_id", "description"};
        String[] whereArgs = {name};

        String table = FOOD_TABLE;
        if (getLanguagePref() != null && !getLanguagePref().equals("en")) {
            table = "localization_" + getLanguagePref();
        }
        Cursor c = db.query(table, columns, "description = ?", whereArgs, null, null, null);

        ArrayList<Food> foods = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                String foodId = c.getString(0);
                String description = c.getString(1);
                Food f = new Food(description, foodId);
                foods.add(f);
            } while (c.moveToNext());
        }
        c.close();
        return foods;
    }

    public ArrayList<Food> getFoodsByPartialName(String substring) {
        /* This function searches for a given substring */

        String table = "food";
        String[] columns = {"fdc_id", "description"};
        String[] whereArgs = {"%" + substring + "%", "disabled"};
        String orderBy = String.format("description = \"%s\" DESC, description LIKE \"%s%%\" DESC, LENGTH(description) ASC", substring, substring);
        String selectionStm = "description LIKE ? and data_type != ?";

        if (getLanguagePref() != null && !getLanguagePref().equals("en")) {
            table = "localization_" + getLanguagePref();
            selectionStm = "description LIKE ?";
            whereArgs = new String[1];
            whereArgs[0] = "%" + substring + "%";
        }

        Cursor c = db.query(table, columns, selectionStm, whereArgs, null, null, orderBy, null);
        ArrayList<Food> foods = new ArrayList<>();
        if (substring.isEmpty()) {
            c.close();
            return foods;
        }

        if (c.moveToFirst()) {
            do {
                String foodId = c.getString(0);
                String description = c.getString(1);
                Food f = new Food(description, foodId);
                foods.add(f);
            } while (c.moveToNext());
        }

        /* search for any custom foods that are not localized */
        if (getLanguagePref() != null && !getLanguagePref().equals("en")) {
            String[] whereArgsCustom = {"app_custom", "%" + substring + "%", "disabled"};
            Cursor customFoods = db.query(FOOD_TABLE, columns, "data_type == ? and description LIKE ? and data_type != ?", whereArgsCustom, null, null, orderBy, null);
            if (customFoods.moveToFirst()) {
                do {
                    String foodId = customFoods.getString(0);
                    String description = customFoods.getString(1);
                    Food f = new Food(description, foodId);
                    foods.add(f);
                } while (customFoods.moveToNext());
            }
            customFoods.close();
        }

        c.close();
        return foods;
    }

    public HashMap<String, Integer> getNutrientsForFood(String foodId) {

        if (foodNutritionResults.containsKey(foodId)) {
            return foodNutritionResults.get(foodId);
        }

        HashMap<String, Integer> ret = new HashMap<>();
        String table = getNutrientTableForFdcId(foodId);

        String[] columns = {"nutrient_id", "amount"};
        String whereStm = String.format("fdc_id = \"%s\"", foodId);
        Cursor nutrients = db.query(table, columns, whereStm, null, null, null, null);

        if (nutrients.moveToFirst()) {
            do {

                /* convert raw amount into native value */
                String nutrientID = nutrients.getString(0);
                Integer rawAmount = nutrients.getInt(1);
                ret.put(nutrientID, rawAmount);
            } while (nutrients.moveToNext());
        } else {
            Log.w("NA", "No Nutrition found for this foodId: " + foodId + "in sub-db: " + table);
            nutrients.close();
            return null;
        }
        nutrients.close();

        foodNutritionResults.put(foodId, ret);
        return ret;
    }

    public static String getNutrientNativeUnit(String nutrientID) throws IllegalStateException {
        if(nutritionNativeUnitMap.containsKey(nutrientID)){
            return nutritionNativeUnitMap.get(nutrientID);
        }
        if (db == null) {
            throw new IllegalStateException("Database was not initialized before use of static member.");
        }

        String tableNut = "nutrient";
        String[] columnsNut = {"unit_name"};
        String whereStmNut = String.format("id = \"%s\"", nutrientID);
        Cursor nutrientConversion = db.query(tableNut, columnsNut, whereStmNut, null, null, null, null);

        if (!nutrientConversion.moveToFirst()) {
            Log.w("ConversionWarning", "Nutrient native unit not found, assuming microgram. " + nutrientID);
            nutrientConversion.close();
            return Conversions.MICROGRAM;
        }

        String unitName = nutrientConversion.getString(0);
        nutrientConversion.close();
        nutritionNativeUnitMap.put(nutrientID, unitName);
        return unitName;
    }

    public ArrayList<Food> getLoggedFoodByGroupId(int groupId) {

        /* check cache */
        if (foodGroupResult.containsKey(groupId)) {
            return foodGroupResult.get(groupId);
        }

        String[] whereArgs = {Integer.toString(groupId)};
        String[] columns = {"food_id", "loggedAt", "amount", "portion_type"};
        Cursor c = db.query(JOURNAL_TABLE, columns, "group_id = ?", whereArgs, null, null, null);

        ArrayList<Food> ret = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                String foodId = c.getString(0);
                String date = c.getString(1);
                float amount = c.getFloat(2);
                PortionType portionType = PortionType.valueOf(c.getString(3));
                Food f = this.getFoodById(foodId, date);
                if (f != null) {
                    f.associatedAmount = amount;
                    f.associatedPortionType = portionType;
                    f.portionTypeInGram = this.getPortionToGramRatio(f, portionType);
                    ret.add(f);
                }
            } while (c.moveToNext());
        }
        c.close();
        foodGroupResult.put(groupId, ret);
        return ret;
    }

    public ArrayList<Food> getSuggestionsForCombination(ArrayList<SelectedFoodItem> selectedSoFarItems) {
        /* This function returns suggestions for create_foods to log based on previously selected combinations */

        ArrayList<Food> selectedSoFar = new ArrayList<>();
        for (SelectedFoodItem item : selectedSoFarItems) {
            selectedSoFar.add(item.food);
        }

        HashMap<Integer, ArrayList<Food>> prevSelected = getLoggedFoodsByDate(LocalDate.MIN, LocalDate.MAX, null);
        ArrayList<SuggestionHelper> suggestionCounter = new ArrayList<>();

        /* try all group keys */
        for (Integer key : prevSelected.keySet()) {
            /* check if any of the create_foods in the group is already selected */
            for (Food f : Objects.requireNonNull(prevSelected.get(key))) {
                if (selectedSoFar.contains(f)) {
                    /* if any is selected, add all but the already selected food */
                    ArrayList<Food> toBeAddedFoodList = prevSelected.get(key);
                    if (toBeAddedFoodList == null) {
                        continue;
                    }
                    for (Food toBeAddedFood : toBeAddedFoodList) {
                        if (selectedSoFar.contains(toBeAddedFood)) {
                            continue;
                        }
                        SuggestionHelper current = new SuggestionHelper(toBeAddedFood);
                        if (suggestionCounter.contains(current)) {
                            suggestionCounter.get(suggestionCounter.indexOf(current)).counter += 1;
                        } else {
                            suggestionCounter.add(current);
                        }
                    }
                    break;
                }
            }
        }

        int limit = 10;
        if (limit >= suggestionCounter.size()) {
            limit = suggestionCounter.size();
        }

        final ArrayList<Food> results = new ArrayList<>();
        Collections.sort(suggestionCounter);
        for (int i = 0; i < limit; i++) {
            results.add(suggestionCounter.get(i).food);
        }

        return results;
    }

    public boolean checkCustomNameFoodExists(String description) {
        String[] args = {description};
        String[] columns = {"fdc_id"};
        Cursor c = db.query(FOOD_TABLE, columns, "description = ?", args, null, null, null);
        if (c.moveToNext()) {
            c.close();
            return true;
        }
        c.close();
        return false;
    }

    public synchronized boolean changeCustomFood(Food origFood, Food changedFood) {
        if (!origFood.isIdValid() || !checkFoodExists(origFood)) {
            return false;
        }
        invalidateFoodIdInCache(origFood.id);
        String[] whereArgs = {origFood.id};
        db.delete(FOOD_TABLE, "fdc_id = ?", whereArgs);
        createNewFood(changedFood, Integer.parseInt(changedFood.id));

        /* flush relevant cache */
        invalidateFoodIdInCache(origFood.id);
        foodNutritionResults.remove(origFood.id);

        return true;
    }

    public synchronized Food createNewFood(Food food, int reuseFdcId) {

        int currentId = reuseFdcId;
        if (currentId < 0) {
            String[] columns = {"fdc_id, data_type"};
            Cursor c = db.query(FOOD_TABLE, columns, "data_type = \"app_custom\"", null, null, null, "fdc_id DESC", "1");
            int maxUsedID = DEFAULT_MIN_CUSTOM_ID;
            if (c.moveToNext()) {
                maxUsedID = c.getInt(0);
            }
            c.close();
            currentId = maxUsedID + 1;
        }

        /* insert the foods */
        ContentValues valuesFood = new ContentValues();
        valuesFood.put("fdc_id", currentId);
        valuesFood.put("data_type", "app_custom");
        valuesFood.put("description", food.name);
        valuesFood.put("food_category_id", "");
        db.insert(FOOD_TABLE, null, valuesFood);

        /* insert the nutrition for the food */
        for (NutritionElement ne : food.nutrition.getElements().keySet()) {
            ContentValues valuesNutrient = new ContentValues();
            Log.wtf("TEST", ne.toString());
            valuesNutrient.put("id", currentId * 10);
            valuesNutrient.put("fdc_id", currentId);
            valuesNutrient.put("nutrient_id", Nutrition.databaseIdFromEnum(ne));
            valuesNutrient.put("amount", food.nutrition.getElements().get(ne));
            db.insert("food_nutrient_custom", null, valuesNutrient);
        }

        ContentValues valuesEnergy = new ContentValues();
        valuesEnergy.put("id", currentId * 10);
        valuesEnergy.put("fdc_id", currentId);
        valuesEnergy.put("nutrient_id", Food.DB_ID_ENERGY);
        valuesEnergy.put("amount", food.energy);
        db.insert("food_nutrient_custom", null, valuesEnergy);

        ContentValues valuesFiber = new ContentValues();
        valuesFiber.put("id", currentId * 10);
        valuesFiber.put("fdc_id", currentId);
        valuesFiber.put("nutrient_id", Food.DB_ID_FIBER);
        valuesFiber.put("amount", food.fiber);
        db.insert("food_nutrient_custom", null, valuesFiber);

        food.id = Integer.toString(currentId);

        /* flush relevant cache */
        invalidateFoodIdInCache(food.id);
        foodNutritionResults.remove(food.id);

        return food;
    }

    public boolean deleteCustomFood(Food f) {
        if (checkFoodExists(f)) {
            String[] args = {f.id};
            String[] columns = {"fdc_id"};
            Cursor c = db.query(FOOD_TABLE, columns, "fdc_id = ?", args, null, null, null);
            if (c.moveToNext()) {
                if (checkFoodInJournal(f)) {
                    markFoodAsDeactivated(f);
                } else {
                    db.delete("food", "fdc_id = ?", args);
                    db.delete("food_nutrient_custom", "fdc_id = ?", args);
                    foodCache.remove(f.id);
                }
                return true;
            }
            c.close();
        }

        /* flush relevant cache */
        invalidateFoodIdInCache(f.id);
        foodNutritionResults.remove(f.id);

        return false;
    }

    public void markFoodAsDeactivated(Food f) {
        String[] whereArgs = {f.id};
        ContentValues values = new ContentValues();
        values.put("data_type", "disabled");
        db.update(FOOD_TABLE, values, "fdc_id = ?", whereArgs);
    }

    public void markFoodAsActivated(Food f) {
        String[] whereArgs = {f.id};
        ContentValues values = new ContentValues();
        values.put("data_type", "app_custom");
        db.update(FOOD_TABLE, values, "fdc_id = ?", whereArgs);
    }

    private boolean checkFoodInJournal(Food f) {
        String[] columns = {"food_id"};
        String[] whereArgs = {f.id};
        Cursor c = db.query(JOURNAL_TABLE, columns, "food_id = ?", whereArgs, null, null, null);
        boolean hasNext = c.moveToNext();
        c.close();
        return hasNext;
    }

    public ArrayList<Food> getAllCustomFoods() {
        ArrayList<Food> ret = new ArrayList<>();
        String[] columns = {"fdc_id"};
        Cursor c = db.query(true, "food_nutrient_custom", columns, null, null, "fdc_id", null, null, null);
        if (c.moveToNext()) {
            Cursor cFood;
            do {
                String[] columnsFood = {"description"};
                String fdc_id = c.getString(0);
                String[] selectionArgs = {fdc_id, "disabled"};
                cFood = db.query(FOOD_TABLE, columnsFood, "fdc_id = ? AND data_type != ?", selectionArgs, null, null, null, null);
                if (cFood.moveToNext()) {
                    do {
                        ret.add(new Food(cFood.getString(0), fdc_id));
                    } while (cFood.moveToNext());
                }
            } while (c.moveToNext());
            cFood.close();
        }
        c.close();
        return ret;
    }

    public JSONObject exportDatabase(boolean exportLog, boolean exportCustomFoods, boolean exportPersonalInfo) throws JSONException {
        JSONObject ret = new JSONObject();

        if (exportLog) {
            JSONArray journal = new JSONArray();
            HashMap<Integer, ArrayList<Food>> journalEntries = this.getLoggedFoodsByDate(LocalDateTime.MIN, LocalDateTime.MAX);
            if (journalEntries != null) {
                for (Integer key : journalEntries.keySet()) {
                    JSONObject foodGroup = new JSONObject();

                    ArrayList<Food> foodGroupEntries = journalEntries.get(key);
                    if (foodGroupEntries == null || foodGroupEntries.isEmpty()) {
                        continue;
                    }

                    LocalDateTime loggedAt = null;
                    JSONArray foodsInGroup = new JSONArray();
                    for (Food f : foodGroupEntries) {
                        loggedAt = f.loggedAt;
                        foodsInGroup.put(f.toJsonObject());
                    }
                    foodGroup.put("foods", foodsInGroup);
                    foodGroup.put("date", loggedAt.format(Utils.sqliteDatetimeFormat));
                    journal.put(foodGroup);
                }
            }

            ret.put("journal", journal);
        }

        if (exportCustomFoods) {
            JSONArray customFoods = new JSONArray();
            ArrayList<Food> allCustomFoods = getAllCustomFoods();

            if (allCustomFoods != null) {
                for (Food f : allCustomFoods) {
                    JSONObject customFood = f.toJsonObject();

                    /* filter out zero values for nutrients/energy/fiber */
                    HashMap<String, Integer> nutrientsForFood = getNutrientsForFood(f.id);
                    ArrayList<String> keyList = new ArrayList<>(nutrientsForFood.keySet());
                    for (String key : keyList) {
                        if (nutrientsForFood.get(key) == 0) {
                            nutrientsForFood.remove(key);
                        }
                    }

                    customFood.put("nutrition", new JSONObject(nutrientsForFood));
                    customFoods.put(customFood);
                }
            }

            ret.put("custom", customFoods);
        }

        if (exportPersonalInfo) {
            JSONObject personalInfo = new JSONObject();

            personalInfo.put("age", getPersonAge());
            personalInfo.put("height", getPersonHeight());
            personalInfo.put("gender", getPersonGender());

            TreeMap<LocalDate, Integer> dbWeights = getWeightAll();
            JSONArray weights = new JSONArray();
            if (dbWeights != null) {
                List<LocalDate> keyList = new ArrayList<>(dbWeights.keySet());
                for (LocalDate date : keyList) {
                    JSONObject weight = new JSONObject();
                    weight.put("date", date);
                    weight.put("weight", dbWeights.get(date));
                    weights.put(weight);
                }
            }

            JSONArray energyTargets = new JSONArray();
            TreeMap<LocalDate, Integer> dbEnergyTargets = getEnergyTargetsAll();
            if (dbEnergyTargets != null) {
                List<LocalDate> keyList = new ArrayList<>(dbEnergyTargets.keySet());
                for (LocalDate date : keyList) {
                    JSONObject target = new JSONObject();
                    target.put("date", date);
                    target.put("target", dbWeights.get(date));
                    energyTargets.put(target);
                }
            }

            personalInfo.put("energyTargets", energyTargets);
            personalInfo.put("weights", weights);
            ret.put("personInfo", personalInfo);
        }
        return ret;
    }

    public void importDatabaseBackup(JSONObject in) throws JSONException {
        JSONArray journal = null;
        JSONArray custom = null;
        JSONObject person = null;

        try {
            journal = in.getJSONArray("journal");
        } catch (JSONException e) {
            Log.wtf("INFO", "No Journal Info in Import");
        }

        try {
            custom = in.getJSONArray("custom");
        } catch (JSONException e) {
            Log.wtf("INFO", "No Custom Food Info in Import");
        }

        try {
            person = in.getJSONObject("personInfo");
        } catch (JSONException e) {
            Log.wtf("INFO", "No Person Info in Import");
        }

        if (journal != null) {
            /* iterate through foods groups with date */
            for (int i = 0; i < journal.length(); i++) {

                JSONObject foodsAndDate = journal.getJSONObject(i);
                JSONArray foods = foodsAndDate.getJSONArray("foods");
                String date = foodsAndDate.getString("date");
                LocalDateTime dateTime = LocalDateTime.parse(date, Utils.sqliteDatetimeFormat);

                /* iterate through foods */
                ArrayList<Food> foodsArrayList = new ArrayList<>();
                for (int k = 0; k < foods.length(); k++) {
                    JSONObject jsonFood = foods.getJSONObject(k);
                    Food f = new Food(jsonFood.getString("name"), jsonFood.getString("id"), null, dateTime);
                    f.setAssociatedAmount(jsonFood.getInt("amount"));
                    PortionType portionType = PortionType.valueOf(jsonFood.getString("portionType"));
                    f.setAssociatedPortionType(portionType);
                    f.setPortionTypeInGram(getPortionToGramRatio(f, portionType));
                    foodsArrayList.add(f);
                }

                /* add foods to database */
                logExistingFoods(foodsArrayList, dateTime, false);
            }
        }

        if (custom != null) {
            for (int i = 0; i < custom.length(); i++) {

                /* parse json object */
                JSONObject customFood = custom.getJSONObject(i);
                String id = customFood.getString("id");
                String name = customFood.getString("name");
                Integer amount = customFood.getInt("amount");

                /* create food */
                Food f = new Food(name, id, null, null);

                /* set nutrition */
                String nutritionInformation = customFood.getString("nutrition");
                JSONObject nutInfoObjCollection = new JSONObject(nutritionInformation);
                for (NutritionElement key : Nutrition.nutritionElementToDatabaseId.keySet()) {
                    String value = Nutrition.nutritionElementToDatabaseId.get(key);
                    if (value != null && nutInfoObjCollection.has(value)) {
                        f.nutrition.put(key, nutInfoObjCollection.getInt(value));
                    }
                }

                /* add food to db */
                createNewFood(f, Integer.parseInt(id));
            }
        }

        if (person != null) {

            /* parse json object and save to db */
            int age = person.getInt("age");
            if(age != 0) {
                setPersonAge(age);
            }
            int height = person.getInt("height");
            setPersonHeight(height);
            String gender = person.getString("gender");
            setPersonGender(gender);

            /* get documented weights */
            JSONArray weights = person.getJSONArray("weights");
            for (int i = 0; i < weights.length(); i++) {
                JSONObject weightObj = weights.getJSONObject(i);
                String strDate = weightObj.getString("date");
                LocalDate date = LocalDate.parse(strDate, Utils.sqliteDateFormat);
                int weight = weightObj.getInt("weight");
                addWeightAtDate(weight, date);
            }

            /* get documented weights */
            JSONArray energyTargets = person.getJSONArray("weights");
            for (int i = 0; i < energyTargets.length(); i++) {
                JSONObject energyTargetObj = energyTargets.getJSONObject(i);
                String strDate = energyTargetObj.getString("date");
                LocalDate date = LocalDate.parse(strDate, Utils.sqliteDateFormat);
                int weight = energyTargetObj.getInt("target");
                addWeightAtDate(weight, date);
            }
        }
    }

    public String debugDumpFoodlog() {
        StringBuilder ret = new StringBuilder();
        Cursor all = db.query(JOURNAL_TABLE, null, null, null, null, null, null);
        while (all.moveToNext()) {
            for (int i = 0; i < all.getColumnCount(); i++) {
                ret.append(all.getString(i)).append(" | ");
            }
            ret.append("\n");
        }
        all.close();
        return ret.toString();
    }

    public void setPersonWeight(int weightInKg) throws IllegalArgumentException {

        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("weight", weightInKg);
        editor.apply();
    }

    public void setPersonAge(int age) throws IllegalArgumentException {
        if (age < 18 || age > 150) {
            throw new IllegalArgumentException("Age must be between 18 and 150");
        }
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("age", age);
        editor.apply();
        /* set age in nutrition for recommendation */
        Nutrition.setPersonAge(age);
    }

    public void setCuratedFoodsPreference(int state) {
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("curate_foods", state);
        editor.apply();
    }

    public void setPersonEnergyReq(int energyReq, LocalDate date) throws IllegalArgumentException {
        if (energyReq < 1000) {
            throw new IllegalArgumentException("Energy target must be above 1000kcal");
        }

        ContentValues values = new ContentValues();
        if(date == null){
            date = LocalDate.now();
        }
        values.put("date", date.format(Utils.sqliteDateFormat));
        values.put("value", energyReq);

        db.insertWithOnConflict(CALORIE_TARGET_BY_DATE_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void setPersonHeight(int sizeInCm) throws IllegalArgumentException {
        if (sizeInCm < 0 || sizeInCm > 300) {
            throw new IllegalArgumentException("Height must be between 0 and 300 cm");
        }
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("height", sizeInCm);
        editor.apply();
    }

    public void setPersonGender(String gender) throws IllegalArgumentException {
        if (!gender.equals("male") && !gender.equals("female")) {
            throw new IllegalArgumentException("Gender must be 'male' or 'female'.");
        }
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("gender", gender);
        editor.apply();
        /* set gender in nutrition for recommendation */
        Nutrition.setPersonGender(gender);
    }

    public int getPersonWeight() {
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE);
        return pref.getInt("weight", -1);
    }

    public int getPersonHeight() {
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE);
        return pref.getInt("height", -1);
    }

    public int getPersonAge() {
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE);
        return pref.getInt("age", -1);
    }

    public int getCuratedFoodsPreference() {
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE);
        return pref.getInt("curated_foods", -1);
    }

    public int getPersonEnergyReq(LocalDate date) {
        int energyReq = 2000; /* TODO calc from other values #Issue40 */

        if(date == null){
            date = LocalDate.now();
        }

        String dateString = date.format(Utils.sqliteDateFormat);
        String[] whereArgs = {dateString};
        String[] columns = {"target"};
        Cursor c = db.query(CALORIE_TARGET_BY_DATE_TABLE, columns, "date <= ?", whereArgs, null, null, "date DESC", "1");
        if(c.moveToNext()){
            energyReq = c.getInt(0);
        }
        c.close();

        return energyReq;
    }


    public String getPersonGender() {
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE);
        return pref.getString("gender", "none");
    }

    public double getPersonBmi() {
        int height = this.getPersonHeight();
        int weight = this.getPersonWeight();

        if(height <= 0 || weight <= 0){
            return Double.NaN;
        }
        double bmi = (weight/1000.f) / ((height * height) / 10000.0);
        return Math.round(bmi * 100.0) / 100.0;
    }

    public void setLanguagePref(String languagePref) {
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("languagePref", languagePref);
        editor.apply();
    }

    public String getLanguagePref() {
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE);
        return pref.getString("languagePref", null);
    }

    public void close() {
        db.close();
    }

    public ArrayList<PortionType> portionsForFood(Food food) {

        String[] columns = {"cup ", "small", "medium", "large", "packet", "scoop", "tablespoon", "teaspoon", "ml"};
        String[] whereArgs = {food.id};
        Cursor c = db.query(ASSIGNED_PORTION, columns, "fdc_id= ?", whereArgs, null, null, null);
        ArrayList<PortionType> ret = new ArrayList<>();
        String prefType;
        if (c.moveToFirst()) {
            if (!c.getString(0).equals("")) {
                ret.add(PortionType.CUP);
            }
            if (!c.getString(1).equals("")) {
                ret.add(PortionType.SMALL);
            }
            if (!c.getString(2).equals("")) {
                ret.add(PortionType.MEDIUM);
            }
            if (!c.getString(3).equals("")) {
                ret.add(PortionType.LARGE);
            }
            if (!c.getString(4).equals("")) {
                ret.add(PortionType.PACKET);
            }
            if (!c.getString(5).equals("")) {
                ret.add(PortionType.SCOOP);
            }
            if (!c.getString(6).equals("")) {
                ret.add(PortionType.TABLESPOON);
            }
            if (!c.getString(7).equals("")) {
                ret.add(PortionType.TEASPOON);
            }
            if (!c.getString(8).equals("")) {
                ret.add(PortionType.ML);
            }
            if (!ret.contains(PortionType.ML)) {
                ret.add(PortionType.GRAM);
            }
            c.close();
        }
        return ret;
    }


    public PortionType getPreferredPortionType(Food food) {
        ArrayList<PortionType> portionTypes = portionsForFood(food);
        if(portionTypes.contains(PortionType.ML)){
            return PortionType.ML;
        }else if(portionTypes.size() > 0){
            return portionTypes.get(0);
        }else{
            return PortionType.GRAM;
        }
    }

    public double getPortionToGramRatio(Food food, PortionType portionType) {
        if (portionType == PortionType.GRAM){
            return 1.0;
        }
        String[] columns = { portionType.toString() };
        String[] whereArgs = { food.id };
        Cursor c = db.query(ASSIGNED_PORTION, columns, "fdc_id = ?", whereArgs, null, null, null);
        double ratio = Double.NaN;
        if (c.moveToFirst()) {
            ratio = c.getFloat(0);
        }
        c.close();
        return ratio;
    }

    //returns TreeMap of food-name and amount, which contains highest nutrient values:
    public SortedMap<Food, Float> getRecommendationMap(NutritionElement nutritionElement) {
        /* look for food with highest amount of dedicated NutritionElement in all food_nutrient_ -tables */

        String idNutritionElement = Integer.toString(Nutrition.databaseIdFromEnum(nutritionElement));
        String idEnergy = "1008";

        // search all tables for high nutrient values:
        TreeMap<Food, Float> resultMap = new TreeMap<>();
        for (String tableId : foodNutrientTableIds) {
            String[] columnNow = {"nut.fdc_id", "(round((nut.amount / en.amount), 4)) as ratio"};
            String table = tableId + " as nut, " + tableId + " as en";
            float minAm = 0f;
            String where = "nut.fdc_id = en.fdc_id AND nut.nutrient_id=? AND en.nutrient_id=1008 AND ratio>=" + minAm;
            String[] whereArgs = {idNutritionElement};
            Cursor cc = db.query(table, columnNow, where, whereArgs, null, null, "ratio DESC", "5");

            while (cc.moveToNext()) {
                String foodID = cc.getString(0);
                float amount = Float.parseFloat(cc.getString(1));

                // only consider values that are higher than the lowest tracked by now - since we analyze tables separately
                if (minAm == 0 || amount < minAm) minAm = amount;
                Food f = (getFoodById(foodID));

                /* todo: workaround of (not fixed by now) bug - there are more foodIds in nutrient-table than in the table used by 'getFoodById' Issue#41 */
                if (f != null) {
                    resultMap.put(f, amount);
                }
            }
            cc.close();
        }

        // sort TreeMap and return the 5 most nutritious foods:
        return Utils.sortRecommendedTreeMap(resultMap);
    }

    /* weights table interactions */

    public void createWeightsTableIfNotExist() {
        final String CREATE_TABLE_WEIGHT = "CREATE TABLE weightsByDate (date TEXT PRIMARY KEY, weight INTEGER)";
        try {
            db.execSQL(CREATE_TABLE_WEIGHT);
        } catch (android.database.SQLException e) {
            Log.w("DB", "Table already Exists");
        }
    }

    public void addWeightAtDate(int weightInGram, LocalDate date) {
        if (weightInGram < 40000 || weightInGram > 600000) {
            throw new IllegalArgumentException("Weight must be between 40kg and 600kg");
        }
        String dateString = date.format(Utils.sqliteDateFormat);
        ContentValues values = new ContentValues();
        values.put("date", dateString);
        values.put("weight", weightInGram);
        db.insertWithOnConflict(WEIGHTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        /* set as current weight if current Day or most recent Log */
        if (date.equals(LocalDate.now())) {
            Log.wtf("DATE", "NOW");
            setPersonWeight(weightInGram);
            return;
        }

        Log.wtf("DATE", "notNOW");
        ArrayList<LocalDate> weightKeys = new ArrayList<>(getWeightAll().keySet());
        Collections.sort(weightKeys);
        if (date.isAfter(weightKeys.get(0))) {
            setPersonWeight(weightInGram);
        }

    }


    public void removeWeightAtDate(int weightInGram, LocalDate date) {
        String[] whereArgs = {date.format(Utils.sqliteDateFormat)};
        db.delete(WEIGHTS, "date = ?", whereArgs);
    }

    public int getWeightAtDate(LocalDate date) {
        String[] columns = {"weight"};
        String dateString = date.format(Utils.sqliteDateFormat);
        String[] whereArgs = {dateString};

        Cursor c = db.query(WEIGHTS, columns, "date = ?", whereArgs, null, null, null);
        if (c.moveToNext()) {
            int weight = c.getInt(0);
            c.close();
            return weight;
        } else {
            throw new AssertionError("Tried to access a weight with a date that doesn't exist.");
        }
    }

    public TreeMap<LocalDate, Integer> getWeightAll() {
        TreeMap<LocalDate, Integer> weightsByDate = new TreeMap<>();
        String[] columns = {"date", "weight"};

        Cursor c = db.query(WEIGHTS, columns, null, null, null, null, null);
        while (c.moveToNext()) {
            String dateString = c.getString(0);
            int weight = c.getInt(1);
            LocalDate localDate = LocalDate.parse(dateString, Utils.sqliteDateFormat);
            weightsByDate.put(localDate, weight);
        }
        c.close();

        return weightsByDate;
    }

    public TreeMap<LocalDate, Integer> getEnergyTargetsAll() {
        TreeMap<LocalDate, Integer> targetsByDate = new TreeMap<>();
        String[] columns = {"date", "target"};

        Cursor c = db.query(CALORIE_TARGET_BY_DATE_TABLE, columns, null, null, null, null, null);
        while (c.moveToNext()) {
            String dateString = c.getString(0);
            int target = c.getInt(1);
            LocalDate localDate = LocalDate.parse(dateString, Utils.sqliteDateFormat);
            targetsByDate.put(localDate, target);
        }
        c.close();

        return targetsByDate;
    }

    public void deleteCustomFoodGroup(int groupId) {
        String[] whereArgs = {Integer.toString(groupId)};
        int rowsDeleted = db.delete(JOURNAL_TABLE, "group_id = ?", whereArgs);
        Log.d("Rows deleted for food custom food group:", rowsDeleted + " " + groupId);
    }


    private static class SuggestionHelper implements Comparable<SuggestionHelper> {
        public int counter;
        public final Food food;

        public SuggestionHelper(Food food) {
            this.counter = 1;
            this.food = food;
        }

        @Override
        public int compareTo(SuggestionHelper s) {
            return Integer.compare(this.counter, s.counter);
        }
    }
}
