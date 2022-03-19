package com.example.nutritionapp.configuration;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.WeightTracking;
import com.example.nutritionapp.foodJournal.overviewFoodsLists.FoodOverviewAdapter;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Locale;

public class PersonalInformation extends AppCompatActivity {

    private static final int JSON_INDENT = 2;
    private static final int REQUEST_CODE_EXPORT  = 0;
    private static final int REQUEST_CODE_IMPORT  = 1;

    public static int ENERGY_TARGET = 2000;
    public static int PROTEIN_TARGET = 50; //50;
    public static int CARB_TARGET = 30; //700;
    public static int FAT_TARGET = 20; //84;
    private Database db;

    public enum DataType {
        HEIGHT, WEIGHT, AGE, GENDER, LANGUAGE_DE, HEADER, CALORIES, BMI, IMPORT, EXPORT
    }

    private RecyclerView personalView;
    private  ConfigurationAdapter adapter;

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
////        final Button submit = findViewById(R.id.meConfig_submit);
//        final Button exportButton = findViewById(R.id.exportDatabase);
//        final Button importButton = findViewById(R.id.importDatabase);

        personalView = findViewById(R.id.mainList);
        personalView.addItemDecoration(new DividerItemDecoration(personalView.getContext(), DividerItemDecoration.VERTICAL));
        ArrayList<ConfigurationListItem> items = generateData();
        adapter = new ConfigurationAdapter(this, items, db);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        personalView.setLayoutManager(layoutManager);
        personalView.setAdapter(adapter);


//
//        final EditText etGender = findViewById(R.id.et_meConfig_gender);
//        final EditText etAge    = findViewById(R.id.et_meConfig_age);
//        final Button btWeight = findViewById(R.id.bt_meConfig_weight);
//        final EditText etHeight = findViewById(R.id.et_meConfig_height);
//        final EditText etCalories = findViewById(R.id.et_meConfig_calories);
//        final TextView bmiDisplay = findViewById(R.id.tv_meConfig_BMI);
//        final CheckBox languageSelectionDE = findViewById(R.id.languageSelectionDE);


//        ConstraintLayout layout = findViewById(R.id.meConfigLayout);
//        loadAndSetGender(db, etGender);
//        loadAndSetAge(db, etAge);
//        loadAndSetWeight(db, btWeight);
//        loadAndSetHeight(db, etHeight);
//        loadAndSetEnergyReq(db, etCalories);
//        setBmi(db, bmiDisplay);
//
//        btWeight.setOnClickListener(v-> {
//            Intent weightActivity = new Intent(getApplicationContext(), WeightTracking.class);
//            startActivity(weightActivity);
//        });
//
//        submit.setOnClickListener(v -> {
//            collectData(db, etGender, etAge, etHeight);
//            setBmi(db, bmiDisplay);
//            hideKeyboard();
//            layout.clearFocus();
//        });
//
//        /* submit with DONE-key on SoftKeyboard */
//        etCalories.setOnEditorActionListener((v, actionId, event) -> {
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                hideKeyboard();
//                manuallySetEnergyReq(db, etCalories);
//                return true;
//            }
//            return false;
//        });
//
//        if(db.getLanguagePref() != null && db.getLanguagePref().equals("de")){
//            languageSelectionDE.setChecked(true);
//        }
//        languageSelectionDE.setOnCheckedChangeListener((button, isChecked) -> {
//            if(isChecked){
//                db.setLanguagePref("de");
//            }else{
//                db.setLanguagePref("en");
//            }
//        });

//        exportButton.setOnClickListener(v -> {
//            Intent fileDialog = new Intent(Intent.ACTION_CREATE_DOCUMENT);
//            fileDialog.addCategory(Intent.CATEGORY_OPENABLE);
//            fileDialog.setType("text/plain");
//            startActivityForResult(fileDialog, REQUEST_CODE_EXPORT);
//        });
//
//        importButton.setOnClickListener(v -> {
//            Intent fileDialog = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            fileDialog.addCategory(Intent.CATEGORY_OPENABLE);
//            fileDialog.setType("text/plain");
//            startActivityForResult(fileDialog, REQUEST_CODE_IMPORT);
//        });
    }


    ArrayList<ConfigurationListItem> generateData(){
        ArrayList<ConfigurationListItem> result = new ArrayList<>();

        result.add(new ConfigurationListItem(DataType.HEADER, "Personal Data", "")); // todo stringtostrings
        result.add(new ConfigurationListItem(DataType.AGE, "Age", String.valueOf(db.getPersonAge())));
        result.add(new ConfigurationListItem(DataType.GENDER, "Gender", db.getPersonGender()));
        result.add(new ConfigurationListItem(DataType.HEIGHT, "Height in cm", String.valueOf(db.getPersonHeight())));
        result.add(new ConfigurationListItem(DataType.WEIGHT, "Weight in kg", String.valueOf(db.getPersonWeight())));

        result.add(new ConfigurationListItem(DataType.HEADER, "Calculated Results", ""));
        result.add(new ConfigurationListItem(DataType.BMI, "BMI", String.valueOf(db.getPersonBmi())));
        result.add(new ConfigurationListItem(DataType.CALORIES, "Calories", String.valueOf(db.getPersonEnergyReq())));

        result.add(new ConfigurationListItem(DataType.HEADER, " Health Data", ""));
        result.add(new ConfigurationListItem(DataType.IMPORT, "Import", ">"));
        result.add(new ConfigurationListItem(DataType.EXPORT, "Export", ">"));

        result.add(new ConfigurationListItem(DataType.HEADER, "Language Settings", ""));
        result.add(new ConfigurationListItem(DataType.LANGUAGE_DE, "Deutsch", db.getLanguagePref() != null && db.getLanguagePref().equals("de")));


        return result;
    }

