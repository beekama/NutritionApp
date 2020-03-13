package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class food_journal extends AppCompatActivity {

    ListView listView;
    TextView textView;
    String[] testdata ={
            "hallo",
            "tut",
            "das",
            "funktionieren?",
            "meh",
            "da",
            "muss",
            "meeeee",
            "hhhr",
            "ich",
            "will",
            "scrollen",
            "..."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_journal);

        listView = (ListView)findViewById(R.id.listview);
        textView = (TextView)findViewById(R.id.ListTextView);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,testdata);

        listView.setAdapter(adapter);

/*        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
            Object listItem = listView.getItemAtPosition(i);
        }});*/


        //get back to home with home-button:

        Button backHome = (Button) findViewById(R.id.backHomeFromJournal);
        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), MainActivity.class);
                startActivity(myIntent);
            }
        });




    }
}
