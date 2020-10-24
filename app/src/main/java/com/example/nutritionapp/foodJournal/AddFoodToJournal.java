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

import com.example.nutritionapp.BuildConfig;
import com.example.nutritionapp.ButtonUtils.UnfocusOnEnter;
import com.example.nutritionapp.foodJournal.AddFoodsLists.*;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Utils;


import java.time.LocalDateTime;
import java.util.ArrayList;

public class AddFoodToJournal extends AppCompatActivity {

    private static final int DEFAULT_AMOUNT = 100;
    final ArrayList<SelectedFoodItem> selected = new ArrayList<>();
    private boolean editMode = false;

    protected void onCreate(Bundle savedInstanceState) {
        //splash screen: - show only when needed
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal_add_food);

        final EditText searchFieldEditText = findViewById(R.id.searchField);
        final ListView lv = findViewById(R.id.listview);
        final ArrayList<GroupListItem> inputList = new ArrayList<>();
        final Database db = new Database(this);


        TextWatcher filterNameTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Food> foods = db.getFoodsByPartialName(s.toString());
                updateSearchList(foods, inputList, lv);
            }
        };

        searchFieldEditText.addTextChangedListener(filterNameTextWatcher);
        searchFieldEditText.setOnKeyListener(new UnfocusOnEnter() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (super.onKey(v, keyCode, event)) {
                    //switchToSuggestionView(db, suggestionsByPrevSelected, suggestions, lv);
                    return true;
                }
                return false;
            }
        });
        searchFieldEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            } else {
                //switchToSearchView(lv, suggestions);
            }
        });

        Button cancel = findViewById(R.id.cancel);
        Button confirm = findViewById(R.id.confirm);

        cancel.setOnClickListener(v -> {
            if (searchFieldEditText.hasFocus()) {
                searchFieldEditText.clearFocus();
                //switchToSuggestionView(db, new ArrayList<>(), suggestions, lv);
            } else {
                db.close();
                finish();
            }
        });
        confirm.setOnClickListener(v -> {
            if(this.editMode){
                //db.updateFoodGroup(selected, groupId, loggedAt);
            }else {
                db.logExistingFoods(selected, null, null);
            }
            db.close();
            finish();
        });
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
        lv.setVisibility(View.GONE);
        suggestions.setVisibility(View.VISIBLE);
        lv.invalidate();
        suggestions.invalidate();
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

}
