package com.example.nutritionapp.ui;

import static com.example.nutritionapp.other.Utils.getStringIdentifier;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModel;

import com.example.nutritionapp.R;
import com.example.nutritionapp.customFoods.CreateFoodNutritionSelectorItem;
import com.example.nutritionapp.other.ActivityExtraNames;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionElement;
import com.example.nutritionapp.other.Utils;

import java.util.ArrayList;
import java.util.Collections;

public class CreateFoodItemViewModel extends ViewModel {
    private Database db;
    private Fragment fragment;
    private Bundle fragmentArgs;

    Nutrition n;
    private ArrayList<CreateFoodNutritionSelectorItem> staticSelectors;
    private ArrayList<CreateFoodNutritionSelectorItem> nutritionSelectors;

    private static final int CREATE_NEW_ID = -1;
    private final ArrayList<CreateFoodNutritionSelectorItem> allItems = new ArrayList<>();
    private int servingSize;
    private boolean editMode;
    private Food editModeOrigFood;
    private Food editFood = null;

    public CreateFoodItemViewModel(Database db, Fragment fragment, Bundle args){
        this.db = db;
        this.fragment = fragment;
        this.fragmentArgs = args;
        onCreate();
    }

    public void onCreate(){
        Log.wtf("WTF", "oncreateviewmodel");
        cloneFoodForEdit(fragmentArgs);
        staticSelectors = createStaticSelector();
        nutritionSelectors = createNutritionSelectors();
        allItems.addAll(staticSelectors);
        allItems.addAll(nutritionSelectors);
    }

    public ArrayList<CreateFoodNutritionSelectorItem> getAllItems(){
        return allItems;
    }

    public Database getDb(){
        return db;
    }

    public ArrayList<CreateFoodNutritionSelectorItem> getStaticSelectors(){
        return staticSelectors;
    }

    public void cloneFoodForEdit(Bundle args){
        String fdc_id;
        if (args != null && (fdc_id = args.getString(ActivityExtraNames.FDC_ID)) != null){
            this.editMode = true;
            editFood = db.getFoodById(fdc_id, null);
            if (editFood == null){
                throw new AssertionError("DB Return null for existing custom food id: " + fdc_id);
            }
            editModeOrigFood = editFood.deepclone();
            n = editFood.nutrition;
        } else {
            n = new Nutrition();
        }
    }

