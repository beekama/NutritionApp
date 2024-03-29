package com.example.nutritionapp.foodJournal.addFoodsLists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.nutritionapp.R;

import java.util.ArrayList;
import java.util.EventListener;

import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.PortionType;
import com.example.nutritionapp.other.Utils;

public class SelectedFoodAdapter extends BaseAdapter {

    private final Context context;
    public final ArrayList<SelectedFoodItem> items;
    EventListener amountListener;

    public SelectedFoodAdapter(Context context, ArrayList<SelectedFoodItem> items, EventListener listener){
        this.context=context;
        this.items=items;
        this.amountListener = listener;
    }

    public interface EventListener{
        void amountSelectionEvent(Food food, int position, PortionType portionType, double amountAssociated);
    }


    public int getCount() {
        return items.size();
    }

    public Object getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SelectedFoodItem currentItem = items.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.journal_add_food_selected_item, parent, false);
            TextView nameView = convertView.findViewById(R.id.item_name);
            Button amountSelectorButton = convertView.findViewById(R.id.amount_selector);
            Button portionSelectorButton = convertView.findViewById(R.id.portion_type_selector);

            /* identical button functions for amount & portion type (same dialog) */
            amountSelectorButton.setOnClickListener(v -> {
                SelectedFoodItem item = items.get(position);
                amountListener.amountSelectionEvent(item.food, position, item.food.associatedPortionType, item.food.associatedAmount);
            });
            portionSelectorButton.setOnClickListener(v -> {
                SelectedFoodItem item = items.get(position);
                amountListener.amountSelectionEvent(item.food, position, item.food.associatedPortionType, item.food.associatedAmount);
            });

        nameView.setText(currentItem.food.name);
            amountSelectorButton.setText(String.valueOf(currentItem.food.associatedAmount));
            portionSelectorButton.setText(Utils.getStringIdentifier(context, currentItem.food.associatedPortionType.toString()));

        return  convertView;
    }
}