//    /* is Called after resume from Weight-View */
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        // update everything affected by weight
//        final Button btWeight = findViewById(R.id.bt_meConfig_weight);
//        final TextView bmiDisplay = findViewById(R.id.tv_meConfig_BMI);
//        loadAndSetWeight(db, btWeight);
//        setBmi(db, bmiDisplay);
//    }

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
                            JSONObject json = db.exportDatabase(true, true, true);
                            bw.write(json.toString(JSON_INDENT));
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

                    Toast noticeExportSuccess = Toast.makeText(this, "Database export successfully!", Toast.LENGTH_LONG);
                    noticeExportSuccess.show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast noticeExportCancel = Toast.makeText(this, "Database export canceled!", Toast.LENGTH_LONG);
                    noticeExportCancel.show();
                    break;
            }
        }else if (requestCode == 1) {
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (data != null  && data.getData() != null) {
                            final InputStream inputStream;
                            final StringBuilder inJson = new StringBuilder();
                            try {
                                inputStream = getContentResolver().openInputStream(data.getData());
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                                bufferedReader.lines().forEach(inJson::append); // <- how fucking cool is this
                                bufferedReader.close();
                                JSONObject json = new JSONObject(inJson.toString());
                                db.importDatabaseBackup(json);
                            } catch (IOException e) {
                                Toast error = Toast.makeText(this,"IO Exception: " + e.getMessage(), Toast.LENGTH_LONG);
                                error.show();
                            } catch (JSONException e){
                                Toast error = Toast.makeText(this,"JSON Parse Error: " + e.getMessage(), Toast.LENGTH_LONG);
                                error.show();
                            }
                        }

                        Toast noticeImportSuccess = Toast.makeText(this, "Database imported successfully!", Toast.LENGTH_LONG);
                        noticeImportSuccess.show();
                        break;

                    case Activity.RESULT_CANCELED:
                        Toast noticeImportCancel = Toast.makeText(this, "Database imported canceled!", Toast.LENGTH_LONG);
                        noticeImportCancel.show();
                        break;
                }
        }
    }

//    private void manuallySetEnergyReq(Database db, EditText etCalories) {
//        try {
//            int calories = Integer.parseInt(etCalories.getText().toString());
//            db.setPersonEnergyReq(calories);
//        } catch (IllegalArgumentException e) {
//            Toast toast = Toast.makeText(getApplicationContext(), "Bad manual Input for calories", Toast.LENGTH_LONG);
//            toast.show();
//            loadAndSetEnergyReq(db, etCalories);
//        }
//    }

    private void collectData(Database db, EditText etGender, EditText etAge, EditText etHeight) {
        try {
            int newAge = Integer.parseInt(etAge.getText().toString());
            String newGender = etGender.getText().toString();
            int newHeight = Integer.parseInt(etHeight.getText().toString());
            db.setPersonGender(newGender);
            db.setPersonAge(newAge);
            db.setPersonHeight(newHeight);
        } catch (NumberFormatException e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Need Numeric Value as Input for Age, Weight and Height", Toast.LENGTH_LONG);
            toast.show();
        } catch (IllegalArgumentException e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Only 'male' or 'female' are supported", Toast.LENGTH_LONG);
            toast.show();
        }
    }

//    @SuppressLint("SetTextI18n")
//    private void loadAndSetWeight(Database db, Button btWeight) {
//        int weight = db.getPersonWeight();
//        if (weight != -1) {
//            float fweight = weight/1000.0f;
//            btWeight.setText(String.format("%.2f", fweight));
//        }
//    }
//
//    @SuppressLint("SetTextI18n")
//    private void loadAndSetAge(Database db, EditText etAge) {
//        int age = db.getPersonAge();
//        if (age != -1) {
//            etAge.setText(Integer.toString(age));
//        }
//    }
//
//    private void loadAndSetGender(Database db, EditText etGender) {
//        String gender = db.getPersonGender();
//        if (!gender.equals("none")) {
//            etGender.setText(gender);
//        }
//    }
//
//    @SuppressLint("SetTextI18n")
//    private void loadAndSetHeight(Database db, EditText etHeight) {
//        int height = db.getPersonHeight();
//        if (height != -1) {
//            etHeight.setText(Integer.toString(height));
//        }
//    }
//
//    @SuppressLint("SetTextI18n")
//    private void loadAndSetEnergyReq(Database db, EditText etEnergy){
//        int energy = db.getPersonEnergyReq();
//        if (energy != -1) {
//            etEnergy.setText(Integer.toString(energy));
//        }
//    }

    private void hideKeyboard() {
        View focusedView = getCurrentFocus();
        if(focusedView == null){
            return;
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        focusedView.clearFocus();
    }

//    public void setBmi(Database db, TextView bmiDisplay) {
//        double bmi = db.getPersonBmi();
//        if(bmi > 0) {
//            bmiDisplay.setText(String.format(Locale.getDefault(), "%d", (int)bmi));
//        }
//    }
}