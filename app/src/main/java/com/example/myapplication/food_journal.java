package com.example.myapplication;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;

public class food_journal extends AppCompatActivity {

    private ListView zeddel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_journal);

        zeddel = (ListView) findViewById(R.id.listview);
        ArrayList<Item> InputListe = new ArrayList<Item>();
        //header:
        InputListe.add(new HeaderItem("day one"));
        //items:
        InputListe.add(new ItemItem("bla"));
        InputListe.add(new ItemItem("blaa"));
        InputListe.add(new ItemItem("blaaa"));
        InputListe.add(new ItemItem("blaaaa"));
        //header:
        InputListe.add(new HeaderItem("day two"));
        //items:
        InputListe.add(new ItemItem("bla"));
        InputListe.add(new ItemItem("blaa"));
        InputListe.add(new ItemItem("blaaa"));
        InputListe.add(new ItemItem("blaaaa"));
        //header:
        InputListe.add(new HeaderItem("day three"));
        //items:
        InputListe.add(new ItemItem("bla"));
        InputListe.add(new ItemItem("blaa"));
        InputListe.add(new ItemItem("blaaa"));
        InputListe.add(new ItemItem("blaaaa"));
        //header:
        InputListe.add(new HeaderItem("day four"));
        //items:
        InputListe.add(new ItemItem("bla"));
        InputListe.add(new ItemItem("blaa"));
        InputListe.add(new ItemItem("blaaa"));
        InputListe.add(new ItemItem("blaaaa"));
        //header:
        InputListe.add(new HeaderItem("day five"));
        //items:
        InputListe.add(new ItemItem("bla"));
        InputListe.add(new ItemItem("blaa"));
        InputListe.add(new ItemItem("blaaa"));
        InputListe.add(new ItemItem("blaaaa"));

        //set adapter:
        final myAdapter adapter = new myAdapter(this, InputListe);
        zeddel.setAdapter(adapter);
        zeddel.setTextFilterEnabled(true);


        
        /* connect to sqlite database */
        Database database = new Database(this);
        Log.wtf("DEBUG", database.getFoodById("336106").name);

        //get back to home with home-button:
        Button backHome = (Button) findViewById(R.id.backHomeFromJournal);
        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
