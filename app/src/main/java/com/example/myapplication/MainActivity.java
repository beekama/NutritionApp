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
        //splash screen: - show only when needed
        setTheme(R.style.AppTheme);

        //required settings:
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        //get database connection
        final Database db = new Database(this);


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


        //BUTTON 1:
        //go to food_journal:
        View v_fj = (View) findViewById(R.id.food_journal);
        v_fj.setBackgroundResource(R.color.p1);
        //set buttontext:
        TextView food_journal_title = (TextView) v_fj.findViewById(R.id.button_title);
        TextView food_journal_left =(TextView) v_fj.findViewById(R.id.button_left);
        TextView food_journal_right = (TextView)v_fj.findViewById(R.id.button_right);
        food_journal_title.setText("FOOD JOURNAL");
        food_journal_left.setText("weight");
        food_journal_right.setText("height");
        //go to food_journal:
        v_fj.setOnClickListener(new View.OnClickListener(){
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
