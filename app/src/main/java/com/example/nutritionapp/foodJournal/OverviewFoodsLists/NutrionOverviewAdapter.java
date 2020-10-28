package com.example.nutritionapp.foodJournal.OverviewFoodsLists;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionElement;
import com.example.nutritionapp.other.NutritionPercentageTupel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class NutrionOverviewAdapter extends BaseAdapter {

    private final Nutrition nutritionActual;
    private final ArrayList<NutritionPercentageTupel> nutritionPercentageSortedFilterZero;
    private final Activity context;

    public NutrionOverviewAdapter(Activity context, Nutrition nutritionActual, ArrayList<NutritionPercentageTupel> nutritionPercentageSortedFilterZero) {
        this.context = context;
        this.nutritionActual = nutritionActual;
        this.nutritionPercentageSortedFilterZero = nutritionPercentageSortedFilterZero;
        Collections.reverse(nutritionPercentageSortedFilterZero);
    }

    @Override
    public int getCount() {
        return nutritionPercentageSortedFilterZero.size();
    }

    @Override
    public Object getItem(int position) {
        return nutritionPercentageSortedFilterZero.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView != null){
            return convertView;
        }else{
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.journal_nutrition_overview_item, parent, false);
        }

        TextView nutritionName = convertView.findViewById(R.id.nutritionName);
        TextView progressBarLabel = convertView.findViewById(R.id.progressBarText);
        ProgressBar progressBar = convertView.findViewById(R.id.progressBar);

        NutritionPercentageTupel el = this.nutritionPercentageSortedFilterZero.get(position);
        nutritionName.setText(el.nutritionElement.toString());
        progressBarLabel.setText(Objects.requireNonNull(nutritionActual.getElements().get(el.nutritionElement)).toString());

        progressBar.setMax(100);
        progressBar.setMin(0);
        progressBar.setProgress((int)el.percentage);

        return convertView;
    }
}
