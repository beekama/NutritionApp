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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionapp.ButtonUtils.UnfocusOnEnter;
import com.example.nutritionapp.R;
import com.example.nutritionapp.foodJournal.AddFoodsLists.GroupListItem;
import com.example.nutritionapp.foodJournal.AddFoodsLists.ListAdapter;
import com.example.nutritionapp.foodJournal.AddFoodsLists.ListFoodItem;
import com.example.nutritionapp.foodJournal.AddFoodsLists.SelectedFoodAdapter;
import com.example.nutritionapp.foodJournal.AddFoodsLists.SelectedFoodItem;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Utils;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class FoodGroupOverview extends AppCompatActivity {

    private static final int DEFAULT_AMOUNT = 100;
    final ArrayList<SelectedFoodItem> selected = new ArrayList<>();
    private boolean editMode = false;

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal_food_group_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        ImageButton toolbarBack = findViewById(R.id.toolbar_back);
        toolbar.setTitle("");
        toolbarTitle.setText("DETAILS");
        setSupportActionBar(toolbar);
        toolbarBack.setOnClickListener((v -> finish()));
        toolbarBack.setImageResource(R.drawable.ic_arrow_back_black_24dp);

        ListView selectedListView = findViewById(R.id.selected_items);
        SelectedFoodAdapter selectedAdapter = new SelectedFoodAdapter(getApplicationContext(), new ArrayList<>());
        selectedListView.setAdapter(selectedAdapter);

        final ListView suggestions = findViewById(R.id.suggestions);
        final ArrayList<GroupListItem> suggestionsByPrevSelected = new ArrayList<>();
        Database db = new Database(this);

        /* set existing items if edit mode */
        int groupId = this.getIntent().getIntExtra("groupId", -1);
        final LocalDateTime loggedAt;
        if (groupId >= 0) {
            this.editMode = true;
            ArrayList<Food> foods = db.getLoggedFoodByGroupId(groupId);
            for (Food f : foods) {
                selected.add(new SelectedFoodItem(f, f.associatedAmount));
            }
            if (!foods.isEmpty()) {
                loggedAt = foods.get(0).loggedAt;
            } else {
                loggedAt = null;
            }
            updateSelectedView(selectedListView, selected);
            updateSuggestionList(db.getSuggestionsForCombination(selected), suggestionsByPrevSelected, suggestions);
        } else {
            loggedAt = null;
        }

        final TextView date = findViewById(R.id.date);
        date.setText(loggedAt.format(Utils.sqliteDatetimeFormat));

        selectedListView.setOnItemLongClickListener((parent, view, position, id) -> {
            final SelectedFoodItem item = (SelectedFoodItem) parent.getItemAtPosition(position);
            selected.remove(item);
            updateSelectedView(selectedListView, selected);
            updateSuggestionList(db.getSuggestionsForCombination(selected), suggestionsByPrevSelected, suggestions);
            return false;
        });

        suggestions.setOnItemClickListener((parent, view, position, id) -> {
            ListFoodItem item = (ListFoodItem) parent.getItemAtPosition(position);
            selected.add(new SelectedFoodItem(item.food, DEFAULT_AMOUNT));
            updateSelectedView(selectedListView, selected);
            updateSuggestionList(db.getSuggestionsForCombination(selected), suggestionsByPrevSelected, suggestions);
        });

    }

    private void updateSelectedView(ListView selectedListView, ArrayList<SelectedFoodItem> alreadySelected) {
        ArrayList<SelectedFoodItem> sfi = new ArrayList<SelectedFoodItem>(alreadySelected);
        SelectedFoodAdapter newAdapter = new SelectedFoodAdapter(getApplicationContext(), sfi);
        selectedListView.setAdapter(newAdapter);
        selectedListView.invalidate();
    }

    private void updateSuggestionList(ArrayList<Food> foods, ArrayList<GroupListItem> suggestionsPrevSelected, ListView suggestions) {
        suggestionsPrevSelected.clear();
        for (Food f : foods) {
            if (suggestionsPrevSelected.contains(f)) {
                continue;
            }
            suggestionsPrevSelected.add(new ListFoodItem(f));
        }

        suggestions.invalidate();
        ListAdapter adapter = new ListAdapter(getApplicationContext(), suggestionsPrevSelected);
        suggestions.setAdapter(adapter);
    }
}