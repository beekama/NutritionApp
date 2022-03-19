package com.example.nutritionapp.configuration;

import com.example.nutritionapp.other.NutritionElement;

public class ConfigurationListItem {
    final PersonalInformation.DataType type;
    final String text;
    String sValue = "";
    Boolean bValue = false;

    public ConfigurationListItem(PersonalInformation.DataType type, String text, String sValue) {
        this.type = type;
        this.text = text;
        this.sValue = sValue;
    }


    public ConfigurationListItem(PersonalInformation.DataType type, String text, Boolean bValue) {
        this.type = type;
        this.text = text;
        this.bValue = bValue;
    }
}
