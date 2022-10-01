package com.example.nutritionapp.other;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
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

    public SimpleInputPopup(@NonNull Context context, String inputTitle, String inputLabel, int inputType) {
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

        inputTitleView.setText(Utils.capitalize(inputTitle, true));
        inputLabelView.setText(Utils.capitalize(inputLabel, true));
        inputEditText.setInputType(inputType);
        inputEditText.setInputType(inputType);

        inputEditText.setOnEditorActionListener((v, action, event) -> {
            if (action == EditorInfo.IME_ACTION_DONE) {
                if(inputType == InputType.TYPE_CLASS_NUMBER){
                    this.numberValue = Integer.parseInt(v.getText().toString());
                }
                this.stringValue = v.getText().toString();
                this.dismiss();
                return true;
            }
            return false;
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
