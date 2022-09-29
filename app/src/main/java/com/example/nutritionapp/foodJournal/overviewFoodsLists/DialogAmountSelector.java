package com.example.nutritionapp.foodJournal.overviewFoodsLists;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.example.nutritionapp.other.Conversions;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.PortionType;
import com.example.nutritionapp.other.Utils;

import java.util.ArrayList;

public class DialogAmountSelector extends Dialog implements  DataTransfer{

    public PortionType typeSelected;
    public double amountSelected;
    final Food selectedFood;
    final Activity context;
    final Database db;
    ListView nutOverviewList;
    TextView estimatedAmount;
    TextView estimatedAmountLabel;

    public RecyclerView.Adapter<?> amountSelectorAdapter;
    public RecyclerView.Adapter<?> portionSelectorAdapter;

    public RecyclerView portionTypeSelectorView;
    public RecyclerView amountSelectorView;


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
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.journal_dialog_amout_selector);

        TextView selectedFoodView = findViewById(R.id.foodName);
        estimatedAmount = findViewById(R.id.amountInGramEstimate);
        estimatedAmountLabel = findViewById(R.id.amountInGramLabel);
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

        portionTypeSelectorView = findViewById(R.id.portionTypeSelector);
        amountSelectorView = findViewById(R.id.amountSelector);

        LinearLayoutManager portionSelectorLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager amountSelectorLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        portionTypeSelectorView.setLayoutManager(portionSelectorLayoutManager);
        amountSelectorView.setLayoutManager(amountSelectorLayoutManager);

        //update amountSelector if selectedPortion has changed:
        //portionTypeSelector.setOnClickListener( v -> updateAmountSelectorItems);


        ArrayList<Double> amountOptions = Utils.amountsForPortionType(this.typeSelected);

        ArrayList<PortionType> portionOptions = db.portionsForFood(new Food(selectedFood.name, selectedFood.id));
        portionSelectorAdapter = new SelectorDialogAdapterPortions(context, portionOptions, this, selectedFood.associatedPortionType);
        amountSelectorAdapter = new SelectorDialogAdapterAmount(context, amountOptions, this, selectedFood.associatedAmount);

        portionTypeSelectorView.setAdapter(portionSelectorAdapter);
        amountSelectorView.setAdapter(amountSelectorAdapter);


        /* scroll to targets */
        SelectorDialogAdapterAmount ad = (SelectorDialogAdapterAmount) amountSelectorAdapter;
        SelectorDialogAdapterPortions pd = (SelectorDialogAdapterPortions) portionSelectorAdapter;
        int targetPositionAmount = ad.findValuePositionInItems(amountSelected);

        amountSelectorView.smoothScrollToPosition(targetPositionAmount);
        int targetPositionPortion = pd.findPortionPositionInItems(typeSelected);
        portionTypeSelectorView.smoothScrollToPosition(targetPositionPortion);

        notifyEstimatedAmountChanged();
    }

    @Override
    public void onBackPressed() {
        amountSelected = 0f;
        this.cancel();
    }

    private void updateNutritionOverview(double amount, PortionType portionType) {
        selectedFood.setAssociatedAmount(amount);
        selectedFood.setAssociatedPortionType(portionType);
        selectedFood.setPortionTypeInGram(db.getPortionToGramRatio(selectedFood, portionType));
        selectedFood.setNutritionFromDb(db);
        ArrayList<Food> analysis = new ArrayList<>();
        analysis.add(selectedFood);
        NutritionAnalysis na = new NutritionAnalysis(analysis);
        ListAdapter nutOverviewAdapter = new NutritionOverviewAdapter(context, na.getNutritionActual(), na.getNutritionPercentageSortedFilterZero());
        nutOverviewList.setAdapter(nutOverviewAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyPortionTypeChanged(){
        SelectorDialogAdapterAmount s = (SelectorDialogAdapterAmount) amountSelectorAdapter;
        s.setItems(Utils.amountsForPortionType(this.typeSelected));
        s.notifyDataSetChanged(); /* dataset 100% change */
    }

    @Override
    public void setPortionType(PortionType p) {
        typeSelected = p;
        notifyPortionTypeChanged();
        notifyEstimatedAmountChanged();
    }

    private void notifyEstimatedAmountChanged() {
        if(this.typeSelected == PortionType.GRAM){
            estimatedAmountLabel.setVisibility(View.INVISIBLE);
            estimatedAmount.setVisibility(View.INVISIBLE);
        }else {
            estimatedAmount.setVisibility(View.VISIBLE);
            estimatedAmountLabel.setVisibility(View.VISIBLE);
            double size = db.getPortionToGramRatio(selectedFood, this.typeSelected) * this.amountSelected;
            estimatedAmount.setText(Integer.toString((int)size));
        }
    }

    @Override
    public void getValues() {
    }

    @Override
    public void setAmountSelected(double a) {
        amountSelected = a;
        notifyEstimatedAmountChanged();
    }
}

