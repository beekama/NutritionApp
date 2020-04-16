package com.example.nutritionapp.foodJournal.OverviewFoodsLists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Food;

import java.util.ArrayList;

public class GroupListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<GroupFoodItem> item;

    public GroupListAdapter(Context context, ArrayList<GroupFoodItem> item){
        this.context=context;
        this.item=item;
    }

    public int getCount() {
        return item.size();
    }

    public Object getItem(int position) {
        return item.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.journal_foods_foodgroup, parent, false);
        TextView test = convertView.findViewById(R.id.foodsInGroup);

        String allFoods = "";
        for(Food f : ((GroupFoodItem)getItem(position)).foods){
            allFoods += f.name;
        }
        test.setText(allFoods);
        return convertView;
    }
}
