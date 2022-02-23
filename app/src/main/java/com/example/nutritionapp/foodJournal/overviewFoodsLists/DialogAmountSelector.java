package com.example.nutritionapp.foodJournal.overviewFoodsLists;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
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

    public PortionTypes typeSelected;
    public float amountSelected;
    final Food selectedFood;
    final Activity context;
    final Database db;
    ListView nutOverviewList;

    public DialogAmountSelector(@NonNull Activity context, Database db, Food selectedFood) {
        super(context);
        this.context = context;
        this.db = db;
        this.selectedFood = selectedFood;
        this.typeSelected = selectedFood.getAssociatedPortionType();
        this.amountSelected = selectedFood.getAssociatedAmount();
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
        cancel.setOnClickListener(v -> {
            amountSelected = 0f;
            this.cancel();
        });

        Button confirm = findViewById(R.id.confirmButton);
        confirm.setText(R.string.textConfirm);
        confirm.setOnClickListener(v -> {
            getValues();
            updateNutritionOverview(amountSelected, typeSelected);
            this.dismiss();
        });

        RecyclerView portionTypeSelector = findViewById(R.id.portionTypeSelector);
        RecyclerView amountSelector = findViewById(R.id.amountSelector);

        LinearLayoutManager portionSelectorLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager amountSelectorLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        portionTypeSelector.setLayoutManager(portionSelectorLayoutManager);
        amountSelector.setLayoutManager(amountSelectorLayoutManager);

        //update amountSelector if selectedPortion has changed:
        //portionTypeSelector.setOnClickListener( v -> updateAmountSelectorItems);


        ArrayList<Float> amountOptions = new ArrayList<>();
        amountOptions.add(0.125f);
        amountOptions.add(0.25f);
        amountOptions.add(0.5f);
        amountOptions.add(0.75f);
        amountOptions.add(1f);
        amountOptions.add(1.5f);
        amountOptions.add(2f);
        amountOptions.add(2.5f);
        amountOptions.add(3f);
        amountOptions.add(3.5f);
        amountOptions.add(4f);
        amountOptions.add(4.5f);
        amountOptions.add(5f);
        amountOptions.add(5.5f);
        amountOptions.add(6f);
        amountOptions.add(6.5f);
        amountOptions.add(7f);
        amountOptions.add(8f);
        amountOptions.add(9f);
        amountOptions.add(10f);
        amountOptions.add(11f);
        amountOptions.add(12f);
        amountOptions.add(14f);
        amountOptions.add(16f);
        amountOptions.add(18f);
        amountOptions.add(20f);
        amountOptions.add(22f);
        amountOptions.add(24f);
        amountOptions.add(26f);
        amountOptions.add(28f);
        amountOptions.add(30f);
        amountOptions.add(32f);
        amountOptions.add(40f);
        amountOptions.add(50f);
        amountOptions.add(60f);
        amountOptions.add(70f);
        amountOptions.add(80f);
        amountOptions.add(90f);
        amountOptions.add(100f);
        amountOptions.add(200f);
        amountOptions.add(500f);
        amountOptions.add(1000f);


        ArrayList<PortionTypes> portionOptions = db.portionsForFood(new Food(selectedFood.name,selectedFood.id));
        RecyclerView.Adapter<?> portionSelectorAdapter = new SelectorDialogAdapterPortions(context, portionOptions, this, selectedFood.associatedPortionType);
        RecyclerView.Adapter<?> amountSelectorAdapter = new SelectorDialogAdapterAmount(context, amountOptions, this, selectedFood.associatedAmount);

        portionTypeSelector.setAdapter(portionSelectorAdapter);
        amountSelector.setAdapter(amountSelectorAdapter);


    }

    @Override
    public void onBackPressed() {
        amountSelected = 0f;
        this.cancel();
    }

    private void updateNutritionOverview(float amount, PortionTypes portionType) {
        selectedFood.setAssociatedAmount(amount);
        selectedFood.setAssociatedPortionType(portionType);
        selectedFood.setNutritionFromDb(db);
        ArrayList<Food> analysis = new ArrayList<>();
        analysis.add(selectedFood);
        NutritionAnalysis na = new NutritionAnalysis(analysis);
        ListAdapter nutOverviewAdapter = new NutritionOverviewAdapter(context, na.getNutritionActual(), na.getNutritionPercentageSortedFilterZero());
        nutOverviewList.setAdapter(nutOverviewAdapter);
    }

    @Override
    public void setValues(PortionTypes p) {
        typeSelected = p;}

    @Override
    public void getValues() {
    }

    @Override
    public void setValues(Float a) {
        amountSelected = a;
    }
}

