package com.example.myapplication;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import androidx.appcompat.app.AppCompatActivity;

import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.ArrayList;
import java.util.HashMap;

public class food_journal extends AppCompatActivity {

    private ListView zeddel;

    private LocalDate now = LocalDate.now();
    private LocalDate oldestDateShown = LocalDate.now().minusWeeks(1);

    final private Duration ONE_DAY = Duration.ofDays(1);
    final private Duration ONE_WEEK = Duration.ofDays(7);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.food_journal);
        zeddel = (ListView) findViewById(R.id.listview);
        ArrayList<Item> InputListe = new ArrayList<Item>();
        Application lol = getApplication();
        AndroidThreeTen.init(lol);
        final Database db = new Database(this);

        /* add some debug items to db */
        // Food[] debugFoods = { Food.getEmptyFood(LocalDate.now()) };
        // db.logExistingFoods(debugFoods, debugFoods[0].loggedAt);
        // Food[] debugFoods2 = { Food.getEmptyFood(LocalDate.now()), Food.getEmptyFood(LocalDate.now()) };
        // db.logExistingFoods(debugFoods, debugFoods[0].loggedAt);
        // Food[] debugFoods3 = { Food.getEmptyFood(LocalDate.now().minusDays(1)) };
        // db.logExistingFoods(debugFoods, debugFoods[0].loggedAt);
        // Food[] debugFoods4 = { Food.getEmptyFood(LocalDate.now().minusDays(2)) };
        // db.logExistingFoods(debugFoods, debugFoods[0].loggedAt);

        LocalDate startDate = now.atStartOfDay().toLocalDate();
        HashMap<Integer, ArrayList<Food>> foodGroups =  db.getLoggedFoodsByDate(now, oldestDateShown);
        InputListe.add(new HeaderItem("Today"));
        for(Integer key : foodGroups.keySet()){
            String foodNamesInGroup = "";
            for(Food food : foodGroups.get(key)){
                if(startDate.isAfter(food.loggedAt)){
                    InputListe.add(new HeaderItem(food.loggedAt.atStartOfDay().format(DateTimeFormatter.ISO_DATE)));
                    startDate = food.loggedAt.atStartOfDay().toLocalDate();
                }else{
                    foodNamesInGroup += food.name + ",";
                }
            }

            ItemItem nextItem =  new ItemItem(foodNamesInGroup);
            InputListe.add(nextItem);
        }

        //set adapter:
        final myAdapter adapter = new myAdapter(this, InputListe);
        zeddel.setAdapter(adapter);
        zeddel.setTextFilterEnabled(true);

        //get back to home with home-button:
        //Button backHome = (Button) findViewById(R.id.backHomeFromJournal);
        //backHome.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        finish();
        //    }
        //});

        final Button addStuff = (Button)findViewById(R.id.add_food);
        addStuff.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent myIntent = new Intent(v.getContext(), AddFoodToJournal.class);
                startActivity(myIntent);
            }
        });
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
