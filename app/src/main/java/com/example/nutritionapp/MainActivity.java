package com.example.nutritionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nutritionapp.configuration.PersonalInformation;
import com.example.nutritionapp.customFoods.CreateFoodOverview;
import com.example.nutritionapp.foodJournal.FoodJournalOverview;
import com.example.nutritionapp.other.Database;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //splash screen: - show only when needed
        setTheme(R.style.AppTheme);

        //required settings:
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity_main);

        //get database connection
        final Database db = new Database(this);


        //replace actionbar with custom app_toolbar:
        Toolbar tb = findViewById(R.id.toolbar);
        TextView tb_title =findViewById(R.id.toolbar_title);
        ImageView tbleft = (ImageView)findViewById(R.id.toolbar_back);
        ImageView tbright = (ImageView)findViewById(R.id.toolbar_forward);
        tb.setTitle("");
        tb_title.setText("HOME");
        setSupportActionBar(tb);
        tbleft.setImageResource(R.drawable.ic_grainleft);
        tbright.setImageResource(R.drawable.ic_grainright);


        //BUTTON 1:
        //go to journal:
        View v_fj = (View) findViewById(R.id.food_journal);
        v_fj.setBackgroundResource(R.color.p1);
        //set buttontext:
        TextView food_journal_title = (TextView) v_fj.findViewById(R.id.button_title);
        TextView food_journal_left =(TextView) v_fj.findViewById(R.id.button_left);
        TextView food_journal_right = (TextView)v_fj.findViewById(R.id.button_right);
        food_journal_title.setText("FOOD JOURNAL");
        food_journal_left.setText("calories");
        food_journal_right.setText("everything satisfied?");
        //go to journal:
        v_fj.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent myIntent = new Intent(v.getContext(), FoodJournalOverview.class);
                startActivity(myIntent);
            }
        });

        //BUTTON 2:
        //go to configuration:
        View v_conf = (View) findViewById(R.id.config);
        v_conf.setBackgroundResource(R.color.p3);
        //set buttontext:
        TextView config_title = (TextView) v_conf.findViewById(R.id.button_title);
        TextView config_left =(TextView) v_conf.findViewById(R.id.button_left);
        TextView config_right = (TextView)v_conf.findViewById(R.id.button_right);
        config_title.setText("CONFIG  ");
        config_left.setText("weight");
        config_right.setText("height");
        //go to journal:
        v_conf.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent myIntent = new Intent(v.getContext(), PersonalInformation.class);
                startActivity(myIntent);
            }
        });

        //BUTTON 3:
        //go to create_food:
        View v_cfood = (View) findViewById(R.id.create_foods);
        v_cfood.setBackgroundResource(R.color.p2);
        //set buttontext:
        TextView createFood_title = (TextView) v_cfood.findViewById(R.id.button_title);
        TextView createFood_left =(TextView) v_cfood.findViewById(R.id.button_left);
        TextView createFood_right = (TextView)v_cfood.findViewById(R.id.button_right);
        createFood_title.setText("CREATE FOODS");
        createFood_left.setText("num of created items");
        createFood_right.setText("");
        //go to journal:
        v_cfood.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent myIntent = new Intent(v.getContext(), CreateFoodOverview.class);
                startActivity(myIntent);
            }
        });

        //BUTTON 4:
        //go to recommendation:
        View v_recommend = (View) findViewById(R.id.recommendations);
        v_recommend.setBackgroundResource(R.color.p4);
        //set buttontext:
        TextView recommendation_title = (TextView) v_recommend.findViewById(R.id.button_title);
        TextView recommendation_left =(TextView) v_recommend.findViewById(R.id.button_left);
        TextView recommendation_right = (TextView) v_recommend.findViewById(R.id.button_right);
        recommendation_title.setText("RECOMMENDATION");
        config_left.setText("deficiencies");
       /* config_right.setText("");*/
        //go to recommendations:
        v_recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent =  new Intent(v.getContext(), recommendations.class);
                startActivity(myIntent);
            }
        });






    }
}
