package com.example.nutritionapp.foodJournal;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.NutritionAnalysis;

import java.util.ArrayList;
import java.util.HashMap;

public class FoodJournalOverview extends AppCompatActivity {

    private LocalDate now = LocalDate.now();
    private LocalDate oldestDateShown = LocalDate.now().minusWeeks(1);

    final private Duration ONE_DAY = Duration.ofDays(1);
    final private Duration ONE_WEEK = Duration.ofDays(7);
    final private ArrayList<FoodOverviewListItem> inputList = new ArrayList<FoodOverviewListItem>();

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
        adapter = new FoodOverviewAdapter(this, inputList, null);
        foodsFromDatabase = (ListView) findViewById(R.id.listview);
        foodsFromDatabase.setAdapter(adapter);
        foodsFromDatabase.setTextFilterEnabled(true);

        final Button addStuff = (Button)findViewById(R.id.add_food);
        addStuff.setOnClickListener(v -> {
            startActivity(new Intent(v.getContext(), AddFoodToJournal.class));
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        updateFoodJournalList(true);
    }

    private void updateFoodJournalList(boolean runInvalidation) {
        LocalDate startDate = now.atStartOfDay().toLocalDate();
        HashMap<Integer, ArrayList<Food>> foodGroups = db.getLoggedFoodsByDate(now, oldestDateShown);
        inputList.clear();
        inputList.add(new FoodOverviewListItem("Today"));
        for(Integer key : foodGroups.keySet()){
            String foodNamesInGroup = "";
            for(Food food : foodGroups.get(key)){
                if(startDate.isAfter(food.loggedAt)){
                    inputList.add(new FoodOverviewListItem(food.loggedAt.atStartOfDay().format(DateTimeFormatter.ISO_DATE)));
                    startDate = food.loggedAt.atStartOfDay().toLocalDate();
                }else{
                    foodNamesInGroup += food.name + ",";
                }
            }

            FoodOverviewListItem nextItem =  new FoodOverviewListItem(foodNamesInGroup);
            inputList.add(nextItem);
        }
        if(runInvalidation) {
            adapter.notifyDataSetInvalidated();
            foodsFromDatabase.invalidate();
        }
    }


}

class FoodOverviewListItem{
    public final String title;
    public FoodOverviewListItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

class FoodOverviewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<FoodOverviewListItem> items;
    private ArrayList<Food> foodsInThisSection;

    public FoodOverviewAdapter(Context context, ArrayList<FoodOverviewListItem> items, ArrayList<Food> foodsInThisSection){
        this.context = context;
        this.items   = items;
        this.foodsInThisSection = foodsInThisSection;
        if(this.foodsInThisSection == null){
            this.foodsInThisSection = new ArrayList<Food>();
        }
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

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FoodOverviewListItem currentListItem = items.get(position);

        FoodOverviewListItem currentListItemTyped = (FoodOverviewListItem) currentListItem;
        convertView = inflater.inflate(R.layout.journal_foods_dayheader, parent, false);

        /* get relevant sub-views */
        TextView dateText = (TextView) convertView.findViewById(R.id.dateText);
        TextView energyText = (TextView) convertView.findViewById(R.id.energyBar);
        TextView nutritionText = (TextView) convertView.findViewById(R.id.nutritionBar);

        /* set the correct date */
        dateText.setText(items.get(position).getTitle());

        /* calculate and set the energy */
        energyText.setText("N/A");

        /* calculate and set nutrition */
        NutritionAnalysis analysis = new NutritionAnalysis(this.foodsInThisSection); //TODO get foods in section somehow
        nutritionText.setText("N/A");

        return convertView;
    }
}