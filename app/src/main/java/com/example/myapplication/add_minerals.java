package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class add_minerals extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_minerals);

        //goBack to create_Item:
        final ImageButton goBack = (ImageButton) findViewById(R.id.addMinerals_back_button);
        goBack.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), create_item.class);
                startActivity(myIntent);
            }
        }));

        //break:
        //todo: warning
    }
}