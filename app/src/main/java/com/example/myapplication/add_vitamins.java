package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class add_vitamins extends AppCompatActivity {
        public void onCreate(Bundle savedInstanceState) {
            setTheme(R.style.AppTheme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.add_vitamins);

            //goBack to create_Item:
            final ImageButton goBack = (ImageButton) findViewById(R.id.addVitamins_back_button);
            goBack.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            }));

            //break:
            //todo: warning
}}
