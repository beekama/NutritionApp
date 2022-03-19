package com.example.nutritionapp.configuration;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nutritionapp.WeightTracking;
import com.example.nutritionapp.other.Database;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.R;

import java.util.ArrayList;
import java.util.zip.CheckedInputStream;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class ConfigurationAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<ConfigurationListItem> items;
    final int VIEW_TYPE_HEADER = 0;
    final int VIEW_TYPE_ITEM = 1;
    Database db;

    private static final int JSON_INDENT = 2;
    private static final int REQUEST_CODE_EXPORT  = 0;
    private static final int REQUEST_CODE_IMPORT  = 1;

    public ConfigurationAdapter(Context context, ArrayList<ConfigurationListItem> items, Database db) {
        this.context = context;
        this.items = items;
        this.db = db;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;

        if(viewType == VIEW_TYPE_ITEM) {
            View view = inflater.inflate(R.layout.configuration_list_item, parent, false);
            return new ConfigurationAdapter.LocalViewHolder(view);
        }else if(viewType == VIEW_TYPE_HEADER){
            View view = inflater.inflate(R.layout.configuration_list_header, parent, false);
            return new ConfigurationAdapter.LocalHeaderViewHolder(view);
        }

        throw new AssertionError("Bad ViewType in Personal Information Adapter");
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        /* item at position */
        ConfigurationListItem itemAtCurPos = this.items.get(position);

        /* distinquish header-item and item */
        if(holder instanceof ConfigurationAdapter.LocalHeaderViewHolder){
            ConfigurationAdapter.LocalHeaderViewHolder headerViewHolder = (ConfigurationAdapter.LocalHeaderViewHolder) holder;
            headerViewHolder.textView.setText(itemAtCurPos.text);
            return;
        } else {
            ConfigurationAdapter.LocalViewHolder itemViewHolder = (ConfigurationAdapter.LocalViewHolder) holder;
            itemViewHolder.textView.setText(itemAtCurPos.text);
            switch (itemAtCurPos.type){
                case AGE:
                    if (!itemAtCurPos.sValue.equals("-1")) itemViewHolder.value.setText(itemAtCurPos.sValue);
                    itemViewHolder.lSwitch.setVisibility(View.GONE);
                    itemViewHolder.background.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {

                            // custom dialog
                            final Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.configuration_popup);

                            EditText etAge = (EditText) dialog.findViewById(R.id.input);
                            etAge.setHint("Input Age");
                            etAge.setInputType(InputType.TYPE_CLASS_NUMBER);

                            etAge.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                @Override
                                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                                        try{
                                            String sAge = etAge.getText().toString();
                                            int newAge = Integer.parseInt(sAge);
                                            itemViewHolder.value.setText(sAge);
                                            db.setPersonAge(newAge);
                                            dialog.dismiss();}
                                        catch (NumberFormatException e) {
                                            Toast toast = Toast.makeText(context, "Need Numeric Value as Input for Age, Weight and Height", Toast.LENGTH_LONG);
                                            toast.show();
                                        } catch (IllegalArgumentException e) {
                                            Toast toast = Toast.makeText(context, "Age must be between '18' and '150'", Toast.LENGTH_LONG);
                                            toast.show();
                                        }
                                        return true;
                                    }
                                    return false;
                                }
                            });
                            dialog.show();
                        }
                    });
                    break;
                case GENDER: // todo replace by toggle
                    if (!itemAtCurPos.sValue.equals("-1")) itemViewHolder.value.setText(itemAtCurPos.sValue);
                    itemViewHolder.lSwitch.setVisibility(View.GONE);
                    itemViewHolder.background.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(itemAtCurPos.sValue.equals("male")){
                                itemAtCurPos.sValue = "female";
                                itemViewHolder.value.setText("female");
                                db.setPersonGender("female");
                            } else {
                                itemAtCurPos.sValue = "male";
                                itemViewHolder.value.setText("male");
                                db.setPersonGender("male");
                            }
                        }
                    });
                    break;
                case HEIGHT:
                    if (!itemAtCurPos.sValue.equals("-1")) itemViewHolder.value.setText(itemAtCurPos.sValue);
                    itemViewHolder.lSwitch.setVisibility(View.GONE);
                    itemViewHolder.background.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {

                            // custom dialog
                            final Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.configuration_popup);

                            EditText etHeight = (EditText) dialog.findViewById(R.id.input);
                            etHeight.setHint("Input Height in cm");
                            etHeight .setInputType(InputType.TYPE_CLASS_NUMBER);

                            etHeight.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                @Override
                                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                                        try{
                                            String sHeight = etHeight.getText().toString();
                                            int newHeight = Integer.parseInt(sHeight);
                                            itemViewHolder.value.setText(sHeight);
                                            db.setPersonHeight(newHeight);
                                            dialog.dismiss();}
                                        catch (NumberFormatException e) {
                                            Toast toast = Toast.makeText(context, "Need Numeric Value as Input", Toast.LENGTH_LONG);
                                            toast.show();
                                        } catch (IllegalArgumentException e) {
                                            Toast toast = Toast.makeText(context, "Height must be between 0 and 300 cm", Toast.LENGTH_LONG);
                                            toast.show();
                                        }
                                        return true;
                                    }
                                    return false;
                                }
                            });
                            dialog.show();
                        }
                    });
                    break;
                case WEIGHT:
                    if (!itemAtCurPos.sValue.equals("-1")){
                        float fweight = Integer.parseInt(itemAtCurPos.sValue)/1000.0f;
                        itemViewHolder.value.setText(String.format("%.2f", fweight));
                    }
                    itemViewHolder.lSwitch.setVisibility(View.GONE);
                    itemViewHolder.background.setOnClickListener(v-> {
                        Intent weightActivity = new Intent(context, WeightTracking.class);
                        context.startActivity(weightActivity);
                    });
                    break;
                case CALORIES:
                    if (!itemAtCurPos.sValue.equals("-1")) itemViewHolder.value.setText(itemAtCurPos.sValue);
                    itemViewHolder.lSwitch.setVisibility(View.GONE);
                    itemViewHolder.background.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {

                            // custom dialog
                            final Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.configuration_popup);

                            EditText etCal = (EditText) dialog.findViewById(R.id.input);
                            etCal.setHint("Input Calorie need");
                            etCal.setInputType(InputType.TYPE_CLASS_NUMBER);

                            etCal.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                @Override
                                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                                        try{
                                            String sCal = etCal.getText().toString();
                                            int newCal = Integer.parseInt(sCal);
                                            itemViewHolder.value.setText(sCal);
                                            db.setPersonEnergyReq(newCal);
                                            dialog.dismiss();}
                                        catch (NumberFormatException e) {
                                            Toast toast = Toast.makeText(context, "Need Numeric Value as Input for Energyrequirement", Toast.LENGTH_LONG);
                                            toast.show();
                                        } catch (IllegalArgumentException e) {
                                            Toast toast = Toast.makeText(context, "Value must be over 1000.", Toast.LENGTH_LONG);
                                            toast.show();
                                        }
                                        return true;
                                    }
                                    return false;
                                }
                            });
                            dialog.show();
                        }
                    });
                    break;
                case BMI:
                    itemViewHolder.value.setText(itemAtCurPos.sValue);
                    itemViewHolder.lSwitch.setVisibility(View.GONE);
                    break;
                case IMPORT:
                    itemViewHolder.value.setText(itemAtCurPos.sValue);
                    itemViewHolder.lSwitch.setVisibility(View.GONE);
                    itemViewHolder.background.setOnClickListener(v -> {
                        Intent fileDialog = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        fileDialog.addCategory(Intent.CATEGORY_OPENABLE);
                        fileDialog.setType("text/plain");
                        ((Activity) context).startActivityForResult(fileDialog, REQUEST_CODE_IMPORT);
                    });
                    break;
                case EXPORT:
                    itemViewHolder.value.setText(itemAtCurPos.sValue);
                    itemViewHolder.lSwitch.setVisibility(View.GONE);
                    itemViewHolder.background.setOnClickListener(v -> {
                        Intent fileDialog = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                        fileDialog.addCategory(Intent.CATEGORY_OPENABLE);
                        fileDialog.setType("text/plain");
                        ((Activity) context).startActivityForResult(fileDialog, REQUEST_CODE_EXPORT);
                    });
                    break;
                case LANGUAGE_DE:
                    itemViewHolder.lSwitch.setChecked(itemAtCurPos.bValue);
                    itemViewHolder.value.setVisibility(View.GONE);
                    itemViewHolder.lSwitch.setOnCheckedChangeListener((button, isChecked) -> {
                        if(isChecked){
                            db.setLanguagePref("de");
                        }else{
                            db.setLanguagePref("en");
                        }
                    });
                    break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    @Override
    public int getItemViewType(int position) {
        return items.get(position).type == PersonalInformation.DataType.HEADER ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    static class LocalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View background;
        final TextView textView;
        final TextView value;
        final Switch lSwitch;

        LocalViewHolder(View itemView) {
            super(itemView);
            background = itemView;
            textView = itemView.findViewById(R.id.configuration_item_text);
            value = itemView.findViewById(R.id.configuration_item_input);
            lSwitch = itemView.findViewById(R.id.configuration_slider);
        }

        @Override
        public void onClick(View view) {

        }
    }

    static class LocalHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View background;
        final TextView textView;

        LocalHeaderViewHolder(View itemView) {
            super(itemView);
            background = itemView;
            textView = itemView.findViewById(R.id.configuration_header_text);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
