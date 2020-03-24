package com.example.myapplication;

import android.content.Intent;
import android.inputmethodservice.ExtractEditText;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity ;

public class me_config extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_config);

        //get database connection
        final Database db = new Database(this);

        // --- AGE ---
        // set current values:
        final TextView tvAge = (TextView) findViewById(R.id.et_meConfig_age);
        final Integer age = db.getPersonAge();
        if (age != -1) {
            tvAge.setText(age.toString());
        }
        //submit new age:
        final EditText et = (EditText) findViewById(R.id.et_meConfig_age);
        et.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //if enter button is pressed
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //Toast.makeText(getApplicationContext(), et.getText(),Toast.LENGTH_LONG).show();

                    //get setting from edittext
                    int age = Integer.parseInt(et.getText().toString());
                    try {
                        //write to database class
                        db.setPersonAge(age);
                    } catch (IllegalArgumentException e) {
                        //show a notification
                        Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }
                    return true;
                }
                return false;
            }
        });

        // -- GENDER --
        //set current values:
        final TextView tvGender = (TextView) findViewById(R.id.et_meConfig_gender);
        final String gender = db.getPersonGender();
        if (!gender.equals("none")){
            tvGender.setText(gender);}


        //submit new gender:
        final EditText etGender = (EditText) findViewById(R.id.et_meConfig_gender);
        etGender.setOnKeyListener(new View.OnKeyListener(){
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //if enter button is pressed
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //get setting from edittext
                    String gender = etGender.getText().toString();
                    //Toast.makeText(getApplicationContext(), etGender.getText(), Toast.LENGTH_LONG).show();
                    try {
                        //write to database class
                        db.setPersonGender(gender);
                    } catch (IllegalArgumentException e) {
                        //show a notification
                        Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }
                    return true;
                }
                return false;
            }
    });

        // -- WEIGHT --
        // set current values:
        final TextView tvWeight = (TextView) findViewById(R.id.et_meConfig_weight);
        final Integer weight = db.getPersonWeight();
        if (weight != -1) {
            tvWeight.setText(weight.toString());
        }
        //submit new weight:
        final EditText etWeight = (EditText) findViewById(R.id.et_meConfig_weight);
        etWeight.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //if enter button is pressed
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //Toast.makeText(getApplicationContext(), et.getText(),Toast.LENGTH_LONG).show();

                    //get setting from edittext
                    int weight = Integer.parseInt(etWeight.getText().toString());
                    try {
                        //write to database class
                        db.setPersonWeight(weight);
                    } catch (IllegalArgumentException e) {
                        //show a notification
                        Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }
                    return true;
                }
                return false;
            }
        });

        // -- HEIGHT --
        // set current values:
        final TextView tvHeight = (TextView) findViewById(R.id.et_meConfig_height);
        final Integer height = db.getPersonHeight();
        if (height != -1) {
            tvHeight.setText(height.toString());
        }
        //submit new height:
        final EditText etHeight = (EditText) findViewById(R.id.et_meConfig_height);
        etHeight.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //if enter button is pressed
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //Toast.makeText(getApplicationContext(), et.getText(),Toast.LENGTH_LONG).show();

                    //get setting from edittext
                    int height = Integer.parseInt(etHeight.getText().toString());
                    try {
                        //write to database class
                        db.setPersonHeight(height);
                    } catch (IllegalArgumentException e) {
                        //show a notification
                        Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }
                    return true;
                }
                return false;
            }
        });

    //go Back home from me_config:
        final ImageButton backHome = (ImageButton) findViewById(R.id.meConfig_ib_backToHome);
        backHome.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }));



/*       //update age:
        final ImageButton imageAge = (ImageButton) findViewById(R.id.iv_meConfig_age);
        imageAge.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), update_age.class);
                startActivity(myIntent);
            }
        }));*/
}}
