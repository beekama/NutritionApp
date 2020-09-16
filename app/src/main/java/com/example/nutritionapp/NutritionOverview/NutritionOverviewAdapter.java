package com.example.nutritionapp.NutritionOverview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nutritionapp.R;
import com.example.nutritionapp.foodJournal.AddFoodsLists.GroupListItem;

import java.util.ArrayList;

public class NutritionOverviewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<NutritionOverviewItem> item;

    public NutritionOverviewAdapter(){
        super();
    }
    public NutritionOverviewAdapter(Context context, ArrayList<NutritionOverviewItem> item){
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
        convertView = inflater.inflate(R.layout.journal_add_food_selection_list, parent, false);
        return convertView;
    }
}
