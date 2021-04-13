package com.example.nutritionapp.foodJournal.overviewFoodsLists;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.PortionTypes;

import java.util.ArrayList;

public class DialogAmountSelector extends Dialog implements  DataTransfer{

    public PortionTypes typeSelected = null;
    public int amountSelected = 0;
    final Food selectedFood;
    final Activity context;
    final Database db;
    ListView nutOverviewList;

    public DialogAmountSelector(@NonNull Activity context, Database db, Food selectedFood) {
        super(context);
        this.context = context;
        this.db = db;
        this.selectedFood = selectedFood;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.journal_dialog_amout_selector);

        TextView selectedFoodView = findViewById(R.id.foodName);
        selectedFoodView.setText(selectedFood.name);
        nutOverviewList = findViewById(R.id.nutritionOverview);

        Button cancel = findViewById(R.id.cancelButton);
        cancel.setText(R.string.textCancel);
        cancel.setOnClickListener(v -> this.dismiss());

        Button confirm = findViewById(R.id.confirmButton);
        confirm.setText(R.string.textConfirm);
        //confirm.setOnClickListener(v -> this.dismiss());


        RecyclerView portionTypeSelector = findViewById(R.id.portionTypeSelector);
        RecyclerView amountSelector = findViewById(R.id.amountSelector);

        LinearLayoutManager portionSelectorLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager amountSelectorLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        portionTypeSelector.setLayoutManager(portionSelectorLayoutManager);
        amountSelector.setLayoutManager(amountSelectorLayoutManager);

        //update amountSelector if selectedPortion has changed:
        //portionTypeSelector.setOnClickListener( v -> updateAmountSelectorItems);


        ArrayList<Integer> amountOptions = new ArrayList<>();
        amountOptions.add(1);
        amountOptions.add(10);
        amountOptions.add(50);
        amountOptions.add(100);

        ArrayList<PortionTypes> portionOptions = db.portionsForFood(new Food(selectedFood.name,selectedFood.id));
        RecyclerView.Adapter<?> portionSelectorAdapter = new SelectorDialogAdapterPortions(context, portionOptions, this  );
        RecyclerView.Adapter<?> amountSelectorAdapter = new SelectorDialogAdapterAmount(context, amountOptions);

        portionTypeSelector.setAdapter(portionSelectorAdapter);
        amountSelector.setAdapter(amountSelectorAdapter);



       //updateNutritionOverview(100);
    }

    private void updateNutritionOverview(int amount) {
        selectedFood.setAssociatedAmount(amount);
        selectedFood.setNutritionFromDb(db);
        ArrayList<Food> analysis = new ArrayList<>();
        analysis.add(selectedFood);
        NutritionAnalysis na = new NutritionAnalysis(analysis);
        ListAdapter nutOverviewAdapter = new NutritionOverviewAdapter(context, na.getNutritionActual(), na.getNutritionPercentageSortedFilterZero());
        nutOverviewList.setAdapter(nutOverviewAdapter);
    }

    @Override
    public void setValues(PortionTypes p) {
        typeSelected = p;
        Log.wtf("shebaaang", this.typeSelected.toString()) ;}
}

// create two arraylists for two bigportiontypes
// onamount selector give portionlist accorting to a
// update amount selecto in setValues