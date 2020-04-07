package com.example.nutritionapp.foodJournal;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.ArrayList;
import java.util.HashMap;

public class FoodJournalOverview extends AppCompatActivity {

    private LocalDate now = LocalDate.now();
    private LocalDate oldestDateShown = LocalDate.now().minusWeeks(1);

    final private Duration ONE_DAY = Duration.ofDays(1);
    final private Duration ONE_WEEK = Duration.ofDays(7);
    final private ArrayList<Item> inputList = new ArrayList<Item>();

    private myAdapter adapter;
    private Database db;
    private ListView foodsFromDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_journal);
        db = new Database(this);

        /* retrieve items */
        updateFoodJournalList(false);

        /* set adapter */
        adapter = new myAdapter(this, inputList);
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
        /* retrieve items */
        updateFoodJournalList(true);
    }

    private void updateFoodJournalList(boolean runInvalidation) {
        LocalDate startDate = now.atStartOfDay().toLocalDate();
        HashMap<Integer, ArrayList<Food>> foodGroups = db.getLoggedFoodsByDate(now, oldestDateShown);
        inputList.clear();
        inputList.add(new HeaderItem("Today"));
        for(Integer key : foodGroups.keySet()){
            String foodNamesInGroup = "";
            for(Food food : foodGroups.get(key)){
                if(startDate.isAfter(food.loggedAt)){
                    inputList.add(new HeaderItem(food.loggedAt.atStartOfDay().format(DateTimeFormatter.ISO_DATE)));
                    startDate = food.loggedAt.atStartOfDay().toLocalDate();
                }else{
                    foodNamesInGroup += food.name + ",";
                }
            }

            ItemItem nextItem =  new ItemItem(foodNamesInGroup);
            inputList.add(nextItem);
        }
        if(runInvalidation) {
            adapter.notifyDataSetInvalidated();
            foodsFromDatabase.invalidate();
        }
    }


    //interface ITEM:
    public interface Item {
        public boolean isSection();
        public String getTitle();
    }

    public class HeaderItem implements Item{
        private final String title;

        public HeaderItem(String title) {
            this.title = title;
        }

        @Override
        public boolean isSection() {
            return true;
        }

        @Override
        public String getTitle() {
            return title;
        }
    }

    public class ItemItem implements Item{
        public final String title;

        public ItemItem(String title) {
            this.title = title;
        }

        @Override
        public boolean isSection() {
            return false;
        }

        @Override
        public String getTitle() {
            return title;
        }
    }



    //adapter:
    public class myAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Item> item;
        private ArrayList<Item> originalItem;

        public myAdapter(){
            super();
        }
        public myAdapter(Context context, ArrayList<Item> item){
            this.context=context;
            this.item=item;
        }
        @Override
        public int getCount() {
            return item.size();
        }

        @Override
        public Object getItem(int position) {
            return item.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (item.get(position).isSection()){
                convertView = inflater.inflate(R.layout.list_header, parent, false);
                TextView HeaderTextView = (TextView) convertView.findViewById(R.id.HeaderTextView);
                HeaderTextView.setText(( item.get(position).getTitle()));
            }
            else{
                convertView = inflater.inflate(R.layout.list_item,parent,false);
                TextView ItemListView = (TextView) convertView.findViewById(R.id.ListTextView);
                ItemListView.setText(( item.get(position).getTitle()));
            }
            return convertView;
        }
    }
}
