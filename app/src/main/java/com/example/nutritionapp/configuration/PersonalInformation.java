package com.example.nutritionapp.configuration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.R;

public class PersonalInformation extends AppCompatActivity {
    @SuppressLint("ResourceAsColor")
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_config);

        //replace actionbar with custom toolbar:
        Toolbar tb = findViewById(R.id.toolbar);
        TextView tb_title = findViewById(R.id.toolbar_title);
        ImageButton tb_back = findViewById(R.id.toolbar_back);
        //back home button:
        final ImageButton backHome = (ImageButton) findViewById(R.id.toolbar_back);
        backHome.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }));
        tb.setTitle("");
        tb_title.setText("PROFILE");
        tb_back.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(tb);


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
                    //hide keyboard and clear focus:
                    hideKeyboard(et);
                    //get setting from edittext
                    //only write to database if input can be parsed properly:
                    try {
                        int age = Integer.parseInt(et.getText().toString());
                        try {
                            //write to database class
                            db.setPersonAge(age);
                        } catch (IllegalArgumentException e) {
                            //show a notification
                            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    } catch (IllegalArgumentException e) {
                        //show notification and old values in textview
                        Toast toast = Toast.makeText(getApplicationContext(), "unaccepted input", Toast.LENGTH_LONG);
                        toast.show();
                        if (age != -1) {
                            tvAge.setText(age.toString());
                        } else {
                            tvAge.setText("");
                        }
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
        if (!gender.equals("none")) {
            tvGender.setText(gender);
        }


        //submit new gender:
        final EditText etGender = (EditText) findViewById(R.id.et_meConfig_gender);
        etGender.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //if enter button is pressed
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //hide keyboard and clear focus:
                    hideKeyboard(etGender);
                    //get setting from edittext
                    //only write to database if input can be parsed properly:
                    try {
                        String gender = etGender.getText().toString();
                        try {
                            //write to database class
                            db.setPersonGender(gender);
                        } catch (IllegalArgumentException e) {
                            //show a notification
                            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    } catch (IllegalArgumentException e) {
                        //show notification and old values in textview
                        Toast toast = Toast.makeText(getApplicationContext(), "unaccepted input", Toast.LENGTH_LONG);
                        toast.show();
                        if (!gender.equals("none")) {
                            tvGender.setText(gender);
                        } else {
                            tvGender.setText("");
                        }
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
                    //hide keyboard and clear focus:
                    hideKeyboard(etWeight);
                    //get setting from edittext
                    //only write to database if input can be parsed properly:
                    try {
                        int weight = Integer.parseInt(etWeight.getText().toString());
                        try {
                            //write to database class
                            db.setPersonWeight(weight);
                        } catch (IllegalArgumentException e) {
                            //show a notification
                            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    } catch (IllegalArgumentException e) {
                        //show notification and old values in textview
                        Toast toast = Toast.makeText(getApplicationContext(), "unaccepted input", Toast.LENGTH_LONG);
                        toast.show();
                        if (weight != -1) {
                            tvWeight.setText(weight.toString());
                        } else {
                            tvWeight.setText("");
                        }
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
                    //only write to database if input can be parsed properly:
                    try {
                        int height = Integer.parseInt(etHeight.getText().toString());
                        try {
                            //write to database class
                            db.setPersonHeight(height);
                        } catch (IllegalArgumentException e) {
                            //show a notification
                            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    } catch (IllegalArgumentException e) {
                        //show notification and old values in textview
                        Toast toast = Toast.makeText(getApplicationContext(), "unaccepted input", Toast.LENGTH_LONG);
                        toast.show();
                        if (height != -1) {
                            tvHeight.setText(Integer.toString(height));
                        } else {
                            tvHeight.setText("");
                        }
                    }
                    return true;
                }
                return false;
            }
        });


        //-- BMI --
        //todo update
        Double bmi = (db.getPersonWeight() * 1.0) / (((db.getPersonHeight() * 1.0) / 100) * ((db.getPersonHeight() * 1.0) / 100));
        final TextView tv_BMI = (TextView) findViewById(R.id.tvOut_meConfig_bmi);
        bmi = Math.round(bmi * 100.0) / 100.0;
        tv_BMI.setText(bmi.toString());

        //-- Calories --
        //set current values:
        final TextView tv_energy = (TextView) findViewById(R.id.tvOut_meConfig_calories);
        final Integer energy = db.getPersonEnergyReq();
        if (energy != -1) {
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
                    //only write to database if input can be parsed properly:
                    try {
                        int calories = Integer.parseInt(etCalories.getText().toString());
                        try {
                            //write to database class
                            db.setPersonEnergyReq(calories);
                        } catch (IllegalArgumentException e) {
                            //show a notification
                            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    } catch (IllegalArgumentException e) {
                        //show notification and old values in textview
                        Toast toast = Toast.makeText(getApplicationContext(), "unaccepted input", Toast.LENGTH_LONG);
                        toast.show();
                        if (energy != -1) {
                            tv_energy.setText(Integer.toString(energy));
                        } else {
                            tv_energy.setText("");
                        }
                    }
                    return true;
                }
                return false;
            }
        });

    }

    private void hideKeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        et.clearFocus();
    }
}