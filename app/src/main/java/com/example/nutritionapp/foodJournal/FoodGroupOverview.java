package com.example.nutritionapp.foodJournal;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.nutritionapp.other.NutritionElement;
import com.example.nutritionapp.other.NutritionPercentageTuple;
import com.example.nutritionapp.other.PortionTypes;
import com.example.nutritionapp.other.Utils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class FoodGroupOverview extends AppCompatActivity {

    private static final int DEFAULT_AMOUNT = 100;
    private static final int NO_UPDATE_EXISTING = -1;
    private static boolean FIRST_ADD = true;
    final ArrayList<SelectedFoodItem> selected = new ArrayList<>();

    /* information from caller */
    private boolean editMode = false;
    boolean isTemplateMode;

    private TextView dateView;
    private TextView timeView;

    LocalDateTime loggedAt;
    ListView selectedListView;
    final ArrayList<GroupListItem> suggestionsByPrevSelected = new ArrayList<>();
    ListView suggestions;
    ListView nutOverviewList;
    Database db;
    View nutritionOverviewHeader;
    View selectedItemsHeader;
    View suggestedItemsHeader;


    SelectedFoodAdapter selectedAdapter;
    int groupId;

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal_food_group_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        ImageButton toolbarBack = findViewById(R.id.toolbar_back);
        ImageButton addButton = findViewById(R.id.toolbar_forward);
        Button saveAsTemplate = findViewById(R.id.saveAsTemplate);
        toolbar.setTitle("");
        toolbarTitle.setText(R.string.toolbarStringGroupDetails);
        setSupportActionBar(toolbar);

        toolbarBack.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        addButton.setImageResource(R.drawable.add_circle_filled);

        selectedListView = findViewById(R.id.selected_items);
        selectedAdapter = new SelectedFoodAdapter(this, new ArrayList<>());
        selectedListView.setAdapter(selectedAdapter);

        FIRST_ADD = true;

        dateView = findViewById(R.id.date);
        timeView = findViewById(R.id.time);

        nutOverviewList = findViewById(R.id.nutritionOverview);

        addButton.setOnClickListener(v -> {
            runFoodSelectionPipeline(null, NO_UPDATE_EXISTING);
        });

        suggestions = findViewById(R.id.suggestions);
        db = new Database(this);

        /* set existing items if edit mode */
        groupId = this.getIntent().getIntExtra("groupId", -1);

        /* see if we are creating or editing a pure template */
        isTemplateMode = this.getIntent().getBooleanExtra("isTemplateMode", false);

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
        if(isTemplateMode){
            saveAsTemplate.setText(R.string.save);
        }else {
            dateView.setText(loggedAt.format(Utils.sqliteDateFormat));
            timeView.setText(loggedAt.format(Utils.sqliteTimeFormat));
            dateView.setOnClickListener(v -> dateUpdateDialog(loggedAt));
            timeView.setOnClickListener(v -> timeUpdateDialog(loggedAt));
        }

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

        selectedAdapter.setOnDataChangeListener(amount -> {
            updateNutritionOverview();
        });

        suggestions.setOnItemClickListener((parent, view, position, id) -> {
            ListFoodItem item = (ListFoodItem) parent.getItemAtPosition(position);
            runFoodSelectionPipeline(item, NO_UPDATE_EXISTING);
        });

        saveAsTemplate.setOnClickListener(view -> {
            save(null,true);
            Toast.makeText(this.getApplicationContext(), "Saved Template", Toast.LENGTH_LONG).show();
        });

        toolbarBack.setOnClickListener(v -> onBackPressed());
    }


    private void addSelectedFoodItem(SelectedFoodItem foodItem) {
        if (foodItem == null) {
            return;
        }
        this.selected.add(foodItem);
        updateSelectedView(this.selectedListView, this.selected);
        updateSuggestionList(this.db.getSuggestionsForCombination(this.selected), this.suggestionsByPrevSelected, this.suggestions);
    }

    private void updateSelectedFoodItem(SelectedFoodItem foodItem, int index) {
        if (foodItem == null) {
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
        if (selectedFoodItem == null) {
            Dialog foodSelectionDialog = new DialogFoodSelector(this);
            foodSelectionDialog.setOnDismissListener(dialog -> {
                DialogFoodSelector castedDialog = (DialogFoodSelector) dialog;
                runAmountSelectorDialog(castedDialog.selectedFood, position);
            });
            displaySelectorDialog(foodSelectionDialog);
        } else {
            runAmountSelectorDialog(selectedFoodItem.food, position);
        }
    }

    private void displaySelectorDialog(Dialog foodSelectionDialog) {

        /* Prof. Dr. StackOverflow https://stackoverflow.com/questions/2306503/how-to-make-an-alert-dialog-fill-90-of-screen-size */
        foodSelectionDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = foodSelectionDialog.getWindow();
        if (window != null) {
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(lp);
        }
    }

    private void runAmountSelectorDialog(Food selectedFood, int position) {
        if (selectedFood == null) {
            return;
        }
        selectedFood.setPreferredPortionFromDb(this.db);
        selectedFood.setAmountByAssociatedPortionType();
        DialogAmountSelector amountSelector = new DialogAmountSelector(this, db, selectedFood);
        amountSelector.setOnDismissListener(dialog -> {

            /* get values */
            DialogAmountSelector castedDialog = (DialogAmountSelector) dialog;
            float amountSelected = castedDialog.amountSelected;
            PortionTypes typeSelected = castedDialog.typeSelected;

            /* abort if bad selection */    // currently impossible -> pre selection
            if (amountSelected == 0 || typeSelected == null) {
                return;
            }

            // TODO normalize selected amount
            /* update */
            SelectedFoodItem sf = new SelectedFoodItem(selectedFood, amountSelected, typeSelected);
            if (position < 0) {
                addSelectedFoodItem(sf);
            } else {
                updateSelectedFoodItem(sf, position);
            }
        });

        displaySelectorDialog(amountSelector);
    }


    private void dateUpdateDialog(final LocalDateTime loggedAt) {
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            LocalDateTime selected = LocalDateTime.of(year, Utils.monthAndroidToDefault(month), dayOfMonth, 0, 0);
            this.dateView.setText(selected.format(Utils.sqliteDateFormat));
        }, loggedAt.getYear(), Utils.monthDefaultToAndroid(loggedAt.getMonthValue()), loggedAt.getDayOfMonth());
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
        /* HEADER */
        if (FIRST_ADD){
            selectedItemsHeader = findViewById(R.id.selected_items_header);
            TextView noHeader = selectedItemsHeader.findViewById(R.id.headerText);
            noHeader.setText(R.string.addFoodsGroupItemHeader);
        }
        ArrayList<SelectedFoodItem> sfi = new ArrayList<>(alreadySelected);
        SelectedFoodAdapter newAdapter = new SelectedFoodAdapter(this, sfi);
        selectedListView.setAdapter(newAdapter);
        selectedListView.invalidate();

        updateNutritionOverview();
    }

    private void updateSuggestionList(ArrayList<Food> foods, ArrayList<GroupListItem> suggestionsPrevSelected, ListView suggestions) {
        suggestionsPrevSelected.clear();

        /* HEADER */
        if (suggestionsPrevSelected.isEmpty()){
            suggestedItemsHeader = findViewById(R.id.suggested_items_header);
            TextView noHeader = suggestedItemsHeader.findViewById(R.id.headerText);
            noHeader.setText(R.string.addFoodsSuggestionHeader);
        }
        for (Food f : foods) {
            if (suggestionsPrevSelected.contains(new ListFoodItem(f))) {
                continue;
            }
            suggestionsPrevSelected.add(new ListFoodItem(f));
        }

        suggestions.invalidate();
        SelectableFoodListAdapter adapter = new SelectableFoodListAdapter(this, suggestionsPrevSelected);
        suggestions.setAdapter(adapter);
    }

    private void updateNutritionOverview() {
        if (FIRST_ADD){
            FIRST_ADD = false;

            nutritionOverviewHeader = findViewById(R.id.nutritionOverview_header);
            TextView noHeader = nutritionOverviewHeader.findViewById(R.id.headerText);
            noHeader.setText(R.string.addFoodsNutritionOverviewHeader);
        }
        ArrayList<Food> analysis = new ArrayList<>();
        for (SelectedFoodItem sfi : selected) {
            analysis.add(sfi.food);
        }
        NutritionAnalysis na = new NutritionAnalysis(analysis);

        /* FIXME: wtf are those next lines doing here? are they even ever used? */
        ArrayList<NutritionPercentageTuple> npt = new ArrayList<>();
        npt.add(new NutritionPercentageTuple(NutritionElement.CALCIUM, 0.f));
        npt.addAll(na.getNutritionPercentageSortedFilterZero());

        ListAdapter nutOverviewAdapter = new NutritionOverviewAdapter(this, na.getNutritionActual(), na.getNutritionPercentageSortedFilterZero());
        nutOverviewList.setAdapter(nutOverviewAdapter);
    }

    @Override
    public void onBackPressed() {

        String dateTimeString = dateView.getText() + " " + timeView.getText() + ":00";
        LocalDateTime computedLoggedAt;
        if(isTemplateMode){
            computedLoggedAt = LocalDateTime.now();
        }else{
            computedLoggedAt = LocalDateTime.parse(dateTimeString, Utils.sqliteDatetimeFormat);
        }

        save(computedLoggedAt, isTemplateMode);

        /* report back a dirty date if necessary */
        Intent resultIntent = new Intent();
        resultIntent.putExtra("dateTimeString", computedLoggedAt.format(Utils.sqliteDatetimeFormat));
        resultIntent.putExtra("groupId", groupId);
        setResult(Activity.RESULT_OK, resultIntent);

        finishAfterTransition();
    }

    private void save(LocalDateTime computedLoggedAt, boolean asTemplate){
        for (int i = 0; i < selectedAdapter.getCount(); i++) {
            SelectedFoodItem item = (SelectedFoodItem) selectedAdapter.getItem(i);
        }
        if (this.editMode) {
            db.updateFoodGroup(selected, groupId, computedLoggedAt, asTemplate);
        } else {
            groupId = db.logExistingFoods(selected, computedLoggedAt, null, asTemplate);
        }
    }
}