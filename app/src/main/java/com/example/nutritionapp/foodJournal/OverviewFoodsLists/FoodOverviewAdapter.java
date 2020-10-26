package com.example.nutritionapp.foodJournal.OverviewFoodsLists;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nutritionapp.NutritionOverview.NutritionOverview;
import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Conversions;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.NutritionPercentageTupel;

import java.util.ArrayList;

public class FoodOverviewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<FoodOverviewListItem> items;

    public FoodOverviewAdapter(Context context, ArrayList<FoodOverviewListItem> items){
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

        if(convertView != null){
            return convertView;
        }else{
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.journal_foods_dayheader, parent, false);
        }

        /* item at position */
        FoodOverviewListItem itemAtCurPos = this.items.get(position);

        /* get relevant sub-views */
        TextView dateText = convertView.findViewById(R.id.dateText);
        ProgressBar energyBar = convertView.findViewById(R.id.energyBar);
        ListView subFoodList = convertView.findViewById(R.id.list_grouped_foods);
        TextView energyBarText = convertView.findViewById(R.id.energyBarText);

        /* set the correct date */
        dateText.setText(items.get(position).date);

        dateText.setOnClickListener(view -> {
            /* TODO reactivate this when it's fixed
            Intent target = new Intent(view.getContext(), NutritionOverview.class);
            target.putExtra("startDate", items.get(position).date);
            context.startActivity(target);
             */
        });

        NutritionAnalysis analysis = new NutritionAnalysis(itemAtCurPos.foods);
        int energyUsed = Conversions.jouleToKCal(analysis.getTotalEnergy());
        int energyNeeded = 2000;
        int energyUsedPercentage = energyUsed*100/energyNeeded;

        if(energyUsedPercentage < 75){
            energyBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        }else if(energyUsedPercentage < 125){
            energyBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
        }else{
            energyBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        }

        energyBar.setProgress(Math.min(energyUsedPercentage, 100));
        String energyBarContent = String.format("Energy %d/%d", energyUsed, energyNeeded);
        energyBarText.setText(energyBarContent);


        /* calculate and set nutrition */
        ArrayList<NutritionPercentageTupel> percentages = analysis.getNutritionPercentageSortedFilterZero();

        /* display the foods in the nested sub-list */
        ArrayList<GroupFoodItem> listItemsInThisSection = new ArrayList<>();
        for(int group : itemAtCurPos.foodGroups.keySet()){
            listItemsInThisSection.add(new GroupFoodItem(itemAtCurPos.foodGroups.get(group), group));
        }

        ListAdapter subListViewAdapter = new GroupListAdapter(context, listItemsInThisSection);
        subFoodList.setAdapter(subListViewAdapter);
        return convertView;
    }
}
