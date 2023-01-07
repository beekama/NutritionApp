package com.example.nutritionapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.nutritionapp.MainActivity;
import com.example.nutritionapp.R;
import com.example.nutritionapp.configuration.ConfigurationAdapter;
import com.example.nutritionapp.configuration.ConfigurationListItem;
import com.example.nutritionapp.other.Database;
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
import java.util.Objects;


public class ConfigurationFragment extends Fragment {

    private static final int JSON_INDENT = 2;
    private static final int REQUEST_CODE_EXPORT = 0;
    private static final int REQUEST_CODE_IMPORT = 1;

    public static final int ENERGY_TARGET = 2000;
    public static final int PROTEIN_TARGET = 50; //50;
    public static final int CARB_TARGET = 30; //700;
    public static final int FAT_TARGET = 20; //84;
    private Database db;
    private View view;
    public ActivityResultLauncher<Intent> onImportDialogResultLauncher;
    public ActivityResultLauncher<Intent> onExportDialogResultLauncher;

    public enum DataType {
        HEIGHT, WEIGHT, AGE, GENDER, LANGUAGE_DE, HEADER, CALORIES, BMI, IMPORT, EXPORT, CURATED_FOODS
    }

    private final ConfigurationAdapter.CallBack callBackUpdate = () -> {
        /* refresh and notify other running activities that language has changed */
        getParentFragmentManager()
                .beginTransaction()
                .detach(ConfigurationFragment.this)
                .attach(ConfigurationFragment.this)
                .commit();
        Intent intent = new Intent("LANGUAGE_CHANGED");
        requireActivity().sendBroadcast(intent);
    };


    public ConfigurationFragment() {
        // Required empty public constructor
    }

