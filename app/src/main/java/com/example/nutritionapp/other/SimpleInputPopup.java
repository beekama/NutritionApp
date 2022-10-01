package com.example.nutritionapp.other;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.nutritionapp.R;

public class SimpleInputPopup extends Dialog {


    private final int inputType;
    private final String inputTitle;
    private final String inputLabel;

    public String stringValue = null;
    public double numberValue = Double.NaN;

    public SimpleInputPopup(@NonNull Context context, Database db, String inputTitle, String inputLabel, int inputType) {
        super(context);
        this.inputType = inputType;
        this.inputTitle = inputTitle;
        this.inputLabel = inputLabel;
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.other_simple_input);

        EditText inputEditText = findViewById(R.id.input);
        TextView inputLabelView = findViewById(R.id.inputLabel);
        TextView inputTitleView = findViewById(R.id.inputTitle);

        inputTitleView.setText(inputTitle);
        inputLabelView.setText(inputLabel);
        inputEditText.setInputType(inputType);
        inputEditText.setInputType(inputType);

        Button cancel = findViewById(R.id.cancelButton);
        cancel.setText(R.string.textCancel);
        cancel.setOnClickListener(v -> {
            this.cancel();
        });

        Button confirm = findViewById(R.id.confirmButton);
        confirm.setText(R.string.textConfirm);
        confirm.setOnClickListener(v -> {
            this.dismiss();
        });
    }

    public String getStringValue() {
        if(Double.isNaN(numberValue)){
            return stringValue;
        }else{
            return Double.toString(numberValue);
        }
    }
}
