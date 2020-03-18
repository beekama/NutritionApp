package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class create_food extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.foods);

    //go and add an Item:
    final ImageButton addItemButton = (ImageButton) findViewById(R.id.addItemButton);
    addItemButton.setOnClickListener((new View.OnClickListener() {
        public void onClick(View v) {
/*            //change color while pressing -- needs something like sleep to be visible
            v.setSelected(!v.isSelected());
            if (v.isSelected()){
                v.getBackground().setAlpha(0);
            }*/
            Intent myIntent = new Intent(v.getContext(), create_item.class);
            startActivity(myIntent);
/*            v.setSelected(false);
            if(!v.isSelected()){
                v.getBackground().setAlpha(255);
            }*/
        }
    }));

    //go back to home with back-button:
        final ImageButton goBack =(ImageButton) findViewById(R.id.foods_back_button);
        goBack.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), MainActivity.class);
                startActivity(myIntent);
            }
        }));
}}

