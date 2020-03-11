package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button goToFoodJournal = (Button)findViewById(R.id.food_journal);
        goToFoodJournal.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent myIntent = new Intent(v.getContext(),food_journal.class);
                //TextView vtext = (TextView)findViewById(R.id.);
                //vtext.setText("LOL");
                startActivity(myIntent);
            }
        });

    }
}
