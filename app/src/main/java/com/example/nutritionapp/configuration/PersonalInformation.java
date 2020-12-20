package com.example.nutritionapp.configuration;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.Duration;
import java.util.Locale;

public class PersonalInformation extends AppCompatActivity {

    private Database db;

    @SuppressLint("ResourceAsColor")
    public void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);

        db = new Database(this);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        final TextView toolbarTitle = findViewById(R.id.toolbar_title);
        final ImageButton backHome = findViewById(R.id.toolbar_back);

        backHome.setOnClickListener((v -> finish()));
        toolbar.setTitle("");
        toolbarTitle.setText(R.string.configurationTitle);
        backHome.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        final Button submit = findViewById(R.id.meConfig_submit);
        final Button exportButton = findViewById(R.id.exportDatabase);
        final Button importButton = findViewById(R.id.importDatabase);

        final EditText etGender = findViewById(R.id.et_meConfig_gender);
        final EditText etAge    = findViewById(R.id.et_meConfig_age);
        final EditText etWeight = findViewById(R.id.et_meConfig_weight);
        final EditText etHeight = findViewById(R.id.et_meConfig_height);
        final EditText etCalories = findViewById(R.id.et_meConfig_calories);
        final TextView bmiDisplay = findViewById(R.id.tv_meConfig_BMI);
        final CheckBox languageSelectionDE = findViewById(R.id.languageSelectionDE);

        ConstraintLayout layout = findViewById(R.id.meConfigLayout);
        loadAndSetGender(db, etGender);
        loadAndSetAge(db, etAge);
        loadAndSetWeight(db, etWeight);
        loadAndSetHeight(db, etHeight);
        loadAndSetEnergyReq(db, etCalories);
        setBmi(db, bmiDisplay);

        submit.setOnClickListener(v -> {
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

        exportButton.setOnClickListener(v -> {
            Intent fileDialog = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            fileDialog.addCategory(Intent.CATEGORY_OPENABLE);
            fileDialog.setType("text/plain");
            startActivityForResult(fileDialog, 0);
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    if (data != null  && data.getData() != null) {
                        OutputStream outputStream;
                        try {
                            outputStream = getContentResolver().openOutputStream(data.getData());
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
                            JSONObject json = db.exportDatabase(true, true);
                            bw.write(json.toString(2));
                            bw.flush();
                            bw.close();
                        } catch (IOException e) {
                            Toast error = Toast.makeText(this,"IO Exception: " + e.getMessage(), Toast.LENGTH_LONG);
                            error.show();
                        }catch (JSONException e) {
                            Toast error = Toast.makeText(this,"Export failed: " + e.getMessage(), Toast.LENGTH_LONG);
                            error.show();
                        }
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        }
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