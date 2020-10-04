package com.example.nutritionapp.recommendation;

import android.app.Notification;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.NutritionElement;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

class RecommendationListItem {
    NutritionElement nutritionElement;
    float percentage;
    public PieDataSet pieDataSet;
    ArrayList<PieEntry> pieEntryList = new ArrayList<>();

    public RecommendationListItem(NutritionElement nutritionElement, float percentage) {
        this.nutritionElement = nutritionElement;
        this.percentage = percentage;
        this.pieEntryList.add(new PieEntry(percentage, nutritionElement.toString()));
        if (percentage < 100) {
            this.pieEntryList.add(new PieEntry(100 - percentage, "missing"));
        }
        this.pieDataSet = new PieDataSet(pieEntryList, "alldata");
    }
}
