package com.example.nutritionapp.configuration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.R;

public class PersonalInformation extends AppCompatActivity {
    private Integer weight, height;

    @SuppressLint("ResourceAsColor")
    public void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);

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

        //BASIC CONFIGURATION

        //--SET CURRENT VALUES--
        //age:
        final TextView tvAge = (TextView) findViewById(R.id.et_meConfig_age);
        final Integer age = db.getPersonAge();
        if (age != -1) {
            tvAge.setText(age.toString());
        }
        //gender:
        final TextView tvGender = (TextView) findViewById(R.id.et_meConfig_gender);
        final String gender = db.getPersonGender();
        if (!gender.equals("none")) {
            tvGender.setText(gender);
        }
        // weight:
        final TextView tvWeight = (TextView) findViewById(R.id.et_meConfig_weight);
        weight = db.getPersonWeight();
        if (weight != -1) {
            tvWeight.setText(weight.toString());
        }
        //height:
        final TextView tvHeight = (TextView) findViewById(R.id.et_meConfig_height);
        height = db.getPersonHeight();
        if (height != -1) {
            tvHeight.setText(Integer.toString(height));
        }



        //SUBMIT CHANGES IN CONFIGURATION:
        final Button submit = (Button) findViewById(R.id.meConfig_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //age:
                final EditText et = (EditText) findViewById(R.id.et_meConfig_age);
                defaultEditBackground(et);
                //get setting from edittext
                //only write to database if input can be parsed properly:
                try {
                    int newAge = Integer.parseInt(et.getText().toString());
                    try {
                        //write to database class
                        db.setPersonAge(newAge);
                    } catch (IllegalArgumentException e) {
                        //show a notification
                        Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                        wrongEditBackground(et);
                    }
                } catch (IllegalArgumentException e) {
                    //show notification and old values in textview
                    Toast toast = Toast.makeText(getApplicationContext(), "unaccepted input", Toast.LENGTH_LONG);
                    toast.show();
                    wrongEditBackground(et);
                    if (age != -1) {
                        tvAge.setText(age.toString());
                    } else {
                        tvAge.setText("");
                    }
                }


                //gender:
                final EditText etGender = (EditText) findViewById(R.id.et_meConfig_gender);
                defaultEditBackground(etGender);
                //get setting from edittext
                //only write to database if input can be parsed properly:
                try {
                    String newGender = etGender.getText().toString();
                    try {
                        //write to database class
                        db.setPersonGender(newGender);
                    } catch (IllegalArgumentException e) {
                        //show a notification
                        Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                        wrongEditBackground(etGender);
                    }
                } catch (IllegalArgumentException e) {
                    //show notification and old values in textview
                    Toast toast = Toast.makeText(getApplicationContext(), "unaccepted input", Toast.LENGTH_LONG);
                    toast.show();
                    wrongEditBackground(etGender);
                    if (!gender.equals("none")) {
                        tvGender.setText(gender);
                    } else {
                        tvGender.setText("");
                    }

                }

                //submit new weight:
                final EditText etWeight = (EditText) findViewById(R.id.et_meConfig_weight);
                defaultEditBackground(etWeight);
                //get setting from edittext
                //only write to database if input can be parsed properly:
                try {
                    int newWeight = Integer.parseInt(etWeight.getText().toString());
                    try {
                        //write to database class
                        db.setPersonWeight(newWeight);
                        weight = newWeight;
                    } catch (IllegalArgumentException e) {
                        //show a notification
                        Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                        wrongEditBackground(etWeight);
                    }
                } catch (IllegalArgumentException e) {
                    //show notification and old values in textview
                    Toast toast = Toast.makeText(getApplicationContext(), "unaccepted input", Toast.LENGTH_LONG);
                    toast.show();
                    wrongEditBackground(etWeight);
                    if (weight != -1) {
                        tvWeight.setText(weight.toString());
                    } else {
                        tvWeight.setText("");
                    }
                }

                //submit new height:
                final EditText etHeight = (EditText) findViewById(R.id.et_meConfig_height);
                defaultEditBackground(etHeight);
                //get setting from edittext
                //only write to database if input can be parsed properly:
                try {
                    int newHeight = Integer.parseInt(etHeight.getText().toString());
                    try {
                        //write to database class
                        db.setPersonHeight(newHeight);
                        height = newHeight;
                    } catch (IllegalArgumentException e) {
                        //show a notification
                        Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                        tvHeight.setText(Integer.toString(height));
                        wrongEditBackground(etHeight);
                    }
                } catch (IllegalArgumentException e) {
                    //show notification and old values in textview
                    Toast toast = Toast.makeText(getApplicationContext(), "unaccepted input", Toast.LENGTH_LONG);
                    toast.show();
                    wrongEditBackground(etHeight);
                    if (height != -1) {
                        tvHeight.setText(Integer.toString(height));
                    } else {
                        tvHeight.setText("");
                    }
                }
                //hide keyboard - if one is active/a view is focused:
                try {
                    View focusedView = getCurrentFocus();
                    hideKeyboard(focusedView);
                } catch (RuntimeException e) {
                }
                //remove focus
                ConstraintLayout l = findViewById(R.id.meConfigLayout);
                l.clearFocus();
                updateBMI(height.doubleValue(), weight.doubleValue());
            }
        });







        //EXTENDED CONFIGURATION:

        //calculate BMI
        updateBMI(height.doubleValue(), weight.doubleValue());


        //-- Calories --
        //set current values:
        final TextView tv_energy = (TextView) findViewById(R.id.tvOut_meConfig_calories);
        final Integer energy = db.getPersonEnergyReq();
        if (energy != -1) {
            tv_energy.setText(Integer.toString(energy));
        }

        //submit new calories WITH DONE-key on SoftkeyBoard:
        final EditText etCalories = (EditText) findViewById(R.id.tvOut_meConfig_calories);
        etCalories.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
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
                            //set old/default value:
                            tv_energy.setText(Integer.toString(energy));
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

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        view.clearFocus();
    }

    //change view bottom line to red, due to wrong input
    private void wrongEditBackground(EditText et) {
        et.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
    }

    //set default bottom line color
    private void defaultEditBackground(EditText et) {
        et.getBackground().mutate().setColorFilter(getResources().getColor(R.color.greyLight), PorterDuff.Mode.SRC_ATOP);
    }

    public void updateBMI(Double height, Double weight) {
        Double bmi = weight / ((height * height) / 10000);
        final TextView tv_BMI = (TextView) findViewById(R.id.tvOut_meConfig_bmi);
        bmi = Math.round(bmi * 100.0) / 100.0;
        tv_BMI.setText(bmi.toString());
    }
}