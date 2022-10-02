package com.example.nutritionapp.other;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LocaleHelper {
    private static final String SELECTED_LANGUAGE = "Locale.Helper.SelectedLanguage";

    public static Context setDefaultLanguage(Context context){
        String language = getStoredData(context, Locale.getDefault().getLanguage());
        return setLocale(context, language);
    }

    public  static Context setDefaultLanguage(Context context, String defaultLanguage){
        String language = getStoredData(context, defaultLanguage);
        return setLocale(context, language);
    }

    public static String getLanguage(Context context){
        return getStoredData(context, Locale.getDefault().getLanguage());
    }

    public static Context setLocale(Context context, String language){
        saveLocaleChanges(context, language);
        return  updateResources(context, language);
    }

    private static void saveLocaleChanges(Context context, String language){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_LANGUAGE, language);
        editor.apply();
    }

    private static String getStoredData(Context context, String defaultLanguage){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage);
    }

    private static Context updateResources(Context context, String language){
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }
}
