package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class update_age extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_age);

        //cancel update age:
        final ImageButton backHome = (ImageButton) findViewById(R.id.updateAge_ib_backToHome);
        backHome.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), me_config.class);
                startActivity(myIntent);
            }
            //todo: warning changes will be deleted
        }));

    }}