    public static ConfigurationFragment newInstance(String param1, String param2) {
        return new ConfigurationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onExportDialogResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    int resultCode = result.getResultCode();
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            if (result.getData() != null && result.getData().getData() != null) {
                                OutputStream outputStream;
                                try {
                                    outputStream = requireActivity().getContentResolver().openOutputStream(result.getData().getData());
                                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
                                    JSONObject json = db.exportDatabase(true, true, true);
                                    bw.write(json.toString(JSON_INDENT));
                                    bw.flush();
                                    bw.close();
                                } catch (IOException e) {
                                    Toast error = Toast.makeText(getActivity(), "IO Exception: " + e.getMessage(), Toast.LENGTH_LONG);
                                    error.show();
                                    break;
                                } catch (JSONException e) {
                                    Toast error = Toast.makeText(getActivity(), "Export failed: " + e.getMessage(), Toast.LENGTH_LONG);
                                    error.show();
                                    break;
                                }
                            }

                            Toast noticeExportSuccess = Toast.makeText(getActivity(), R.string.DatabaseExportSuccessful, Toast.LENGTH_LONG);
                            noticeExportSuccess.show();
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast noticeExportCancel = Toast.makeText(getActivity(), R.string.DatabaseExportCanceled, Toast.LENGTH_LONG);
                            noticeExportCancel.show();
                            break;
                    }
                }
        );

        onImportDialogResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    int resultCode = result.getResultCode();
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            if (result.getData() != null && result.getData().getData() != null) {
                                final InputStream inputStream;
                                final StringBuilder inJson = new StringBuilder();
                                try {
                                    inputStream = requireActivity().getContentResolver().openInputStream(result.getData().getData());
                                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                                    bufferedReader.lines().forEach(inJson::append); // <- how fucking cool is this
                                    bufferedReader.close();
                                    JSONObject json = new JSONObject(inJson.toString());
                                    db.importDatabaseBackup(json);
                                } catch (IOException e) {
                                    Toast error = Toast.makeText(getActivity(), "IO Exception: " + e.getMessage(), Toast.LENGTH_LONG);
                                    error.show();
                                } catch (JSONException e) {
                                    Toast error = Toast.makeText(getActivity(), "JSON Parse Error: " + e.getMessage(), Toast.LENGTH_LONG);
                                    error.show();
                                }
                            }

                            Toast noticeImportSuccess = Toast.makeText(getActivity(), "Database imported successfully!", Toast.LENGTH_LONG);
                            noticeImportSuccess.show();
                            break;

                        case Activity.RESULT_CANCELED:
                            Toast noticeImportCancel = Toast.makeText(getActivity(), "Database imported canceled!", Toast.LENGTH_LONG);
                            noticeImportCancel.show();
                            break;
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_configuration, container, false);

        db = new Database((MainActivity) getActivity());
        String defaultLanguage = db.getLanguagePref();
        if (defaultLanguage != null) LocaleHelper.setLocale(getContext(), defaultLanguage);

        setRecyclerView();
        return view;
    }

    ArrayList<ConfigurationListItem> generateData() {
        ArrayList<ConfigurationListItem> result = new ArrayList<>();

        result.add(new ConfigurationListItem(ConfigurationFragment.DataType.HEADER, getString(R.string.ConfigurationPersonalDataHeader), ""));
        result.add(new ConfigurationListItem(ConfigurationFragment.DataType.AGE, getString(R.string.age), String.valueOf(db.getPersonAge())));
        result.add(new ConfigurationListItem(ConfigurationFragment.DataType.GENDER, getString(R.string.gender), db.getPersonGender()));
        result.add(new ConfigurationListItem(ConfigurationFragment.DataType.HEIGHT, getString(R.string.ConfigurationHeightTitle), String.valueOf(db.getPersonHeight())));
        result.add(new ConfigurationListItem(ConfigurationFragment.DataType.WEIGHT, getString(R.string.ConfigurationWeightTitle), String.valueOf(db.getPersonWeight())));

        result.add(new ConfigurationListItem(ConfigurationFragment.DataType.HEADER, getString(R.string.ConfigurationCalculatedResultsHeader), ""));
        result.add(new ConfigurationListItem(ConfigurationFragment.DataType.BMI, getString(R.string.bmi), String.valueOf(db.getPersonBmi())));
        result.add(new ConfigurationListItem(ConfigurationFragment.DataType.CALORIES, getString(R.string.calories), String.valueOf(db.getPersonEnergyReq(null))));

        result.add(new ConfigurationListItem(ConfigurationFragment.DataType.HEADER, getString(R.string.ConfigurationHealDataHeader), ""));
        result.add(new ConfigurationListItem(ConfigurationFragment.DataType.IMPORT, getString(R.string.import_backup_button), ">"));
        result.add(new ConfigurationListItem(ConfigurationFragment.DataType.EXPORT, getString(R.string.export_data_button), ">"));

        result.add(new ConfigurationListItem(ConfigurationFragment.DataType.HEADER, getString(R.string.languageSectionHeader), ""));
        result.add(new ConfigurationListItem(ConfigurationFragment.DataType.LANGUAGE_DE, getString(R.string.localization_de), db.getLanguagePref() != null && db.getLanguagePref().equals("de")));

        result.add(new ConfigurationListItem(ConfigurationFragment.DataType.CURATED_FOODS, getString(R.string.ConfigurationCuratedFoodTitle), db.getCuratedFoodsPreference() > 0));


        return result;
    }


    void setRecyclerView() {
        RecyclerView personalView = view.findViewById(R.id.mainList);
        personalView.addItemDecoration(new DividerItemDecoration(personalView.getContext(), DividerItemDecoration.VERTICAL));
        ArrayList<ConfigurationListItem> items = generateData();
        ConfigurationAdapter adapter = new ConfigurationAdapter(getContext(), items, db, callBackUpdate, onExportDialogResultLauncher, onImportDialogResultLauncher);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        personalView.setLayoutManager(layoutManager);
        personalView.setAdapter(adapter);
    }


}
