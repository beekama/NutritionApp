package com.example.nutritionapp.foodJournal;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritionapp.ButtonUtils.UnfocusOnEnter;
import com.example.nutritionapp.foodJournal.AddFoodsLists.*;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Food;

import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;

public class AddFoodToJournal extends AppCompatActivity {

    final ArrayList<SelectedFoodItem> selected = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        //splash screen: - show only when needed
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal_add_food);

        EditText searchFieldEditText = findViewById(R.id.searchField);
        ListView selectedListView = findViewById(R.id.selected_items);
        SelectedFoodAdapter selectedAdapter = new SelectedFoodAdapter(getApplicationContext(), new ArrayList<>());
        selectedListView.setAdapter(selectedAdapter);

        final ListView lv = findViewById(R.id.listview);
        final ListView suggestions = findViewById(R.id.suggestions);

        final ArrayList<GroupListItem> inputList = new ArrayList<>();
        final ArrayList<GroupListItem> suggestionsByPrevSelected = new ArrayList<>();
        Database db = new Database(this);

        TextWatcher filterNameTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Food> foods = db.getFoodsByPartialName(s.toString());
                updateSearchList(foods, inputList, lv);
            }
        };

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListFoodItem item = (ListFoodItem)parent.getItemAtPosition(position);
                selected.add(new SelectedFoodItem(item.food, 100));
                updateSelectedView(selectedListView, selected);
            }
        });

        selectedListView.setOnItemLongClickListener((parent, view, position, id) -> {
            selected.remove(parent.getItemAtPosition(position));
            updateSelectedView(selectedListView, selected);
            updateSuggestionList(db.getSuggestionsForCombination(selected), suggestionsByPrevSelected, suggestions);
            return false;
        });

        suggestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListFoodItem item = (ListFoodItem)parent.getItemAtPosition(position);
                selected.add(new SelectedFoodItem(item.food, 100));
                updateSelectedView(selectedListView, selected);
                updateSuggestionList(db.getSuggestionsForCombination(selected), suggestionsByPrevSelected, suggestions);
            }
        });

        searchFieldEditText.addTextChangedListener(filterNameTextWatcher);
        searchFieldEditText.setOnKeyListener(new UnfocusOnEnter(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(super.onKey(v, keyCode, event)){
                    switchToSuggestionView(db, suggestionsByPrevSelected, suggestions, lv);
                    return true;
                }
                return false;
            }
        });
        searchFieldEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }else{
                switchToSearchView(lv, suggestions);
            }
        });

        Button cancel = findViewById(R.id.cancel);
        Button confirm = findViewById(R.id.confirm);

        cancel.setOnClickListener(v -> {
            View sf = findViewById(R.id.searchField);
            if(sf.hasFocus()){
                sf.clearFocus();
                switchToSuggestionView(db, new ArrayList<>(), suggestions, lv);
            }else {
                db.close();
                finish();
            }
        });
        confirm.setOnClickListener(v -> { db.logExistingFoods(selected, null, null); db.close(); finish();});
    }

    /* TODO amount selection (grams) */
    private void updateSelectedView(ListView selectedListView, ArrayList<SelectedFoodItem> alreadySelected) {
        ArrayList<SelectedFoodItem> sfi = new ArrayList<SelectedFoodItem>(alreadySelected);
        SelectedFoodAdapter newAdapter = new SelectedFoodAdapter(getApplicationContext(), sfi);
        selectedListView.setAdapter(newAdapter);
        selectedListView.invalidate();
    }

    private void switchToSearchView(ListView lv, ListView suggestions) {
        lv.setVisibility(View.VISIBLE);
        suggestions.setVisibility(View.GONE);
        ListAdapter adapter = new ListAdapter(getApplicationContext(), new ArrayList<>());
        suggestions.setAdapter(adapter);
        lv.invalidate();
        suggestions.invalidate();
    }

    private void switchToSuggestionView(Database db, ArrayList<GroupListItem> suggestionsByPrevSelected, ListView suggestions, ListView lv) {
        updateSuggestionList(db.getSuggestionsForCombination(selected), suggestionsByPrevSelected, suggestions);
        lv.setVisibility(View.GONE);
        suggestions.setVisibility(View.VISIBLE);
        lv.invalidate();
        suggestions.invalidate();
    }

    private void updateSuggestionList(ArrayList<Food> foods, ArrayList<GroupListItem> suggestionsPrevSelected, ListView suggestions) {
        suggestionsPrevSelected.clear();
        for(Food f : foods){
            if(suggestionsPrevSelected.contains(f)){
                continue;
            }
            suggestionsPrevSelected.add(new ListFoodItem(f));
        }

        suggestions.invalidate();
        ListAdapter adapter = new ListAdapter(getApplicationContext(), suggestionsPrevSelected);
        suggestions.setAdapter(adapter);
    }

    private void updateSearchList(ArrayList<Food> foods, ArrayList<GroupListItem> inputList, ListView lv) {
        inputList.clear();
        for(Food f : foods){
            inputList.add(new ListFoodItem(f));
        }

        lv.invalidate();
        ListAdapter adapter = new ListAdapter(getApplicationContext(), inputList);
        lv.setAdapter(adapter);
    }

/*    public synchronized void logExistingFoods(ArrayList<SelectedFoodItem> selectedSoFarItems, LocalDateTime d, Object hackyhack) {
        *//* This functions add a list of create_foods to the journal at a given date *//*
        ArrayList<Food> selectedSoFar = new ArrayList<>();
        for (SelectedFoodItem item : selectedSoFarItems) {
            Food f = item.food;
            f.setAssociatedAmount(item.amount);
            selectedSoFar.add(item.food);
        }
        logExistingFoods(selectedSoFar, d);
    }

    private void updatePieChartData(ArrayList<SelectedFoodItem> selectedSoFarItems, LocalDateTime d ){
        *//* add selected Food to (test)chart *//*
        ArrayList<Food> selected = new ArrayList<>();
        for (SelectedFoodItem item : selectedSoFarItems){
            Food f = item.food
        }
    }*/
}
