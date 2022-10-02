package com.example.nutritionapp;

import android.app.Application;
import android.content.Context;

import com.example.nutritionapp.other.LocaleHelper;


public class DefaultApplication extends Application {

    @Override
    protected void attachBaseContext(Context base){
        super.attachBaseContext(LocaleHelper.setDefaultLanguage(base, "de"));
    }

}
