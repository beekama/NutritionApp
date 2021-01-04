package com.example.nutritionapp.customFoods;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nutritionapp.R;

import java.util.ArrayList;

public class FoodOverviewAdapter extends BaseAdapter {

    private Context context;
    public ArrayList<FoodOverviewItem> items;

    public FoodOverviewAdapter(){
        super();
    }
    public FoodOverviewAdapter(Context context, ArrayList<FoodOverviewItem> items){
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
        convertView = inflater.inflate(R.layout.food_overview_item, parent, false);

        FoodOverviewItem item = items.get(position);
        TextView nameView = convertView.findViewById(R.id.custom_food_name);
        nameView.setText(item.food.name);
        return  convertView;
    }
}
