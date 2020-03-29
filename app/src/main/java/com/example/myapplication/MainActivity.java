package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //replace actionbar with custom toolbar:
        Toolbar tb = findViewById(R.id.toolbar);
        TextView tb_title =findViewById(R.id.toolbar_title);
        ImageView grain = (ImageView)findViewById(R.id.toolbar_back);
        ImageView graint = (ImageView)findViewById(R.id.toolbar_forward);
        tb.setTitle("");
        tb_title.setText("HOME");
        setSupportActionBar(tb);
        grain.setImageResource(R.drawable.ic_grain);
        graint.setImageResource(R.drawable.ic_grain);

        //go to food_journal:
        Button goToFoodJournal = (Button)findViewById(R.id.food_journal);
        goToFoodJournal.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent myIntent = new Intent(v.getContext(),food_journal.class);
                startActivity(myIntent);
            }
        });

        //go to create_food:
        Button goCreateFood = (Button)findViewById(R.id.create_foods);
        goCreateFood.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent myIntent = new Intent(v.getContext(),create_food.class);
                startActivity(myIntent);
            }
        });

        //go to me_config:
        Button goConfig = (Button)findViewById(R.id.config);
        goConfig.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent myIntent = new Intent(v.getContext(),me_config.class);
                startActivity(myIntent);
            }
        });




    }
}
