package com.example.nutritionapp.foodJournal;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritionapp.ButtonUtils.HideKeyboardOnFocusLoss;
import com.example.nutritionapp.ButtonUtils.UnfocusOnEnter;
import com.example.nutritionapp.foodJournal.AddFoodsLists.*;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Utils;

import java.util.ArrayList;

public class AddFoodToJournal extends AppCompatActivity {

    final ArrayList<Food> selected = new ArrayList<Food>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal_add_food);

        EditText searchFieldEditText = findViewById(R.id.searchField);
        TextView selectedTextView = findViewById(R.id.selected_items);

        final ListView lv = (ListView) findViewById(R.id.listview);
        final ListView suggestions = (ListView) findViewById(R.id.suggestions);

        final ArrayList<GenericListItem> inputList = new ArrayList<GenericListItem>();
        final ArrayList<GenericListItem> suggestionsByPrevSelected = new ArrayList<GenericListItem>();
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
                selected.add(item.food);
                selectedTextView.setText(Utils.foodArrayListToString(selected));
                selectedTextView.invalidate();
            }
        });

        suggestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListFoodItem item = (ListFoodItem)parent.getItemAtPosition(position);
                selected.add(item.food);
                selectedTextView.setText(Utils.foodArrayListToString(selected));
                selectedTextView.invalidate();
                updateSuggestionList(db.getSuggestionsForCombination(selected), suggestionsByPrevSelected, suggestions);
            }
        });

        searchFieldEditText.addTextChangedListener(filterNameTextWatcher);
        searchFieldEditText.setOnKeyListener(new UnfocusOnEnter(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(super.onKey(v, keyCode, event)){
                    updateSuggestionList(db.getSuggestionsForCombination(selected), suggestionsByPrevSelected, suggestions);
                    lv.setVisibility(View.GONE);
                    suggestions.setVisibility(View.VISIBLE);
                    lv.invalidate();
                    suggestions.invalidate();
                    return true;
                }
                return false;
            }
        });
        searchFieldEditText.setOnFocusChangeListener(new HideKeyboardOnFocusLoss());

        Button cancel = findViewById(R.id.cancel);
        Button confirm = findViewById(R.id.confirm);

        cancel.setOnClickListener(v -> { db.close(); finish(); });
        confirm.setOnClickListener(v -> { db.logExistingFoods(selected, null); db.close(); finish();});
    }

    private void updateSuggestionList(ArrayList<Food> foods, ArrayList<GenericListItem> suggestionsPrevSelected, ListView suggestions) {
        suggestionsPrevSelected.clear();
        for(Food f : foods){
            suggestionsPrevSelected.add(new ListFoodItem(f));
        }

        /* I know this looks bad but notifyDatasetChanged() is way too slow */
        suggestions.invalidate();
        ListAdapter adapter = new ListAdapter(getApplicationContext(), suggestionsPrevSelected);
        suggestions.setAdapter(adapter);
    }

    private void updateSearchList(ArrayList<Food> foods, ArrayList<GenericListItem> inputList, ListView lv) {
        inputList.clear();
        for(Food f : foods){
            inputList.add(new ListFoodItem(f));
        }

        /* I know this looks bad but notifyDatasetChanged() is way too slow */
        lv.invalidate();
        ListAdapter adapter = new ListAdapter(getApplicationContext(), inputList);
        lv.setAdapter(adapter);
    }
}
