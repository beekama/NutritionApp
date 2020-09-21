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
import android.widget.Toast;

import com.example.nutritionapp.NutritionOverview.NutritionOverview;
import com.example.nutritionapp.R;
import com.example.nutritionapp.foodJournal.AddFoodsLists.SelectedFoodItem;
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
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.create_food_nutrition_selection_item, parent, false);
        }

        /* get relevant sub-views */
        TextView name = convertView.findViewById(R.id.name);
        EditText value = convertView.findViewById(R.id.value);
        if(item.inputTypeString){
            value.setInputType(EditText.AUTOFILL_TYPE_TEXT);
        }

        name.setText(item.tag);
        if(item.amount >= 0) {
            value.setText(item.amount);
        }else if(item.data != null){
            value.setText(item.data);
        }else{
            value.setText("");
        }
        value.setHint(item.unit);

        value.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                return;
            }
            String cur = value.getText().toString();
            if(cur.equals("")){
                return;
            }
            try{
                if(item.inputTypeString){
                    item.data = value.getText().toString();
                }else {
                    item.amount = Integer.parseInt(cur);
                }
            }catch(NumberFormatException e){
                Log.e("ERROR", cur);
                Toast toast = Toast.makeText(context, "Amount is not a Number when it should be.", Toast.LENGTH_LONG);
                toast.show();
            }
            return;
        });

        return convertView;
    }
}
