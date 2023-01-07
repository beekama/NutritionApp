package com.example.nutritionapp.configuration;

import com.example.nutritionapp.ui.ConfigurationFragment;

public class ConfigurationListItem {
    final ConfigurationFragment.DataType type;
    final String text;
    public double numberValue;
    String stringValue = "";
    Boolean bValue = false;

    public ConfigurationListItem(ConfigurationFragment.DataType type, String text, String stringValue) {
        this.type = type;
        this.text = text;
        this.stringValue = stringValue;
    }


    public ConfigurationListItem(ConfigurationFragment.DataType type, String text, Boolean bValue) {
        this.type = type;
        this.text = text;
        this.bValue = bValue;
    }
}
