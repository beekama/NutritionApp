package com.example.nutritionapp.foodJournal.OverviewFoodsLists;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.PortionTypes;

public class DialogAmountSelector extends Dialog {

    public PortionTypes typeSelected = null;
    public int amountSelected = 0;
    Activity context;

    public DialogAmountSelector(@NonNull Activity context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.journal_dialog_amout_selector);

        TextView nutOverviewPh = findViewById(R.id.nutritionOverview);
        nutOverviewPh.setText("PLACEHOLDER PLACERHOLDER PLACEHOLDER");

        Button cancel = findViewById(R.id.cancelButton);
        cancel.setText("Cancel");
        cancel.setOnClickListener(v -> this.dismiss());

        Button confirm = findViewById(R.id.confirmButton);
        confirm.setText("Confirm");
        confirm.setOnClickListener(v -> this.dismiss());

        HorizontalScrollView portionTypeSelector = findViewById(R.id.portionTypeSelector);
        HorizontalScrollView amountSelector = findViewById(R.id.amountSelector);
    }
}
