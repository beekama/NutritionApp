package com.example.nutritionapp.configuration;

public class ConfigurationListItem {
    final PersonalInformation.DataType type;
    final String text;
    public double numberValue;
    String stringValue = "";
    Boolean bValue = false;

    public ConfigurationListItem(PersonalInformation.DataType type, String text, String stringValue) {
        this.type = type;
        this.text = text;
        this.stringValue = stringValue;
    }


    public ConfigurationListItem(PersonalInformation.DataType type, String text, Boolean bValue) {
        this.type = type;
        this.text = text;
        this.bValue = bValue;
    }
}
