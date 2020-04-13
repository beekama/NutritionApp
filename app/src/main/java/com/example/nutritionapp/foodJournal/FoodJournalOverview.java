package com.example.nutritionapp.foodJournal;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritionapp.foodJournal.AddFoodsLists.GenericListItem;
import com.example.nutritionapp.foodJournal.AddFoodsLists.ListFoodItem;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.NutritionPercentageTupel;
import com.example.nutritionapp.other.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.SortedMap;

public class FoodJournalOverview extends AppCompatActivity {

    private LocalDate now = LocalDate.now();
    private LocalDate oldestDateShown = LocalDate.now().minusWeeks(1);

    final private Duration ONE_DAY = Duration.ofDays(1);
    final private Duration ONE_WEEK = Duration.ofDays(7);
    final private ArrayList<FoodOverviewListItem> foodDataList = new ArrayList<>();

    private FoodOverviewAdapter adapter;
    private Database db;
    private ListView foodsFromDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //splash screen: - show only when needed
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal);
        db = new Database(this);

        /* retrieve items */
        updateFoodJournalList(false);

        /* set adapter */
        adapter = new FoodOverviewAdapter(this, foodDataList);
        foodsFromDatabase = findViewById(R.id.listview);
        foodsFromDatabase.setAdapter(adapter);
        foodsFromDatabase.setTextFilterEnabled(true);

        final Button addStuff = findViewById(R.id.add_food);
        addStuff.setOnClickListener(v -> startActivity(new Intent(v.getContext(), AddFoodToJournal.class)));

    }

    @Override
    public void onResume() {
        super.onResume();
        updateFoodJournalList(true);
    }

    private void updateFoodJournalList(boolean runInvalidation) {
        HashMap<Integer, ArrayList<Food>> foodGroups = db.getLoggedFoodsByDate(now, oldestDateShown);
        SortedMap<LocalDateTime, HashMap<Integer, ArrayList<Food>>> foodGroupsByDay = Utils.foodGroupsByDays(foodGroups);
        foodDataList.clear();

        /* generate reversed list */
        ArrayList<LocalDateTime> keyListReversed = new ArrayList<>();
        keyListReversed.addAll(foodGroupsByDay.keySet());
        Collections.reverse(keyListReversed);

        for(LocalDateTime day : keyListReversed){
            HashMap<Integer, ArrayList<Food>> localFoodGroups = foodGroupsByDay.get(day);
            String dateString = day.format(DateTimeFormatter.ISO_DATE);
            ArrayList<Food> foodListForGroupOnDay = new ArrayList<>();
            for(Integer groupId : localFoodGroups.keySet()){
                foodListForGroupOnDay.addAll(localFoodGroups.get(groupId));
            }
            FoodOverviewListItem nextItem = new FoodOverviewListItem(dateString, foodListForGroupOnDay);
            foodDataList.add(nextItem);
        }
        if(runInvalidation) {
            adapter.notifyDataSetInvalidated();
            foodsFromDatabase.invalidate();
        }
    }


}

class FoodOverviewListItem{
    public final String date;
    public ArrayList<Food> foods;

    public FoodOverviewListItem(String date, ArrayList<Food> foods) {
        this.date = date;
        this.foods = foods;
    }

}

class FoodOverviewAdapter extends BaseAdapter {
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
        ArrayList<NutritionPercentageTupel> percentages = analysis.getNutritionPercentageSorted();
        String testText =  percentages.get(0).nutritionElement + " : Only " + percentages.get(0).percentage + "% of DRI";
        nutritionText.setText(testText);
        energyText.setText(Integer.toString(analysis.getTotalEnergy()) + " Joule");

        /* display the foods in the nested sub-list */
        ArrayList<GenericListItem> listItemsInThisSection = new ArrayList<>();
        for(Food f : itemAtCurPos.foods){
            listItemsInThisSection.add(new ListFoodItem(f));
        }
        ListAdapter subListViewAdapter = new com.example.nutritionapp.foodJournal.AddFoodsLists.ListAdapter(context, listItemsInThisSection);
        subFoodList.setAdapter(subListViewAdapter);
        return convertView;
    }
}