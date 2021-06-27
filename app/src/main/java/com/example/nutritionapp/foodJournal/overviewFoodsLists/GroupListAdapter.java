package com.example.nutritionapp.foodJournal.overviewFoodsLists;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nutritionapp.R;
import com.example.nutritionapp.foodJournal.FoodGroupOverview;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Utils;

import java.util.ArrayList;

public class GroupListAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<GroupFoodItem> item;
    private final Activity parentActivity;

    public GroupListAdapter(Context context, ArrayList<GroupFoodItem> item, Activity parentActivity){
        this.context=context;
        this.item=item;
        this.parentActivity = parentActivity;
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

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.journal_foods_foodgroup, parent, false);
        }

        TextView foodsInGroupListItem = convertView.findViewById(R.id.foodsInGroup);
        GroupFoodItem item = (GroupFoodItem) this.getItem(position);
        foodsInGroupListItem.setOnClickListener(view -> {
            Intent target = new Intent(view.getContext(), FoodGroupOverview.class);
            target.putExtra("groupId", item.groupId);
            parentActivity.startActivityForResult(target, Utils.FOOD_GROUP_DETAILS_ID);
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
