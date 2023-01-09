package com.example.nutritionapp.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nutritionapp.MainActivity;
import com.example.nutritionapp.R;
import com.example.nutritionapp.foodJournal.addFoodsLists.GroupListItem;
import com.example.nutritionapp.foodJournal.addFoodsLists.ListFoodItem;
import com.example.nutritionapp.foodJournal.addFoodsLists.SelectableFoodListAdapter;
import com.example.nutritionapp.foodJournal.addFoodsLists.SelectedFoodAdapter;
import com.example.nutritionapp.foodJournal.addFoodsLists.SelectedFoodItem;
import com.example.nutritionapp.foodJournal.overviewFoodsLists.DialogAmountSelector;
import com.example.nutritionapp.foodJournal.overviewFoodsLists.DialogFoodSelector;
import com.example.nutritionapp.foodJournal.overviewFoodsLists.NutritionOverviewAdapter;
import com.example.nutritionapp.other.ActivityExtraNames;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.PortionType;
import com.example.nutritionapp.other.Utils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;


public class FoodGroupFragment extends Fragment {

    private static final int DEFAULT_AMOUNT = 100;
    private static final int NO_UPDATE_EXISTING = -1;
    private static boolean FIRST_ADD = true;
    private final ArrayList<SelectedFoodItem> selected = new ArrayList<>();

    /* information from caller */
    private boolean editMode = false;
    private boolean isTemplateMode;

    private TextView dateView;
    private TextView timeView;

    private LocalDateTime loggedAt;
    private ListView selectedListView;
    private final ArrayList<GroupListItem> suggestionsByPrevSelected = new ArrayList<>();
    private ListView suggestions;
    private ListView nutOverviewList;
    private Database db;
    private View nutritionOverviewHeader;
    private View selectedItemsHeader;
    private View suggestedItemsHeader;
    private View view;


    SelectedFoodAdapter selectedAdapter;
    int groupId;

    public FoodGroupFragment() {
        // Required empty public constructor
    }

    public static FoodGroupFragment newInstance(String param1, String param2) {
        FoodGroupFragment fragment = new FoodGroupFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_food_group, container, false);

        Toolbar toolbar = ((MainActivity) getActivity()).findViewById(R.id.toolbar);
        ImageButton toolbarBack = toolbar.findViewById(R.id.toolbar_back);
        ImageButton addButton = toolbar.findViewById(R.id.toolbar_forward);
        Button saveAsTemplate = view.findViewById(R.id.saveAsTemplate);
        toolbar.setTitle(R.string.toolbarStringGroupDetails);

        toolbarBack.setImageResource(R.color.transparent);
        addButton.setImageResource(R.drawable.add_circle_filled);

        selectedListView = view.findViewById(R.id.selected_items);
        selectedAdapter = new SelectedFoodAdapter(getContext(), new ArrayList<>());
        selectedListView.setAdapter(selectedAdapter);

        FIRST_ADD = true;

        dateView = view.findViewById(R.id.date);
        timeView = view.findViewById(R.id.time);

        nutOverviewList = view.findViewById(R.id.nutritionOverview);

        addButton.setOnClickListener(v -> {
            runFoodSelectionPipeline(null, NO_UPDATE_EXISTING);
        });

        suggestions = view.findViewById(R.id.suggestions);
        db = new Database((MainActivity)getActivity());

        /* set existing items if edit mode */
        groupId = this.getActivity().getIntent().getIntExtra(ActivityExtraNames.GROUP_ID, -1);

        /* see if we are creating or editing a pure template */
        isTemplateMode = this.getActivity().getIntent().getBooleanExtra(ActivityExtraNames.GROUP_ID, false);

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

        selectedListView.setOnItemLongClickListener((parent, clickedView, position, id) -> {
            final SelectedFoodItem item = (SelectedFoodItem) parent.getItemAtPosition(position);
            selected.remove(item);
            updateSelectedView(selectedListView, selected);
            updateSuggestionList(db.getSuggestionsForCombination(selected), suggestionsByPrevSelected, suggestions);
            return false;
        });

        selectedListView.setOnItemClickListener((parent, clickedView, position, id) -> {
            runFoodSelectionPipeline((ListFoodItem) parent.getItemAtPosition(position), position);
        });

        suggestions.setOnItemClickListener((parent, clickedView, position, id) -> {
            ListFoodItem item = (ListFoodItem) parent.getItemAtPosition(position);
            runFoodSelectionPipeline(item, NO_UPDATE_EXISTING);
        });

        saveAsTemplate.setOnClickListener(clickedView -> {
            save(null,true);
            Toast.makeText(this.getContext(), "Saved Template", Toast.LENGTH_LONG).show();
        });

