package com.example.nutritionapp.foodJournal.OverviewFoodsLists;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.example.nutritionapp.ButtonUtils.UnfocusOnEnter;
import com.example.nutritionapp.foodJournal.AddFoodsLists.*;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Food;


import java.util.ArrayList;

public class DialogFoodSelector extends Dialog {

    Activity context;
    public Food selectedFood = null;
    ListView searchResultListView;
    ArrayList<GroupListItem> inputList;

    public DialogFoodSelector(@NonNull Activity context) {
        super(context);
        this.context = context;
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal_dialog_food_selector);

        final EditText searchFieldEditText = findViewById(R.id.searchField);
        searchResultListView = findViewById(R.id.searchResults);
        inputList = new ArrayList<>();
        final Database db = new Database(context);


        TextWatcher filterNameTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSearchList(db.getFoodsByPartialName(s.toString()));
            }
        };

        searchFieldEditText.addTextChangedListener(filterNameTextWatcher);
        searchFieldEditText.setOnKeyListener(new UnfocusOnEnter());
        searchFieldEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        searchResultListView.setOnItemClickListener((parent, view, position, id) -> {
            if (searchFieldEditText.hasFocus()) {
                searchFieldEditText.clearFocus();
            }
            this.selectedFood = ((ListFoodItem) parent.getItemAtPosition(position)).food;
            //db.close();
            dismiss();
        });

        Button cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> {
            if (searchFieldEditText.hasFocus()) {
                searchFieldEditText.clearFocus();
            }
            this.selectedFood = null;
            //db.close();
            dismiss();
        });
    }


    private void updateSearchList(ArrayList<Food> foods) {
        inputList.clear();
        for(Food f : foods){
            inputList.add(new ListFoodItem(f));
        }

        SelectableFoodListAdapter adapter = new SelectableFoodListAdapter(this.getContext(), inputList);
        searchResultListView.setAdapter(adapter);
        searchResultListView.invalidate();
    }

}
