package com.example.nutritionapp.configuration;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.R;
import com.example.nutritionapp.other.LocaleHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class PersonalInformation extends AppCompatActivity implements UpdateConfig {

    private static final int JSON_INDENT = 2;
    private static final int REQUEST_CODE_EXPORT  = 0;
    private static final int REQUEST_CODE_IMPORT  = 1;

    public static final int ENERGY_TARGET = 2000;
    public static final int PROTEIN_TARGET = 50; //50;
    public static final int CARB_TARGET = 30; //700;
    public static final int FAT_TARGET = 20; //84;
    private Database db;

    @Override
    public void updateBMI() {
        setRecyclerView();
    }

    /* refresh and notify other running activities that language has changed */
    @Override
    public void refresh() {
        recreate();
        Intent intent = new Intent("LANGUAGE_CHANGED");
        sendBroadcast(intent);
    }

    public enum DataType {
        HEIGHT, WEIGHT, AGE, GENDER, LANGUAGE_DE, HEADER, CALORIES, BMI, IMPORT, EXPORT, CURATED_FOODS
    }

    @SuppressLint("ResourceAsColor")
    public void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);

        db = new Database(this);

        String defaultLanguage = db.getLanguagePref();
        if (defaultLanguage != null) LocaleHelper.setLocale(this, defaultLanguage);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        final TextView toolbarTitle = findViewById(R.id.toolbar_title);
        final ImageButton backHome = findViewById(R.id.toolbar_back);

        backHome.setOnClickListener((v -> finishAfterTransition()));
        toolbar.setTitle("");
        toolbarTitle.setText(R.string.configurationTitle);
        backHome.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);

        setRecyclerView();
    }


    ArrayList<ConfigurationListItem> generateData(){
        ArrayList<ConfigurationListItem> result = new ArrayList<>();

        result.add(new ConfigurationListItem(DataType.HEADER, getString(R.string.ConfigurationPersonalDataHeader), ""));
        result.add(new ConfigurationListItem(DataType.AGE, getString(R.string.age), String.valueOf(db.getPersonAge())));
        result.add(new ConfigurationListItem(DataType.GENDER, getString(R.string.gender), db.getPersonGender()));
        result.add(new ConfigurationListItem(DataType.HEIGHT, getString(R.string.ConfigurationHeightTitle), String.valueOf(db.getPersonHeight())));
        result.add(new ConfigurationListItem(DataType.WEIGHT, getString(R.string.ConfigurationWeightTitle), String.valueOf(db.getPersonWeight())));

        result.add(new ConfigurationListItem(DataType.HEADER, getString(R.string.ConfigurationCalculatedResultsHeader), ""));
        result.add(new ConfigurationListItem(DataType.BMI, getString(R.string.bmi), String.valueOf(db.getPersonBmi())));
        result.add(new ConfigurationListItem(DataType.CALORIES, getString(R.string.calories), String.valueOf(db.getPersonEnergyReq(null))));

        result.add(new ConfigurationListItem(DataType.HEADER, getString(R.string.ConfigurationHealDataHeader), ""));
        result.add(new ConfigurationListItem(DataType.IMPORT, getString(R.string.import_backup_button), ">"));
        result.add(new ConfigurationListItem(DataType.EXPORT, getString(R.string.export_data_button), ">"));

        result.add(new ConfigurationListItem(DataType.HEADER, getString(R.string.languageSectionHeader), ""));
        result.add(new ConfigurationListItem(DataType.LANGUAGE_DE, getString(R.string.localization_de), db.getLanguagePref() != null && db.getLanguagePref().equals("de")));

        result.add(new ConfigurationListItem(DataType.CURATED_FOODS, getString(R.string.ConfigurationCuratedFoodTitle), db.getCuratedFoodsPreference() > 0));


        return result;
    }

    @Override
    protected void attachBaseContext(Context base){
        super.attachBaseContext(LocaleHelper.setDefaultLanguage(base));
    }

    /* is Called after resume from Weight-View */
    @Override
    protected void onResume() {
        super.onResume();

        // update everything affected by weight - update Data
        setRecyclerView();
    }

    void setRecyclerView(){
        RecyclerView personalView = findViewById(R.id.mainList);
        personalView.addItemDecoration(new DividerItemDecoration(personalView.getContext(), DividerItemDecoration.VERTICAL));
        ArrayList<ConfigurationListItem> items = generateData();
        ConfigurationAdapter adapter = new ConfigurationAdapter(this, items, db, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        personalView.setLayoutManager(layoutManager);
        personalView.setAdapter(adapter);
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

                    Toast noticeExportSuccess = Toast.makeText(this, R.string.DatabaseExportSuccessful, Toast.LENGTH_LONG);
                    noticeExportSuccess.show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast noticeExportCancel = Toast.makeText(this, R.string.DatabaseExportCanceled, Toast.LENGTH_LONG);
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


}

interface UpdateConfig {
    void updateBMI();
    void refresh();
}
