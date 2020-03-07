package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button test = (Button)findViewById(R.id.kathi);
        test.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                TextView vtext = (TextView)findViewById(R.id.textfeld);
                vtext.setText("LOL");
            }
        });

    }
}
