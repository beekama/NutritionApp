package com.example.nutritionapp.foodJournal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Pair;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionapp.R;
import com.example.nutritionapp.foodJournal.addFoodsLists.GroupListItem;
import com.example.nutritionapp.foodJournal.addFoodsLists.SelectableFoodListAdapter;
import com.example.nutritionapp.foodJournal.addFoodsLists.ListFoodItem;
import com.example.nutritionapp.foodJournal.addFoodsLists.SelectedFoodAdapter;
import com.example.nutritionapp.foodJournal.addFoodsLists.SelectedFoodItem;
import com.example.nutritionapp.foodJournal.overviewFoodsLists.DialogFoodSelector;
import com.example.nutritionapp.foodJournal.overviewFoodsLists.DialogAmountSelector;
import com.example.nutritionapp.foodJournal.overviewFoodsLists.NutritionOverviewAdapter;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.PortionTypes;
import com.example.nutritionapp.other.Utils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class FoodGroupOverview extends AppCompatActivity {

    private static final int DEFAULT_AMOUNT = 100;
    private static final int NO_UPDATE_EXISTING = -1;
    final ArrayList<SelectedFoodItem> selected = new ArrayList<>();
    private boolean editMode = false;

    private TextView dateView;
    private TextView timeView;

    LocalDateTime loggedAt;
    ListView selectedListView;
    final ArrayList<GroupListItem> suggestionsByPrevSelected = new ArrayList<>();
    ListView suggestions;
    ListView nutOverviewList;
    Database db;

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal_food_group_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        ImageButton toolbarBack = findViewById(R.id.toolbar_back);
        toolbar.setTitle("");
        toolbarTitle.setText(R.string.toolbarStringGroupDetails);
        setSupportActionBar(toolbar);

        toolbarBack.setImageResource(R.drawable.ic_arrow_back_black_24dp);

        selectedListView = findViewById(R.id.selected_items);
        SelectedFoodAdapter selectedAdapter = new SelectedFoodAdapter(getApplicationContext(), new ArrayList<>());
        selectedListView.setAdapter(selectedAdapter);

        dateView = findViewById(R.id.date);
        timeView = findViewById(R.id.time);

        nutOverviewList = findViewById(R.id.nutritionOverview);
        Button addNewButton = findViewById(R.id.addButton);
        addNewButton.setText(R.string.addSingleFood);

        addNewButton.setOnClickListener(v -> {
            runFoodSelectionPipeline(null, NO_UPDATE_EXISTING);
        });

        suggestions = findViewById(R.id.suggestions);
        db = new Database(this);

        /* set existing items if edit mode */
        int groupId = this.getIntent().getIntExtra("groupId", -1);

        if (groupId >= 0) {
            this.editMode = true;
            ArrayList<Food> foods = db.getLoggedFoodByGroupId(groupId);
            for (Food f : foods) {
                selected.add(new SelectedFoodItem(f, f.associatedAmount, f.associatedPortionType));
            }
            if (!foods.isEmpty()) {
                loggedAt = foods.get(0).loggedAt;
            } else {
                loggedAt = LocalDateTime.now();
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

        selectedListView.setOnItemClickListener((parent, view, position, id) -> {
            runFoodSelectionPipeline((ListFoodItem) parent.getItemAtPosition(position), position);
        });

        suggestions.setOnItemClickListener((parent, view, position, id) -> {
            ListFoodItem item = (ListFoodItem) parent.getItemAtPosition(position);
            runFoodSelectionPipeline(item, NO_UPDATE_EXISTING);
        });

        toolbarBack.setOnClickListener(v -> {
            String dateTimeString = dateView.getText() + " " + timeView.getText() + ":00";
            LocalDateTime computedLoggedAt = LocalDateTime.parse(dateTimeString, Utils.sqliteDatetimeFormat);
            for(int i = 0; i < selectedAdapter.getCount(); i++){
                SelectedFoodItem item = (SelectedFoodItem) selectedAdapter.getItem(i);
            }
            if(this.editMode){
                db.updateFoodGroup(selected, groupId, computedLoggedAt);
            }else {
                db.logExistingFoods(selected, computedLoggedAt, null);
            }
            finish();
        });

    }

    private void addSelectedFoodItem(SelectedFoodItem foodItem) {
        if(foodItem == null){
            return;
        }
        this.selected.add(foodItem);
        updateSelectedView(this.selectedListView, this.selected);
        updateSuggestionList(this.db.getSuggestionsForCombination(this.selected), this.suggestionsByPrevSelected, this.suggestions);
    }

    private void updateSelectedFoodItem(SelectedFoodItem foodItem, int index) {
        if(foodItem == null){
            return;
        }
        SelectedFoodItem f = this.selected.get(index);
        if (!f.food.name.equals(foodItem.food.name)) {
            throw new AssertionError("Food Item Update failed cause names are miss-match, this should not be possible?!");
        }
        updateSelectedView(this.selectedListView, this.selected);
        updateSuggestionList(this.db.getSuggestionsForCombination(this.selected), this.suggestionsByPrevSelected, this.suggestions);
    }

    private void runFoodSelectionPipeline(ListFoodItem selectedFoodItem, int position) {
        if(selectedFoodItem == null){
            Dialog foodSelectionDialog = new DialogFoodSelector(this);
            foodSelectionDialog.setOnDismissListener(dialog -> {
                DialogFoodSelector castedDialog = (DialogFoodSelector) dialog;
                runAmountSelectorDialog(castedDialog.selectedFood, position);
            });
            displaySelectorDialog(foodSelectionDialog);
        }else {
            runAmountSelectorDialog(selectedFoodItem.food, position);
        }
    }

    private void displaySelectorDialog(Dialog foodSelectionDialog) {

        /* Prof. Dr. StackOverflow https://stackoverflow.com/questions/2306503/how-to-make-an-alert-dialog-fill-90-of-screen-size */
        foodSelectionDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = foodSelectionDialog.getWindow();
        if(window != null){
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(lp);
        }
    }

    private void runAmountSelectorDialog(Food selectedFood, int position) {
        if(selectedFood == null){
            return;
        }
        selectedFood.setPreferedPortionFromDb(this.db);
        selectedFood.setAmountByAssociatedPortionType();
        DialogAmountSelector amountSelector = new DialogAmountSelector(this, db, selectedFood);
        amountSelector.setOnDismissListener(dialog -> {

            /* get values */
            DialogAmountSelector castedDialog = (DialogAmountSelector) dialog;
            float amountSelected = castedDialog.amountSelected;
            PortionTypes typeSelected = castedDialog.typeSelected;

            /* abort if bad selection */
            if(amountSelected == 0 || typeSelected == null){
                return;
            }

            // TODO normalize selected amount
            /* update */
            SelectedFoodItem sf = new SelectedFoodItem(selectedFood, amountSelected, typeSelected);
            if(position < 0){
                addSelectedFoodItem(sf);
            }else{
                updateSelectedFoodItem(sf, position);
            }
        });

        displaySelectorDialog(amountSelector);
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
        ArrayList<SelectedFoodItem> sfi = new ArrayList<>(alreadySelected);
        SelectedFoodAdapter newAdapter = new SelectedFoodAdapter(getApplicationContext(), sfi);
        selectedListView.setAdapter(newAdapter);
        selectedListView.invalidate();

        updateNutritionOverview();
    }

    private void updateSuggestionList(ArrayList<Food> foods, ArrayList<GroupListItem> suggestionsPrevSelected, ListView suggestions) {
        suggestionsPrevSelected.clear();
        for (Food f : foods) {
            if (suggestionsPrevSelected.contains(new ListFoodItem(f))) {
                continue;
            }
            suggestionsPrevSelected.add(new ListFoodItem(f));
        }

        suggestions.invalidate();
        SelectableFoodListAdapter adapter = new SelectableFoodListAdapter(getApplicationContext(), suggestionsPrevSelected);
        suggestions.setAdapter(adapter);
    }

    private void updateNutritionOverview() {
        ArrayList<Food> analysis = new ArrayList<>();
        for(SelectedFoodItem sfi : selected){
            analysis.add(sfi.food);
        }
        NutritionAnalysis na = new NutritionAnalysis(analysis);
        ListAdapter nutOverviewAdapter = new NutritionOverviewAdapter(this, na.getNutritionActual(), na.getNutritionPercentageSortedFilterZero());
        nutOverviewList.setAdapter(nutOverviewAdapter);
    }

}