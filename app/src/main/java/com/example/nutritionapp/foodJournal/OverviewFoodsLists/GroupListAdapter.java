package com.example.nutritionapp.foodJournal.OverviewFoodsLists;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nutritionapp.NutritionOverview.NutritionOverview;
import com.example.nutritionapp.R;
import com.example.nutritionapp.foodJournal.FoodGroupOverview;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Utils;

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

        TextView foodsInGroupListItem = convertView.findViewById(R.id.foodsInGroup);
        GroupFoodItem item = (GroupFoodItem) this.getItem(position);
        foodsInGroupListItem.setOnClickListener(view -> {
            Intent target = new Intent(view.getContext(), FoodGroupOverview.class);
            target.putExtra("groupId", item.groupId);
            context.startActivity(target);
        });

        StringBuilder allFoods = new StringBuilder();
        boolean firstLoop = true;
        for(Food f : ((GroupFoodItem)getItem(position)).foods){
            if(firstLoop){
                firstLoop = false;
            }else{
                allFoods.append(", ");
            }
            allFoods.append(f.name);
        }
        
        foodsInGroupListItem.setText(allFoods.toString());
        return convertView;
    }
}
