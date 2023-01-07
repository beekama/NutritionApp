package com.example.nutritionapp.configuration;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.nutritionapp.MainActivity;
import com.example.nutritionapp.WeightTrackingFragment;
import com.example.nutritionapp.ui.ConfigurationFragment;
import com.example.nutritionapp.other.Database;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.LocaleHelper;
import com.example.nutritionapp.other.SimpleInputPopup;

import java.util.ArrayList;

public class ConfigurationAdapter extends RecyclerView.Adapter {

    private final Context context;
    private final ArrayList<ConfigurationListItem> items;
    final int VIEW_TYPE_HEADER = 0;
    final int VIEW_TYPE_ITEM = 1;
    final Database db;
    private ConfigurationAdapter.CallBack callBack;
    private Fragment parentFragment;

    private static final int JSON_INDENT = 2;
    private static final int REQUEST_CODE_EXPORT  = 0;
    private static final int REQUEST_CODE_IMPORT  = 1;

    public ConfigurationAdapter(Context context, ArrayList<ConfigurationListItem> items, Database db, ConfigurationAdapter.CallBack callBack, Fragment parentFragment) {
        this.context = context;
        this.items = items;
        this.db = db;
        this.callBack = callBack;
        this.parentFragment = parentFragment;
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
        ConfigurationListItem currentItem = this.items.get(position);

        /* distinguish header-item and item */
        if(holder instanceof ConfigurationAdapter.LocalHeaderViewHolder){
            ConfigurationAdapter.LocalHeaderViewHolder headerViewHolder = (ConfigurationAdapter.LocalHeaderViewHolder) holder;
            headerViewHolder.textView.setText(currentItem.text);
        } else {
            ConfigurationAdapter.LocalViewHolder itemViewHolder = (ConfigurationAdapter.LocalViewHolder) holder;
            itemViewHolder.textView.setText(currentItem.text);

            /* TODO WTF IS THIS DOING */
            if (!currentItem.stringValue.equals("-1")){
                itemViewHolder.inputTextView.setText(currentItem.stringValue);
            }
            itemViewHolder.configurationSlider.setVisibility(View.GONE);

            switch (currentItem.type){
                case AGE:
                case HEIGHT:
                case CALORIES:
                    setListenChangeWithInputDialog(currentItem, itemViewHolder);
                    break;
                case GENDER:
                    itemViewHolder.root.setOnClickListener(v -> {
                        if(currentItem.stringValue.equals("male")){
                            currentItem.stringValue = "female";
                            itemViewHolder.inputTextView.setText(R.string.female);
                            db.setPersonGender("female");
                        } else {
                            currentItem.stringValue = "male";
                            itemViewHolder.inputTextView.setText(R.string.male);
                            db.setPersonGender("male");
                        }
                    });
                    break;
                case WEIGHT:
                    itemViewHolder.root.setOnClickListener(v-> {
                        Class weightFragmentClass = WeightTrackingFragment.class;
                        FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager();
                        try{
                            fragmentManager.beginTransaction()
                                    .replace(R.id.main_fragment_container, (Fragment) weightFragmentClass.newInstance())
                                    .addToBackStack(null)
                                    .commit();
                        } catch (IllegalAccessException | InstantiationException e){
                            e.printStackTrace();
                        }

                    });
                    break;
                case BMI:
                    itemViewHolder.inputTextView.setText(currentItem.stringValue);
                    break;
                case IMPORT:
                    itemViewHolder.inputTextView.setText(currentItem.stringValue);
                    setListenFileDialog(itemViewHolder, Intent.ACTION_OPEN_DOCUMENT);
                    break;
                case EXPORT:
                    itemViewHolder.inputTextView.setText(currentItem.stringValue);
                    setListenFileDialog(itemViewHolder, Intent.ACTION_CREATE_DOCUMENT);
                    break;
                case LANGUAGE_DE:
                    itemViewHolder.configurationSlider.setVisibility(View.VISIBLE);
                    itemViewHolder.configurationSlider.setChecked(currentItem.bValue);
                    itemViewHolder.configurationSlider.setOnCheckedChangeListener((button, isChecked) -> {
                        if(isChecked){
                            db.setLanguagePref("de");
                            LocaleHelper.setLocale(context.getApplicationContext(), "de");
                            callBack.refresh();
                        }else{
                            db.setLanguagePref("en");
                            LocaleHelper.setLocale(context.getApplicationContext(), "en");
                            callBack.refresh();
                        }
                    });
                    break;
                case CURATED_FOODS:
                    itemViewHolder.configurationSlider.setVisibility(View.VISIBLE);
                    itemViewHolder.configurationSlider.setChecked(currentItem.bValue);
                    itemViewHolder.configurationSlider.setOnCheckedChangeListener((button, isChecked) -> {
                        if(isChecked){
                            db.setCuratedFoodsPreference(1);
                        }else{
                            db.setCuratedFoodsPreference(0);
                        }
                    });
                    break;
            }
        }

    }

