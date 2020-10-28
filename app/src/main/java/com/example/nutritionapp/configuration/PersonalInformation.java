package com.example.nutritionapp.configuration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.R;


import java.util.Locale;

public class PersonalInformation extends AppCompatActivity {

    @SuppressLint("ResourceAsColor")
    public void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);

        final Database db = new Database(this);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        final TextView toolbarTitle = findViewById(R.id.toolbar_title);
        final ImageButton backHome = (ImageButton) findViewById(R.id.toolbar_back);

        backHome.setOnClickListener((v -> finish()));
        toolbar.setTitle("");
        toolbarTitle.setText("PROFILE");
        backHome.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        final Button submit = (Button) findViewById(R.id.meConfig_submit);

        final EditText etGender = (EditText) findViewById(R.id.et_meConfig_gender);
        final EditText etAge    = (EditText) findViewById(R.id.et_meConfig_age);
        final EditText etWeight = (EditText) findViewById(R.id.et_meConfig_weight);
        final EditText etHeight = (EditText) findViewById(R.id.et_meConfig_height);
        final EditText etCalories = (EditText) findViewById(R.id.et_meConfig_calories);
        final TextView bmiDisplay = (TextView) findViewById(R.id.tv_meConfig_BMI);
        final CheckBox languageSelectionDE = (CheckBox) findViewById(R.id.languageSelectionDE);

        ConstraintLayout layout = findViewById(R.id.meConfigLayout);
        loadAndSetGender(db, etGender);
        loadAndSetAge(db, etAge);
        loadAndSetWeight(db, etWeight);
        loadAndSetHeight(db, etHeight);
        loadAndSetEnergyReq(db, etCalories);
        setBmi(db, bmiDisplay);

        submit.setOnClickListener((View.OnClickListener) v -> {
            collectData(db, etGender, etAge, etWeight, etHeight);
            setBmi(db, bmiDisplay);
            hideKeyboard();
            layout.clearFocus();
        });

        /* submit with DONE-key on SoftKeyboard */
        etCalories.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard();
                manuallySetEnergyReq(db, etCalories);
                return true;
            }
            return false;
        });

        if(db.getLanguagePref() != null && db.getLanguagePref().equals("de")){
            languageSelectionDE.setChecked(true);
        }
        languageSelectionDE.setOnCheckedChangeListener((button, isChecked) -> {
            if(isChecked){
                db.setLanguagePref("de");
            }else{
                db.setLanguagePref("en");
            }
        });
    }

    private void manuallySetEnergyReq(Database db, EditText etCalories) {
        try {
            int calories = Integer.parseInt(etCalories.getText().toString());
            db.setPersonEnergyReq(calories);
        } catch (IllegalArgumentException e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Bad manual Input for calories", Toast.LENGTH_LONG);
            toast.show();
            loadAndSetEnergyReq(db, etCalories);
        }
    }

    private void collectData(Database db, EditText etGender, EditText etAge, EditText etWeight, EditText etHeight) {
        try {
            int newAge = Integer.parseInt(etAge.getText().toString());
            String newGender = etGender.getText().toString();
            int newWeight = Integer.parseInt(etWeight.getText().toString());
            int newHeight = Integer.parseInt(etHeight.getText().toString());
            db.setPersonGender(newGender);
            db.setPersonAge(newAge);
            db.setPersonWeight(newWeight);
            db.setPersonHeight(newHeight);
        } catch (NumberFormatException e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Need Numeric Value as Input for Age, Weight and Height", Toast.LENGTH_LONG);
            toast.show();
        } catch (IllegalArgumentException e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Only 'male' or 'female' are supported", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadAndSetWeight(Database db, EditText etWeight) {
        int weight = db.getPersonWeight();
        if (weight != -1) {
            etWeight.setText(Integer.toString(weight));
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadAndSetAge(Database db, EditText etAge) {
        int age = db.getPersonAge();
        if (age != -1) {
            etAge.setText(Integer.toString(age));
        }
    }

    private void loadAndSetGender(Database db, EditText etGender) {
        String gender = db.getPersonGender();
        if (!gender.equals("none")) {
            etGender.setText(gender);
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadAndSetHeight(Database db, EditText etHeight) {
        int height = db.getPersonHeight();
        if (height != -1) {
            etHeight.setText(Integer.toString(height));
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadAndSetEnergyReq(Database db, EditText etEnergy){
        int energy = db.getPersonEnergyReq();
        if (energy != -1) {
            etEnergy.setText(Integer.toString(energy));
        }
    }

    private void hideKeyboard() {
        View focusedView = getCurrentFocus();
        if(focusedView == null){
            return;
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        focusedView.clearFocus();
    }

    public void setBmi(Database db, TextView bmiDisplay) {
        double bmi = db.getPersonBmi();
        if(bmi > 0) {
            bmiDisplay.setText(String.format(Locale.getDefault(), "%d", (int)bmi));
        }
    }
}