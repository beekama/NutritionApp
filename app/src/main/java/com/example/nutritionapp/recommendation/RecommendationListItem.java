package com.example.nutritionapp.recommendation;

import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionElement;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

class RecommendationListItem {

    //final float percentage;
   // public final PieDataSet pieDataSet;
    //final ArrayList<PieEntry> pieEntryList = new ArrayList<>();

    final NutritionElement nutritionElement;
    final Float percentage;
    final Integer target;

    public RecommendationListItem(NutritionElement nutritionElement, Float percentage, Integer target) {
        this.nutritionElement = nutritionElement;
        this.percentage = percentage;
        this.target = target;
      //  this.percentage = percentage;
       /* this.pieEntryList.add(new PieEntry(percentage,nutritionElement.toString()));
        if (percentage < 100) {
            this.pieEntryList.add(new PieEntry(100 - percentage, "missing"));
        }*/
       // this.pieDataSet = new PieDataSet(pieEntryList, "alldata");
    }
}
