package com.example.myapplication.ButtonUtils;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class UnfocusOnEnter implements View.OnKeyListener {
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            v.clearFocus();
            return true;
        }else{
            return false;
        }
    }
}