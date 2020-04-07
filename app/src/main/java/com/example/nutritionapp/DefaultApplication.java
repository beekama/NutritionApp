package com.example.nutritionapp;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

public class DefaultApplication extends Application {
    public void onCreate() {
        super.onCreate();

        /* necessary to initialize the local timezone */
        AndroidThreeTen.init(this);
    }
}