        requireActivity().getOnBackPressedDispatcher().addCallback((MainActivity)getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                String dateTimeString = dateView.getText() + " " + timeView.getText() + ":00";
                LocalDateTime computedLoggedAt;
                if(isTemplateMode){
                    computedLoggedAt = LocalDateTime.now();
                }else{
                    computedLoggedAt = LocalDateTime.parse(dateTimeString, Utils.sqliteDatetimeFormat);
                }
                save(computedLoggedAt, isTemplateMode);

                Utils.navigate(StartPageFragment.class, ((MainActivity)getActivity()));
            }
        });

        return view;
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

    public void runFoodSelectionPipeline(ListFoodItem selectedFoodItem, int position) {
        if (selectedFoodItem == null) {
            Dialog foodSelectionDialog = new DialogFoodSelector((MainActivity)getActivity());
            foodSelectionDialog.setOnDismissListener(dialog -> {
                DialogFoodSelector castedDialog = (DialogFoodSelector) dialog;
                runAmountSelectorDialog(castedDialog.selectedFood, position, null, Double.NaN);
            });
            displaySelectorDialog(foodSelectionDialog);
        } else {
            runAmountSelectorDialog(selectedFoodItem.food, position, null, Double.NaN);
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

    public void runAmountSelectorDialog(Food selectedFood, int position, PortionType current, double amountCurrent) {
        if (selectedFood == null) {
            return;
        }
        if(current == null) {
            selectedFood.setPreferredPortionFromDb(this.db);
        }else{
            selectedFood.associatedPortionType = current;
        }

        if(Double.isNaN(amountCurrent)) {
            selectedFood.setAmountByAssociatedPortionType();
        }else{
            selectedFood.associatedAmount = amountCurrent;
        }
        DialogAmountSelector amountSelector = new DialogAmountSelector((MainActivity)getActivity(), db, selectedFood);
        amountSelector.setOnDismissListener(dialog -> {

            /* get values */
            DialogAmountSelector castedDialog = (DialogAmountSelector) dialog;
            double amountSelected = castedDialog.amountSelected;
            PortionType typeSelected = castedDialog.typeSelected;

            /* abort if bad selection */    // currently impossible -> pre selection
            if (amountSelected == 0 || typeSelected == null) {
                return;
            }

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
        DatePickerDialog dialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            LocalDateTime selected = LocalDateTime.of(year, Utils.monthAndroidToDefault(month), dayOfMonth, 0, 0);
            this.dateView.setText(selected.format(Utils.sqliteDateFormat));
        }, loggedAt.getYear(), Utils.monthDefaultToAndroid(loggedAt.getMonthValue()), loggedAt.getDayOfMonth());
        dialog.show();
    }

    private void timeUpdateDialog(LocalDateTime loggedAt) {
        TimePickerDialog dialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
            LocalTime selected = LocalTime.of(hourOfDay, minute);
            this.timeView.setText(selected.format(Utils.sqliteTimeFormat));
        }, loggedAt.getHour(), loggedAt.getMinute(), true);
        dialog.show();
    }

    private void updateSelectedView(ListView selectedListView, ArrayList<SelectedFoodItem> alreadySelected) {
        /* HEADER */
        if (FIRST_ADD){
            selectedItemsHeader = view.findViewById(R.id.selected_items_header);
            TextView noHeader = selectedItemsHeader.findViewById(R.id.headerText);
            noHeader.setText(R.string.addFoodsGroupItemHeader);
        }
        ArrayList<SelectedFoodItem> sfi = new ArrayList<>(alreadySelected);
        SelectedFoodAdapter newAdapter = new SelectedFoodAdapter(getContext(), sfi);
        selectedListView.setAdapter(newAdapter);
        selectedListView.invalidate();

        updateNutritionOverview();
    }

    private void updateSuggestionList(ArrayList<Food> foods, ArrayList<GroupListItem> suggestionsPrevSelected, ListView suggestions) {
        suggestionsPrevSelected.clear();

        /* HEADER */
        if (suggestionsPrevSelected.isEmpty()){
            suggestedItemsHeader = view.findViewById(R.id.suggested_items_header);
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
        SelectableFoodListAdapter adapter = new SelectableFoodListAdapter(getContext(), suggestionsPrevSelected);
        suggestions.setAdapter(adapter);
    }

    private void updateNutritionOverview() {
        if (FIRST_ADD){
            FIRST_ADD = false;

            nutritionOverviewHeader = view.findViewById(R.id.nutritionOverview_header);
            TextView noHeader = nutritionOverviewHeader.findViewById(R.id.headerText);
            noHeader.setText(R.string.addFoodsNutritionOverviewHeader);
        }
        ArrayList<Food> analysis = new ArrayList<>();
        for (SelectedFoodItem sfi : selected) {
            analysis.add(sfi.food);
        }
        NutritionAnalysis na = new NutritionAnalysis(analysis);

        ListAdapter nutOverviewAdapter = new NutritionOverviewAdapter((MainActivity)getActivity(), na.getNutritionActual(), na.getNutritionPercentageSortedFilterZero());
        nutOverviewList.setAdapter(nutOverviewAdapter);
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