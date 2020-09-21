package com.example.nutritionapp.other;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.nutritionapp.R;
import com.example.nutritionapp.foodJournal.AddFoodsLists.SelectedFoodItem;

import org.apache.commons.io.IOUtil;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import static android.database.sqlite.SQLiteDatabase.OPEN_READWRITE;

public class Database {

    private static final int DEFAULT_MIN_CUSTOM_ID = 100000000;
    final String FILE_KEY = "DEFAULT";
    final SQLiteDatabase db;
    private final ArrayList<Integer> fdcIdToDbNumber = new ArrayList<>();
    private final Activity srcActivity;
    private HashMap<String,Food> foodCache = new HashMap<>();

    private void copyDatabaseIfMissing(InputStream inputStream, String targetPath){
        File file = new File(targetPath);
        if (!file.exists()) {
            InputStream in = srcActivity.getResources().openRawResource(R.raw.food);

            try {
                OutputStream out = new FileOutputStream(targetPath);
                IOUtil.copy(in, out);
                out.close();
                in.close();
            } catch (FileNotFoundException e) {
                Log.wtf("WTF", "OUTPUT file not found");
            } catch (IOException e) {
                Log.wtf("yeah whatever", "fuck");
            }
        }
    }
    private void generateNutritionTableSelectionMap(){
        /* This function populates an array with information about which
        fdc_id can be found in which food_nutrient_table.
         */

        ArrayList<String> tmpDbNames = new ArrayList<>();
        String[] columns = { "name" };
        Cursor c = db.query("sqlite_master", columns, "type = \"table\" AND name LIKE \"food_nutrient_%\"", null, null, null, null);
        while(c.moveToNext()){
            String tmpDbName = c.getString(0);
            if(tmpDbName.contains("custom")){
                continue;
            }
            tmpDbNames.add(tmpDbName);
        }

        Collections.sort(tmpDbNames);
        for(String table : tmpDbNames){
            String[] columnsNut = { "fdc_id" };
            Cursor nutC = db.query(table, columnsNut, null, null, null, null, "fdc_id ASC", "1");
            if(nutC.moveToNext()) {
                int minFdcId = nutC.getInt(0);
                fdcIdToDbNumber.add(minFdcId);
            }else{
                throw new AssertionError("Food Nutrition tables missing?!");
            }
        }

    }
    private String getNutrientTableForFdcId(String foodId) {

        if(Integer.parseInt(foodId) > DEFAULT_MIN_CUSTOM_ID){
            return "food_nutrient_custom";
        }
        int count = 0;
        for(Integer el : fdcIdToDbNumber){
            if(Integer.parseInt(foodId) < el){
                /* the number in the fdcIdToDbNumber is the lowest id in the respective db  */
                /* so if we are below that number we must select the previous db            */
                if(count == 0){
                    /* this means we have DB so low it isn't even in the first db   */
                    /* in this case we return the first db, if we returned a bad db */
                    /* the app would crash, which seems a bit extreme in this case  */
                    break;
                }
                return String.format("food_nutrient_%02d", count - 1);
            }else{
                count++;
            }
        }
        Log.w("WARNING", "ID " + foodId +" has no associated Database. (nut good)");
        final String DEFAULT_DB = "food_nutrient_00";
        return DEFAULT_DB;
    }


    public Database(Activity srcActivity) {
        this.srcActivity = srcActivity;
        InputStream srcFile = srcActivity.getResources().openRawResource(R.raw.food);
        String path = srcActivity.getFilesDir().getParent() + "/food.db";
        copyDatabaseIfMissing(srcFile, path);
        db = SQLiteDatabase.openDatabase(path, null, OPEN_READWRITE);
        generateNutritionTableSelectionMap();
    }

