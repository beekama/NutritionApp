package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.inputmethodservice.ExtractEditText;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity ;

import org.w3c.dom.Text;

public class me_config extends AppCompatActivity {
    @SuppressLint("ResourceAsColor")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_config);

        //get database connection
        final Database db = new Database(this);

        // --- AGE ---
        // set current values:
        final TextView tvAge = (TextView) findViewById(R.id.et_meConfig_age);
        Integer age = db.getPersonAge();
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
                    //hide keyboard and clear focus:
                    hideKeyboard(et);
                    //get setting from edittext
                    //only write to database if input is not empty:
                    if (!et.getText().toString().matches("")){
                    int age = Integer.parseInt(et.getText().toString());
                    try {
                        //write to database class
                        db.setPersonAge(age);
                    } catch (IllegalArgumentException e) {
                        //show a notification
                        Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }}
                    return true;
                }
                return false;
            }
        });

        // -- GENDER --
        //set current values:
        final TextView tvGender = (TextView) findViewById(R.id.et_meConfig_gender);
        String gender = db.getPersonGender();
        if (!gender.equals("none")){
            tvGender.setText(gender);}


        //submit new gender:
        final EditText etGender = (EditText) findViewById(R.id.et_meConfig_gender);
        etGender.setOnKeyListener(new View.OnKeyListener(){
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //if enter button is pressed
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //hide keyboard and clear focus:
                    hideKeyboard(etGender);
                    //get setting from edittext
                    // only write to database if input is not empty:
                    if (!etGender.getText().toString().matches("")){
                    String gender = etGender.getText().toString();
                    try {
                        //write to database class
                        db.setPersonGender(gender);
                    } catch (IllegalArgumentException e) {
                        //show a notification
                        Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }}
                    return true;
                }
                return false;
            }
    });

        // -- WEIGHT --
        // set current values:
        final TextView tvWeight = (TextView) findViewById(R.id.et_meConfig_weight);
        Integer weight = db.getPersonWeight();
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
                    //hide keyboard and clear focus:
                    hideKeyboard(etWeight);
                    //get setting from edittext
                    //only write to database if input is not empty:
                    if (!etWeight.getText().toString().matches("")){
                    int weight = Integer.parseInt(etWeight.getText().toString());
                    try {
                        //write to database class
                        db.setPersonWeight(weight);
                    } catch (IllegalArgumentException e) {
                        //show a notification
                        Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    } }
                    return true;
                }
                return false;
            }
        });

        // -- HEIGHT --
        // set current values:
        final TextView tvHeight = (TextView) findViewById(R.id.et_meConfig_height);
        Integer height = db.getPersonHeight();
        if (height != -1) {
            tvHeight.setText(Integer.toString(height));
        }
        //submit new height:
        final EditText etHeight = (EditText) findViewById(R.id.et_meConfig_height);
        etHeight.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //if enter button is pressed
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //hide keyboard and clear focus:
                    hideKeyboard(etHeight);
                    //get setting from edittext
                    //only write to database if input is not empty:
                    if (!etHeight.getText().toString().matches("")){
                    int height = Integer.parseInt(etHeight.getText().toString());
                    try {
                        //write to database class
                        db.setPersonHeight(height);
                    } catch (IllegalArgumentException e) {
                        //show a notification
                        Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }}
                    return true;
                }
                return false;
            }
        });



        //-- BMI --
        Double bmi = db.getPersonWeight() / (Math.sqrt(db.getPersonHeight()));
        final TextView tv_BMI = (TextView) findViewById(R.id.tvOut_meConfig_bmi);
        bmi = Math.round(bmi * 100.0)/100.0;
        tv_BMI.setText(bmi.toString());

        //-- Calories --
        //set current values:
        final TextView tv_energy = (TextView) findViewById(R.id.tvOut_meConfig_calories);
        Integer energy = db.getPersonEnergyReq();
        if (energy != -1){
            tv_energy.setText(Integer.toString(energy));
        }
        //submit new calories:
        final EditText etCalories = (EditText) findViewById(R.id.tvOut_meConfig_calories);
        etCalories.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //if enter button is pressed
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //hide keyboard and clear focus:
                    hideKeyboard(etCalories);
                    //get setting from edittext
                    //only write to database if input is not empty:
                    if (!etCalories.getText().toString().matches("")){
                    int calories = Integer.parseInt(etCalories.getText().toString());
                    try {
                        //write to database class
                        db.setPersonEnergyReq(calories);
                    } catch (IllegalArgumentException e) {
                        //show a notification
                        Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }}
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
}
    private void hideKeyboard(EditText et){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        et.clearFocus();
    }}