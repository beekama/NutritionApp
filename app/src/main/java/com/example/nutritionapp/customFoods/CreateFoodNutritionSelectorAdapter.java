package com.example.nutritionapp.customFoods;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nutritionapp.NutritionOverview.NutritionOverview;
import com.example.nutritionapp.R;
import com.example.nutritionapp.foodJournal.OverviewFoodsLists.GroupFoodItem;
import com.example.nutritionapp.foodJournal.OverviewFoodsLists.GroupListAdapter;
import com.example.nutritionapp.other.Conversions;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.NutritionPercentageTupel;

import java.util.ArrayList;

public class CreateFoodNutritionSelectorAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CreateFoodNutritionSelectorItem> items;

    public CreateFoodNutritionSelectorAdapter(Context context, ArrayList<CreateFoodNutritionSelectorItem> items){
        this.context = context;
        this.items   = items;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /* item at position */
        CreateFoodNutritionSelectorItem item = this.items.get(position);

        /* inflate layout */
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.create_food_nutrition_selection_item, parent, false);

        /* get relevant sub-views */
        TextView name = convertView.findViewById(R.id.name);
        EditText value = convertView.findViewById(R.id.value);
        if(item.inputTypeString){
            value.setInputType(EditText.AUTOFILL_TYPE_TEXT);
        }

        name.setText(item.tag);
        value.setHint(item.unit);

        return convertView;
    }
}