    private ArrayList<CreateFoodNutritionSelectorItem> createStaticSelector(){
        ArrayList<CreateFoodNutritionSelectorItem> staticSelectors = new ArrayList<>();
        int stringID = R.string.generalFoodInformationHeader;
        staticSelectors.add(new CreateFoodNutritionSelectorItem(stringID, new SpannableString(fragment.getString(stringID)), true));
        /* Spannables */
        int stringIdServingSize = R.string.servingSizeInGramLabel;
        Spannable servingSizeSpan = new SpannableString(fragment.getString(stringIdServingSize));
        servingSizeSpan.setSpan(new RelativeSizeSpan(0.8f), 13, 20, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        int stringIdEnergy = R.string.energyInKcalLabel;
        SpannableString energySpanString = new SpannableString(fragment.getString(stringIdEnergy));
        energySpanString.setSpan(new RelativeSizeSpan(0.8f), 7, 14, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        int stringIdFiber = R.string.fiberInGramLabel;
        SpannableString fiberSpanString = new SpannableString(fragment.getString(stringIdFiber));
        fiberSpanString.setSpan(new RelativeSizeSpan(0.8f), 6, 13, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        int stringIdName = R.string.labelFoodName;
        if(editMode && editFood != null){
            staticSelectors.add(new CreateFoodNutritionSelectorItem(stringIdName, new SpannableString(fragment.getString(stringIdName)), editFood.name, true, false));
            CreateFoodNutritionSelectorItem servingSize = new CreateFoodNutritionSelectorItem(db, stringIdEnergy, servingSizeSpan, 100, false, false);
            staticSelectors.add(servingSize);
            CreateFoodNutritionSelectorItem energyItemEdit = new CreateFoodNutritionSelectorItem(db, stringIdEnergy, energySpanString, editFood.energy, false, false);
            CreateFoodNutritionSelectorItem fiberItemEdit = new CreateFoodNutritionSelectorItem(db, stringIdFiber, fiberSpanString, editFood.fiber, false, false);
            staticSelectors.add(energyItemEdit);
            staticSelectors.add(fiberItemEdit);
        }else {
            staticSelectors.add(new CreateFoodNutritionSelectorItem(stringIdName, new SpannableString(fragment.getString(stringIdName)), true, false));
            staticSelectors.add(new CreateFoodNutritionSelectorItem(stringIdServingSize, servingSizeSpan, false, false));
            CreateFoodNutritionSelectorItem energyItem = new CreateFoodNutritionSelectorItem(stringIdEnergy, energySpanString, false, false);
            CreateFoodNutritionSelectorItem fiberItem = new CreateFoodNutritionSelectorItem(stringIdFiber, fiberSpanString, false, false);
            staticSelectors.add(energyItem);
            staticSelectors.add(fiberItem);
        }
        int stringIdCreateNutritionHeader = R.string.createFoodHeaderNutrients;
        staticSelectors.add(new CreateFoodNutritionSelectorItem(stringIdCreateNutritionHeader, new SpannableString(fragment.getString(stringIdCreateNutritionHeader)), true));

        return staticSelectors;
    }

    private ArrayList<CreateFoodNutritionSelectorItem> createNutritionSelectors(){
        ArrayList<CreateFoodNutritionSelectorItem> nutritionSelectors = new ArrayList<>();
        for (NutritionElement ne : n.getElements().keySet()) {
            int stringIdNutritionElement = getStringIdentifier(fragment.requireContext(), ne.toString());
            String neString = fragment.getString(stringIdNutritionElement);
            String noStrPortionType = Database.getNutrientNativeUnit(Integer.toString(Nutrition.databaseIdFromEnum(ne)));
            String portionType =  fragment.getString(getStringIdentifier(fragment.requireContext(), noStrPortionType));
            int startPtString = neString.length() + 1;
            int endPtString = startPtString + 4 + portionType.length() - 1;
            Spannable neSpan = new SpannableString(neString + " in " + portionType);
            neSpan.setSpan(new RelativeSizeSpan(0.8f), startPtString, endPtString, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

            if(this.editMode){
                Integer presetAmount = n.getElements().get(ne);
                nutritionSelectors.add(new CreateFoodNutritionSelectorItem(db, stringIdNutritionElement, neSpan, Utils.zeroIfNull(presetAmount), false, false));
            }else{
                nutritionSelectors.add(new CreateFoodNutritionSelectorItem(db, stringIdNutritionElement, neSpan, 0, false, false));
            }
        }
        Collections.sort(nutritionSelectors);
        return nutritionSelectors;
    }

    public void submitForm(){
        Food f = collectData();
        if (f == null) {
            return;
        }
        if(this.editMode){
            f.id = editModeOrigFood.id;
            db.changeCustomFood(this.editModeOrigFood, f);
        }else {
            db.createNewFood(f, CREATE_NEW_ID);
        }
    }

    private Food collectData() {
        /* this function is sensitive to the correct ordering of the array list
           must be: name-> serving size -> everything else
         */
        Nutrition n = new Nutrition();
        Food f = new Food(null, null);
        f.nutrition = n;
        for (CreateFoodNutritionSelectorItem item : allItems) {
            if (item.ne != null) {
                if(item.amount == -1){
                    continue;
                }
                int servingSizeTmp = 1;
                if(this.servingSize != 0){
                    servingSizeTmp = servingSize;
                }
                f.nutrition.getElements().put(item.ne, item.amount / servingSizeTmp);
            } else {
                if(item.amount == -1){
                    item.amount = 0;
                }

                switch (item.stringID) {
                    case R.string.servingSizeInGramLabel:
                        this.servingSize = item.amount;  /* next level hack */
                        break;
                    case R.string.energyInKcalLabel:
                        f.energy = item.amount;
                        break;
                    case R.string.fiberInGramLabel:
                        f.fiber = item.amount;
                        break;
                    case R.string.labelFoodName:
                        if (item.data == null || item.data.equals("")) {
                            Toast toast = Toast.makeText(fragment.requireContext(), "Name must be set.", Toast.LENGTH_LONG);
                            toast.show();
                            return null;
                        }else if (editModeOrigFood == null && db.checkCustomNameFoodExists(item.data)) {
                            Toast toast = Toast.makeText(fragment.requireContext(), "A food with this name already exists.", Toast.LENGTH_LONG);
                            toast.show();
                            return null;
                        } else {
                            f.name = item.data;
                        }
                        break;
                }
            }
        }
        f.nutrition = n;
        return f;
    }

}
