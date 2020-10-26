package com.example.nutritionapp.foodJournal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionapp.R;
import com.example.nutritionapp.foodJournal.AddFoodsLists.GroupListItem;
import com.example.nutritionapp.foodJournal.AddFoodsLists.SelectableFoodListAdapter;
import com.example.nutritionapp.foodJournal.AddFoodsLists.ListFoodItem;
import com.example.nutritionapp.foodJournal.AddFoodsLists.SelectedFoodAdapter;
import com.example.nutritionapp.foodJournal.AddFoodsLists.SelectedFoodItem;
import com.example.nutritionapp.foodJournal.OverviewFoodsLists.JournalDialogAmountSelector;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
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

        TextView nutOverviewPlaceholder = findViewById(R.id.nutritionOverview);
        nutOverviewPlaceholder.setText(R.string.placeholder);
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
            db.close();
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

    private void runFoodSelectionPipeline(ListFoodItem selectedFood, int position) {
        if(selectedFood == null){
            Dialog foodSelectionDialog = new DialogFoodSelector(this);
            foodSelectionDialog.setOnDismissListener(dialog -> {
                DialogFoodSelector castedDialog = (DialogFoodSelector) dialog;
                runAmountSelectorDialog(castedDialog.selectedFood, position);
            });
            displaySelectorDialog(foodSelectionDialog);
        }else {
            runAmountSelectorDialog(selectedFood.food, position);
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
        JournalDialogAmountSelector amountSelector = new JournalDialogAmountSelector(this);
        amountSelector.setOnDismissListener(dialog -> {

            /* get values */
            JournalDialogAmountSelector castedDialog = (JournalDialogAmountSelector) dialog;
            int amountSelected = castedDialog.amountSelected;
            amountSelected = DEFAULT_AMOUNT;
            PortionTypes typeSelected = castedDialog.typeSelected;
            typeSelected = PortionTypes.GRAM;

            /* abort if bad selection */
            if(amountSelected == 0 || typeSelected == null){
                return;
            }

            // TODO normalize selected amount
            /* update */
            SelectedFoodItem sf = new SelectedFoodItem(selectedFood, amountSelected);
            if(position < 0){
                addSelectedFoodItem(sf);
            }else{
                updateSelectedFoodItem(sf, position);
            }

            //TODO update nutrition overview
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
        SelectableFoodListAdapter adapter = new SelectableFoodListAdapter(getApplicationContext(), suggestionsPrevSelected);
        suggestions.setAdapter(adapter);
    }

}