    /* ################ FOOD LOGGING ############## */
    public synchronized void logExistingFoods(ArrayList<SelectedFoodItem> selectedSoFarItems, LocalDateTime d, Object hackyhack) {
        /* This functions add a list of create_foods to the journal at a given date */
        ArrayList<Food> selectedSoFar = new ArrayList<>();
        for (SelectedFoodItem item : selectedSoFarItems) {
            selectedSoFar.add(item.food);
        }
        logExistingFoods(selectedSoFar, d);
    }
    public synchronized void logExistingFoods(ArrayList<Food> foods, LocalDateTime d) {
        /* This functions add a list of create_foods to the journal at a given date */

        if(d == null){
            d = LocalDateTime.now();
        }

        Random random = new Random();
        int groupID =  random.nextInt(1000000);
        for (Food f : foods) {
            Log.wtf("FOOD", d.format(Utils.sqliteDatetimeFormat));
            ContentValues values = new ContentValues();
            values.put("food_id", f.id);
            values.put("date", d.toString());
            values.put("group_id", groupID);
            values.put("loggedAt", d.format(Utils.sqliteDatetimeFormat));
            values.put("amountInGram", f.getAssociatedAmount());
            db.insert("foodlog", null, values);
        }
    }
    public synchronized void updateFoodGroup(ArrayList<SelectedFoodItem> updatedListWithAmounts, int groupId, LocalDateTime loggedAt) {

        String where = String.format("group_id = %d", groupId);
        db.delete("foodlog",  where, null);

        for(SelectedFoodItem fItem : updatedListWithAmounts){
            Food f = fItem.food;
            ContentValues values = new ContentValues();
            values.put("food_id", f.id);
            values.put("date", loggedAt.toString());
            values.put("group_id", groupId);
            values.put("loggedAt", loggedAt.format(Utils.sqliteDatetimeFormat));
            values.put("amountInGram", f.getAssociatedAmount());
            db.insert("foodlog", null, values);
        }
    }

    public void deleteLoggedFood(ArrayList<Food> foods, LocalDateTime d){
        for(Food f:foods){
            deleteLoggedFood(f, d);
        }
    }
    public synchronized void deleteLoggedFood(Food f, LocalDateTime d) {
        String whereClause = String.format("food_id = \"%s\" AND loggedAt = \"%s\"", f.id, d.format(Utils.sqliteDatetimeFormat));
        db.delete("foodlog", whereClause, null);
    }

    public Food getFoodById(String foodId, String loggedAtIso) {

        String table = "food";
        String[] columns = {"description"};
        String whereStm = String.format("fdc_id = \"%s\"", foodId);
        Cursor c = db.query(table, columns, whereStm, null, null, null, null);

        if(foodCache.containsKey(foodId)){
            return foodCache.get(foodId);
        }else {

            LocalDateTime loggedAt = null;
            if (loggedAtIso != null) {
                loggedAt = LocalDateTime.parse(loggedAtIso, Utils.sqliteDatetimeFormat);
            }

            if (c.moveToFirst()) {
                String foodName = c.getString(0);
                Food f = new Food(foodName, foodId, this, loggedAt);
                foodCache.put(foodId, f);
                c.close();
                return f;
            }
            c.close();
            throw new RuntimeException("The food didn't exists, that's unfortunate.");
        }
    }
    public HashMap<Integer, ArrayList<Food>> getLoggedFoodsByDate(LocalDate start, LocalDate end) {
        /* Return create_foods logged by dates */

        HashMap<Integer, ArrayList<Food>> ret = new HashMap<>();

        if(end == null){
            end = start.plusDays(1);
        }
        String startISO = start.format(Utils.sqliteDateZeroPaddedFormat);
        String endISO = end.format(Utils.sqliteDateZeroPaddedFormat);

        String table = "foodlog";
        String[] columns = {"food_id", "group_id", "loggedAt", "amountInGram"};
        String whereStm = String.format("date(loggedAt) between date(\"%s\") and date(\"%s\")" , endISO, startISO);
        if(start.equals(LocalDate.MIN) || end.equals(LocalDate.MAX)){
            whereStm = "date(loggedAt)";
        }
        Cursor c = db.query(table, columns, whereStm, null, null, null, null);

        if (c.moveToFirst()) {
            do {
                String foodId = c.getString(0);
                int groupID = c.getInt(1);
                String loggedAtISO = c.getString(2);
                int amount = c.getInt(3);

                if (ret.containsKey(groupID)) {
                    ArrayList<Food> group = (ArrayList<Food>) ret.get(groupID);
                    Food f = getFoodById(foodId, loggedAtISO);
                    f.setAssociatedAmount(amount);
                    group.add(f);
                }else{
                    ArrayList<Food> group = new ArrayList<>();
                    Food f = getFoodById(foodId, loggedAtISO);
                    f.setAssociatedAmount(amount);
                    group.add(f);
                    ret.put(groupID, group);
                }

            } while (c.moveToNext());
        }
        c.close();
        return ret;
    }

