package com.example.nutritionapp.foodJournal.overviewFoodsLists;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Conversions;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionPercentageTuple;

import java.util.ArrayList;
import java.util.Collections;

public class NutritionOverviewAdapter extends BaseAdapter {

    private final Nutrition nutritionActual;
    private final ArrayList<NutritionPercentageTuple> nutritionPercentageSortedFilterZero;
    private final Activity context;

    public NutritionOverviewAdapter(Activity context, Nutrition nutritionActual, ArrayList<NutritionPercentageTuple> nutritionPercentageSortedFilterZero) {
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
        int viewType = (getItemViewType(position));
        if(convertView != null){
            return convertView;
        }else {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.journal_nutrition_overview_item, parent, false);
            TextView nutritionName = convertView.findViewById(R.id.nutritionName);
            TextView progressBarLabel = convertView.findViewById(R.id.progressBarText);
            ProgressBar progressBar = convertView.findViewById(R.id.progressBar);

            NutritionPercentageTuple el = this.nutritionPercentageSortedFilterZero.get(position);
            nutritionName.setText(el.nutritionElement.getString(context));


            String pgContentValue = String.valueOf(nutritionActual.getElements().get(el.nutritionElement));
            String pgContentUnit = Conversions.getNativeUnitForNutritionElementUnsafe(el.nutritionElement);
            /* FIXME: idk make 2 text views or string-builder or something */
            progressBarLabel.setText(pgContentValue + pgContentUnit);

            progressBar.setMax(100);
            progressBar.setMin(0);
            progressBar.setProgress((int) el.percentage);
        }

        return convertView;
    }
}
