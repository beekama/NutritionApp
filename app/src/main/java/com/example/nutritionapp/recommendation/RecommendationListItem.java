package com.example.nutritionapp.recommendation;

import android.app.Notification;

import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

class RecommendationListItem {
    final String tag;
    float percentage;
    public PieDataSet pieDataSet;
    ArrayList<PieEntry> pieEntryList = new ArrayList<>();

    public RecommendationListItem(String tag, float percentage) {
        this.tag = tag;
        this.percentage = percentage;
        this.pieEntryList.add(new PieEntry(percentage, tag));
        if (percentage < 100) {
            this.pieEntryList.add(new PieEntry(100 - percentage, "missing"));
        }
        this.pieDataSet = new PieDataSet(pieEntryList, "alldata");
    }
}