    public ArrayList<Food> getFoodsByPartialName(String substring) {
        /* This function searches for a given substring */

        String table = "food";
        String[] columns = {"fdc_id", "description"};
        String whereStm = String.format("description LIKE \"%%%s%%\"", substring);
        String orderBy = String.format("description = \"%s\" DESC, description LIKE \"%s%%\" DESC", substring, substring);
        Cursor c = db.query(table, columns, whereStm, null, null, null, orderBy, null);

        ArrayList<Food> foods = new ArrayList<>();
        if(substring.isEmpty()){
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
        c.close();
        return foods;
    }
    public HashMap<String,Integer> getNutrientsForFood(String foodId){
        HashMap<String,Integer> ret = new HashMap<>();

        String table = getNutrientTableForFdcId(foodId);

        String[] columns = {"nutrient_id", "amount"};
        String whereStm = String.format("fdc_id = \"%s\"", foodId);
        Cursor nutrients = db.query(table, columns, whereStm, null, null, null, null);
        Cursor nutrientConversion;

        if (nutrients.moveToFirst()) {
            do {

                /* convert raw amount into native value */
                String nutrientID = nutrients.getString(0);
                int rawAmount = nutrients.getInt(1);

                String tableNut = "nutrient";
                String[] columnsNut = {"unit_name"};
                String whereStmNut = String.format("id = \"%s\"", nutrientID);
                nutrientConversion = db.query(tableNut, columnsNut, whereStmNut, null, null, null, null);

                if(nutrientConversion.moveToFirst()){
                    String unitName = nutrientConversion.getString(0);
                    int normalizedAmount = Conversions.normalize(unitName, rawAmount);
                    ret.put(nutrientID, normalizedAmount);
                }else{
                    nutrients.close();
                    nutrientConversion.close();
                    throw new RuntimeException("Nutrient not found?!");
                }
            } while (nutrients.moveToNext());
        }else{
            Log.w("NA", "No Nutrition found for this foodId: " + foodId + "in subdb: "+ table);
            nutrients.close();
            return null;
        }
        nutrients.close();
        nutrientConversion.close();
        return ret;
    }

    public ArrayList<Food> getLoggedFoodByGroupId(int groupId) {
        String whereStm = String.format("group_id = %d" , groupId);
        String[] columns = {"food_id", "loggedAt", "amountInGram"};
        Cursor c = db.query("foodlog", columns, whereStm, null, null, null, null);

        ArrayList<Food> ret = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                String foodId = c.getString(0);
                String date = c.getString(1);
                int amount = c.getInt(2);
                Food f = this.getFoodById(foodId, date);
                f.associatedAmount = amount;
                ret.add(f);
            } while (c.moveToNext());
        }
        return ret;
    }

    public ArrayList<Food> getAllCustomFoods() {
        // TODO
        return null;
    }

    private class SuggestionHelper implements Comparable<SuggestionHelper>{
        public int counter;
        public Food food;
        public SuggestionHelper(Food food){
            this.counter = 1;
            this.food = food;
        }

        @Override
        public int compareTo(SuggestionHelper s) {
            return Integer.compare(this.counter, s.counter);
        }
    }
    public ArrayList<Food> getSuggestionsForCombination(ArrayList<SelectedFoodItem> selectedSoFarItems) {
        /* This function returns suggestions for create_foods to log based on previously selected combinations */

        ArrayList<Food> selectedSoFar = new ArrayList<>();
        for(SelectedFoodItem item : selectedSoFarItems){
            selectedSoFar.add(item.food);
        }

        HashMap<Integer, ArrayList<Food>> prevSelected = getLoggedFoodsByDate(LocalDate.MIN, LocalDate.MAX);
        ArrayList<SuggestionHelper> suggestionCounter= new ArrayList<>();
        ArrayList<Food> suggestions = new ArrayList<>();

        /* try all group keys */
        for(Integer key : prevSelected.keySet()){
            /* check if any of the create_foods in the group is already selected */
            for(Food f : prevSelected.get(key)){
                if(selectedSoFar.contains(f)) {
                    /* if any is selected, add all but the already selected food */
                    for(Food toBeAddedFood: prevSelected.get(key)) {
                        if(selectedSoFar.contains(toBeAddedFood)){
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
        if(limit >= suggestionCounter.size()){
            limit = suggestionCounter.size();
        }

        final ArrayList<Food> results = new ArrayList<>();
        Collections.sort(suggestionCounter);
        for(int i = 0; i < limit; i++){
            results.add(suggestionCounter.get(i).food);
        }
        return results;
    }

    public boolean checkCustomFoodExists(String description){
        String[] args = { description };
        String[] columns = {"fdc_id"};
        Cursor c = db.query("food", columns, "description = ?", args, null, null, null);
        if(c.moveToNext()) {
            return true;
        }
        return false;
    }
    public void createNewFood(Food food) {

        String[] columns = {"fdc_id, data_type"};
        Cursor c = db.query("food", columns, "data_type = \"app_custom\"", null, null, null, "fdc_id", "1");
        int maxUsedID = DEFAULT_MIN_CUSTOM_ID;
        if(c.moveToNext()) {
            maxUsedID = c.getInt(0);
        }

        /* insert the foods */
        ContentValues valuesFood = new ContentValues();
        valuesFood.put("fdc_id", maxUsedID + 1);
        valuesFood.put("data_type", "app_custom");
        valuesFood.put("description", food.name);
        valuesFood.put("food_category_id", "");
        db.insert("food", null, valuesFood);

        /* insert the nutrition for the food */
        for(NutritionElement ne : food.nutrition.getElements().keySet()){
            ContentValues valuesNutrient = new ContentValues();
            valuesNutrient.put("id", 0);
            valuesNutrient.put("fdc_id", maxUsedID + 1);
            valuesNutrient.put("nutrient_id", Nutrition.databaseIdFromEnum(ne));
            valuesNutrient.put("amount", food.nutrition.getElements().get(ne));
            db.insert("food_nutrient_custom", null, valuesNutrient);
        }
    }
    public void deleteCustomFood(String description){
        if(checkCustomFoodExists(description)){
            String[] args = { description };
            String[] columns = {"fdc_id"};
            Cursor c = db.query("food", columns, "description = ?", args, null, null, null);
            if(c.moveToNext()) {
                int fdcId = c.getInt(0);
                String[] argsNut = { Integer.toString(fdcId) };
                db.delete("food", "description = ?", args);
                db.delete("food_nutrient_custom", "fdc_id = ?", argsNut);
            }
        }
    }

    public void setPersonWeight(int weightInKg) throws IllegalArgumentException {
        if (weightInKg < 40 || weightInKg > 600) {
            throw new IllegalArgumentException("Weight must be between 40 and 600");
        }
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, srcActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("weight", weightInKg);
        editor.commit();
    }
    public void setPersonAge(int age) throws IllegalArgumentException {
        if (age < 18 || age > 150) {
            throw new IllegalArgumentException("Age must be between 18 and 150");
        }
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, srcActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("age", age);
        editor.commit();
    }
    public void setPersonEnergyReq(int energyReq) throws IllegalArgumentException {
        if (energyReq < 1000) {
            throw new IllegalArgumentException("Energy target must be above 1000kcal");
        }
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, srcActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("energyReq", energyReq);
        editor.commit();
    }
    public void setPersonHeight(int sizeInCm) throws IllegalArgumentException {
        if (sizeInCm < 0 || sizeInCm > 300) {
            throw new IllegalArgumentException("Height must be between 0 and 300 cm");
        }
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, srcActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("height", sizeInCm);
        editor.commit();
    }
    public void setPersonGender(String gender) throws IllegalArgumentException {
        if (!gender.equals("male") && !gender.equals("female")) {
            throw new IllegalArgumentException("Gender must be 'male' or 'female'.");
        }
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, srcActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("gender", gender);
        editor.commit();
    }
    public int getPersonWeight() {
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, srcActivity.MODE_PRIVATE);
        int weight = pref.getInt("weight", -1);
        return weight;
    }

    public int getPersonHeight() {
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, srcActivity.MODE_PRIVATE);
        int height = pref.getInt("height", -1);
        return height;
    }
    public int getPersonAge() {
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, srcActivity.MODE_PRIVATE);
        int age = pref.getInt("age", -1);
        return age;
    }
    public int getPersonEnergyReq() {
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, srcActivity.MODE_PRIVATE);
        int energyReq = pref.getInt("energyReq", -1);
        if (energyReq == -1) {
            energyReq = 2000; //TODO calc from other values
        }
        return energyReq;
    }
    public String getPersonGender() {
        SharedPreferences pref = srcActivity.getApplicationContext().getSharedPreferences(FILE_KEY, srcActivity.MODE_PRIVATE);
        String gender = pref.getString("gender", "none");
        return gender;
    }

    public void close() {
        db.close();
    }
}