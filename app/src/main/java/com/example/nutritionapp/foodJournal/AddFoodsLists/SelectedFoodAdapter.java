package com.example.nutritionapp.foodJournal.AddFoodsLists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.nutritionapp.R;

import java.util.ArrayList;

public class SelectedFoodAdapter extends BaseAdapter {

    private Context context;
    public ArrayList<SelectedFoodItem> items;

    public SelectedFoodAdapter(){
        super();
    }
    public SelectedFoodAdapter(Context context, ArrayList<SelectedFoodItem> items){
        this.context=context;
        this.items=items;
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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.journal_add_food_selected_item, parent, false);
        SelectedFoodItem currentItem = items.get(position);
        TextView nameView = convertView.findViewById(R.id.item_name);
        EditText amountSelectorView = convertView.findViewById(R.id.amount_selector);
        nameView.setText(currentItem.food.name);
        amountSelectorView.setText(Integer.toString(currentItem.amount));
        return  convertView;
    }
}
