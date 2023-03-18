package com.example.nutritionapp.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
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

import androidx.lifecycle.ViewModelProvider;


public class FoodGroupFragment extends Fragment implements SelectedFoodAdapter.EventListener {

    private static final int DEFAULT_AMOUNT = 100;
    private static final int NO_UPDATE_EXISTING = -1;

    /* information from caller */
    private boolean editMode = false;
    private boolean isTemplateMode;
    private LocalDateTime loggedAt;
    private final ArrayList<GroupListItem> suggestionsByPrevSelected = new ArrayList<>();
    private ListView suggestions;
    private ListView nutOverviewList;

    // to restrict overwrite to this view
    private OnBackPressedCallback backPressedCallback;
    int groupId;


    private TextView dateView;
    private TextView timeView;
    SelectedFoodAdapter selectedAdapter;
    private ListView selectedListView;
    private View view;
    private FoodGroupViewModel viewModel;

    public FoodGroupFragment() {
        // Required empty public constructor
    }

    public static FoodGroupFragment newInstance() {
        return new FoodGroupFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this).get(FoodGroupViewModel.class);

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_food_group, container, false);

        // Initialize views
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        ImageButton toolbarBack = toolbar.findViewById(R.id.toolbar_back);
        ImageButton addButton = toolbar.findViewById(R.id.toolbar_forward);
        Button saveAsTemplate = view.findViewById(R.id.saveAsTemplate);
        toolbar.setTitle(R.string.toolbarStringGroupDetails);
        toolbarBack.setImageResource(R.color.transparent);
        addButton.setImageResource(R.drawable.add_circle_filled);
        selectedListView = view.findViewById(R.id.selected_items);
        dateView = view.findViewById(R.id.date);
        timeView = view.findViewById(R.id.time);
        nutOverviewList = view.findViewById(R.id.nutritionOverview);
        suggestions = view.findViewById(R.id.suggestions);

        // get adapter and including data
        selectedAdapter = new SelectedFoodAdapter(getContext(), new ArrayList<>(), this);
        selectedListView.setAdapter(selectedAdapter);

        // initialize values
        viewModel.setDb(new Database(getActivity()));
        viewModel.setFirstAdd(true);

        /* set existing items if edit mode */
        groupId = this.requireActivity().getIntent().getIntExtra(ActivityExtraNames.GROUP_ID, -1);
        /* see if we are creating or editing a pure template */
        isTemplateMode = this.requireActivity().getIntent().getBooleanExtra(ActivityExtraNames.GROUP_ID, false);

        /* edit mode */
        if (groupId >= 0) {
            this.editMode = true;
            ArrayList<Food> foods = viewModel.getDb().getLoggedFoodByGroupId(groupId);
            for (Food f : foods) {
                viewModel.selected.add(new SelectedFoodItem(f, f.associatedAmount, f.associatedPortionType));
            }
            if (!foods.isEmpty()) {
                loggedAt = foods.get(0).loggedAt;
            } else {
                loggedAt = LocalDateTime.now();
            }
            updateSelectedView(selectedListView, viewModel.selected);
            updateSuggestionList(viewModel.getDb().getSuggestionsForCombination(viewModel.selected), suggestionsByPrevSelected, suggestions);
        } else {
            loggedAt = LocalDateTime.now();
        }
        if (isTemplateMode) {
            saveAsTemplate.setText(R.string.save);
        } else {
            dateView.setText(loggedAt.format(Utils.sqliteDateFormat));
            timeView.setText(loggedAt.format(Utils.sqliteTimeFormat));
            dateView.setOnClickListener(v -> dateUpdateDialog(loggedAt));
            timeView.setOnClickListener(v -> timeUpdateDialog(loggedAt));
        }

        // set clicklisteners
        addButton.setOnClickListener(v -> {
            runFoodSelectionPipeline(null, NO_UPDATE_EXISTING);
        });

        selectedListView.setOnItemLongClickListener((parent, clickedView, position, id) -> {
            final SelectedFoodItem item = (SelectedFoodItem) parent.getItemAtPosition(position);
            viewModel.selected.remove(item);
            updateSelectedView(selectedListView, viewModel.selected);
            updateSuggestionList(viewModel.getDb().getSuggestionsForCombination(viewModel.selected), suggestionsByPrevSelected, suggestions);
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
            save(null, true);
            Toast.makeText(this.getContext(), "Saved Template", Toast.LENGTH_LONG).show();
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // check for unsaved old data
        updateSelectedView(selectedListView, viewModel.selected);

        // handle back-pressed
        backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                String dateTimeString = dateView.getText() + " " + timeView.getText() + ":00";
                LocalDateTime computedLoggedAt;
                if (isTemplateMode) {
                    computedLoggedAt = LocalDateTime.now();
                } else {
                    computedLoggedAt = LocalDateTime.parse(dateTimeString, Utils.sqliteDatetimeFormat);
                }
                save(computedLoggedAt, isTemplateMode);

                Utils.navigate(StartPageFragment.class, ((MainActivity) requireActivity()));
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), backPressedCallback);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        backPressedCallback.remove();
    }

    private void addSelectedFoodItem(SelectedFoodItem foodItem) {
        if (foodItem == null) {
            return;
        }
        viewModel.selected.add(foodItem);
        updateSelectedView(this.selectedListView, viewModel.selected);
        updateSuggestionList(viewModel.getDb().getSuggestionsForCombination(viewModel.selected), this.suggestionsByPrevSelected, this.suggestions);
    }

    private void updateSelectedFoodItem(SelectedFoodItem foodItem, int index) {
        if (foodItem == null) {
            return;
        }
        SelectedFoodItem f = viewModel.selected.get(index);
        if (!f.food.name.equals(foodItem.food.name)) {
            throw new AssertionError("Food Item Update failed cause names are miss-match, this should not be possible?!");
        }
        updateSelectedView(this.selectedListView, viewModel.selected);
        updateSuggestionList(viewModel.getDb().getSuggestionsForCombination(viewModel.selected), this.suggestionsByPrevSelected, this.suggestions);
    }

    public void runFoodSelectionPipeline(ListFoodItem selectedFoodItem, int position) {
        if (selectedFoodItem == null) {
            Dialog foodSelectionDialog = new DialogFoodSelector(requireActivity());
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
        if (current == null) {
            selectedFood.setPreferredPortionFromDb(viewModel.getDb());
        } else {
            selectedFood.associatedPortionType = current;
        }

        if (Double.isNaN(amountCurrent)) {
            selectedFood.setAmountByAssociatedPortionType();
        } else {
            selectedFood.associatedAmount = amountCurrent;
        }
        DialogAmountSelector amountSelector = new DialogAmountSelector(requireActivity(), viewModel.getDb(), selectedFood);
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
        if (!alreadySelected.isEmpty()) {
            /* HEADER */
            if (viewModel.getFirstAdd()) {
                View selectedItemsHeader = view.findViewById(R.id.selected_items_header);
                TextView noHeader = selectedItemsHeader.findViewById(R.id.headerText);
                noHeader.setText(R.string.addFoodsGroupItemHeader);
            }
            ArrayList<SelectedFoodItem> sfi = new ArrayList<>(alreadySelected);
            SelectedFoodAdapter newAdapter = new SelectedFoodAdapter(getContext(), sfi, this);
            selectedListView.setAdapter(newAdapter);
            selectedListView.invalidate();

            updateNutritionOverview();

        }
    }

    private void updateSuggestionList(ArrayList<Food> foods, ArrayList<GroupListItem> suggestionsPrevSelected, ListView suggestions) {
        suggestionsPrevSelected.clear();

        /* HEADER */
        View suggestedItemsHeader = view.findViewById(R.id.suggested_items_header);
        TextView noHeader = suggestedItemsHeader.findViewById(R.id.headerText);
        noHeader.setText(R.string.addFoodsSuggestionHeader);
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
        if (viewModel.getFirstAdd()) {
            viewModel.setFirstAdd(false);

            View nutritionOverviewHeader = view.findViewById(R.id.nutritionOverview_header);
            TextView noHeader = nutritionOverviewHeader.findViewById(R.id.headerText);
            noHeader.setText(R.string.addFoodsNutritionOverviewHeader);
        }
        ArrayList<Food> analysis = new ArrayList<>();
        for (SelectedFoodItem sfi : viewModel.selected) {
            analysis.add(sfi.food);
        }
        NutritionAnalysis na = new NutritionAnalysis(analysis);

        ListAdapter nutOverviewAdapter = new NutritionOverviewAdapter(getActivity(), na.getNutritionActual(), na.getNutritionPercentageSortedFilterZero());
        nutOverviewList.setAdapter(nutOverviewAdapter);
    }

    private void clearFoodInput() {
        viewModel.selected.clear();
    }

    private void save(LocalDateTime computedLoggedAt, boolean asTemplate) {
//        for (int i = 0; i < selectedAdapter.getCount(); i++) {
//            SelectedFoodItem item = (SelectedFoodItem) selectedAdapter.getItem(i);
//        }
        if (this.editMode) {
            viewModel.getDb().updateFoodGroup(viewModel.selected, groupId, computedLoggedAt, asTemplate);
        } else {
            groupId = viewModel.getDb().logExistingFoods(viewModel.selected, computedLoggedAt, null, asTemplate);
        }
        clearFoodInput();
    }


    // run amount selector from SelectedFoodAdapter
    @Override
    public void amountSelectionEvent(Food food, int position, PortionType portionType, double amountAssociated) {
        runAmountSelectorDialog(food, position, portionType, amountAssociated);
    }
}