package com.example.nutritionapp.foodJournal;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FoodGroupOverview extends AppCompatActivity {

    private static final int DEFAULT_AMOUNT = 100;
    final ArrayList<SelectedFoodItem> selected = new ArrayList<>();
    private boolean editMode = false;

    private TextView dateView;
    private TextView timeView;

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

        dateView = findViewById(R.id.date);
        timeView = findViewById(R.id.time);

        TextView nutOverviewPlaceholder = findViewById(R.id.nutritionOverview);
        nutOverviewPlaceholder.setText("PLACEHOLDER PLACEHOLDER PLACEHOLDER");
        Button addNewButton = findViewById(R.id.addButton);
        addNewButton.setText("Add..");

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
                loggedAt = LocalDateTime.now();;
            }
            updateSelectedView(selectedListView, selected);
            updateSuggestionList(db.getSuggestionsForCombination(selected), suggestionsByPrevSelected, suggestions);
        } else {
            loggedAt = LocalDateTime.now();
        }

        dateView.setText(loggedAt.format(Utils.sqliteDateFormat));
        timeView.setText(loggedAt.format(Utils.sqliteTimeFormat));
        dateView.setOnClickListener(v -> dateUpdateDialog(loggedAt));
        timeView.setOnClickListener(v -> timeUpdateDialog(loggedAt));

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


    private void dateUpdateDialog(final LocalDateTime loggedAt) {
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            LocalDateTime selected = LocalDateTime.of(year, month, dayOfMonth, 0 ,0);
            this.dateView.setText(selected.format(Utils.sqliteDateFormat));
        }, loggedAt.getYear(), loggedAt.getMonthValue(), loggedAt.getDayOfMonth());
        dialog.show();
    }

    private void timeUpdateDialog(LocalDateTime loggedAt) {
        TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            LocalTime selected = LocalTime.of(hourOfDay, minute);
            this.timeView.setText(selected.format(Utils.sqliteTimeFormat));
        }, loggedAt.getHour(), loggedAt.getMinute(), true);
        dialog.show();
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