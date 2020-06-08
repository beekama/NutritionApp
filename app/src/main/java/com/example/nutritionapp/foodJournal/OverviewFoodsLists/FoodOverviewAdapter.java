package com.example.nutritionapp.foodJournal.OverviewFoodsLists;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Conversions;
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

        /* item at postition */
        FoodOverviewListItem itemAtCurPos = this.items.get(position);

        /* inflate layout */
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.journal_foods_dayheader, parent, false);

        /* get relevant sub-views */
        TextView dateText = convertView.findViewById(R.id.dateText);
        TextView energyText = convertView.findViewById(R.id.energyBar);
        TextView nutritionText = convertView.findViewById(R.id.nutritionBar);
        ListView subFoodList = convertView.findViewById(R.id.list_grouped_foods);

        /* set the correct date */
        dateText.setText(items.get(position).date);


        NutritionAnalysis analysis = new NutritionAnalysis(itemAtCurPos.foods);


        /* calculate and set nutrition */
        ArrayList<NutritionPercentageTupel> percentages = analysis.getNutritionPercentageSortedFilterZero();
        String testText = String.format("%s : Only %d%%", percentages.get(0).nutritionElement, (int) (percentages.get(0).percentage * 100));
        nutritionText.setText(testText);
        energyText.setText(Conversions.jouleToKCal(analysis.getTotalEnergy()) + " KCAL");

        /* display the foods in the nested sub-list */
        ArrayList<GroupFoodItem> listItemsInThisSection = new ArrayList<>();
        for(int group : itemAtCurPos.foodGroups.keySet()){
            listItemsInThisSection.add(new GroupFoodItem(itemAtCurPos.foodGroups.get(group)));
        }
        Log.wtf("FOOD", "--------");
        ListAdapter subListViewAdapter = new GroupListAdapter(context, listItemsInThisSection);
        subFoodList.setAdapter(subListViewAdapter);
        return convertView;
    }
}
