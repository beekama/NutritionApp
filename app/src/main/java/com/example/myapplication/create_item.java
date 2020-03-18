package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class create_item extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_item);

        //goBack from create_Item:
        final ImageButton goBack = (ImageButton) findViewById(R.id.createItem_back_button);
        goBack.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), create_food.class);
                startActivity(myIntent);
            }
        }));
}}
