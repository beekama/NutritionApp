package com.example.nutritionapp.ButtonUtils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import static androidx.core.content.ContextCompat.getSystemService;

public class HideKeyboardOnFocusLoss implements View.OnFocusChangeListener {

    public void onFocusChange(View v, boolean hasFocus){
        if(!hasFocus) {
            InputMethodManager imm =  (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