    private void setListenFileDialog(LocalViewHolder itemViewHolder, String action) {
        itemViewHolder.root.setOnClickListener(v -> {

            /* create file dialog */
            Intent fileDialog = new Intent(action);
            fileDialog.addCategory(Intent.CATEGORY_OPENABLE);
            fileDialog.setType("text/plain");

            /* start file dialog activity */
            Activity parentActivity = (Activity) context;
            if(action.equals(Intent.ACTION_OPEN_DOCUMENT)){
                parentFragment.startActivityForResult(fileDialog, REQUEST_CODE_IMPORT);
                //parentActivity.startActivityForResult(fileDialog, REQUEST_CODE_IMPORT);
            }else {
                parentFragment.startActivityForResult(fileDialog, REQUEST_CODE_IMPORT);
               // parentActivity.startActivityForResult(fileDialog, REQUEST_CODE_EXPORT);
            }

        });
    }

    private void setListenChangeWithInputDialog(ConfigurationListItem itemAtCurPos, LocalViewHolder itemViewHolder) {
        itemViewHolder.configurationSlider.setVisibility(View.GONE);
        itemViewHolder.root.setOnClickListener(v -> {
            final SimpleInputPopup inputPopup = new SimpleInputPopup(context, itemAtCurPos.type.toString(), itemAtCurPos.type.toString(), InputType.TYPE_CLASS_NUMBER);
            inputPopup.setOnDismissListener(dialog -> {
                itemAtCurPos.numberValue = inputPopup.numberValue;
                itemAtCurPos.stringValue = inputPopup.getStringValue();
                itemViewHolder.inputTextView.setText(itemAtCurPos.stringValue);

                /* save to database */
                switch (itemAtCurPos.type){
                    case AGE:
                        db.setPersonAge((int) itemAtCurPos.numberValue);
                        break;
                    case HEIGHT:
                        db.setPersonHeight((int) itemAtCurPos.numberValue);
                        break;
                    case CALORIES:
                        db.setPersonEnergyReq((int) itemAtCurPos.numberValue, null);
                        break;
                }
            });
            inputPopup.show();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    @Override
    public int getItemViewType(int position) {
        return items.get(position).type == ConfigurationFragment.DataType.HEADER ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    static class LocalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View root;
        final TextView textView;
        final TextView inputTextView;
        final Switch configurationSlider;

        LocalViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            textView = itemView.findViewById(R.id.configuration_item_text);
            inputTextView = itemView.findViewById(R.id.configuration_item_input);
            configurationSlider = itemView.findViewById(R.id.configuration_slider);
        }

        @Override
        public void onClick(View view) {

        }
    }

    static class LocalHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View root;
        final TextView textView;

        LocalHeaderViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            textView = itemView.findViewById(R.id.configuration_header_text);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public interface CallBack{
        void refresh();
    }
}


