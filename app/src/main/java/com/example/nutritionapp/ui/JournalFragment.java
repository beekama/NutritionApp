package com.example.nutritionapp.ui;

import static com.example.nutritionapp.other.Utils.navigate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.appcompat.widget.Toolbar;

import com.example.nutritionapp.MainActivity;
import com.example.nutritionapp.R;
import com.example.nutritionapp.deprecated.FoodGroupOverview;
import com.example.nutritionapp.foodJournal.overviewFoodsLists.FoodOverviewAdapter;
import com.example.nutritionapp.foodJournal.overviewFoodsLists.FoodOverviewListItem;
import com.example.nutritionapp.other.ActivityExtraNames;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;


public class JournalFragment extends Fragment {

    private Database db;
    private final LocalDate now = LocalDate.now();
    private final LocalDate oldestDateShown = LocalDate.now().minusWeeks(1);

    final private Duration ONE_DAY = Duration.ofDays(1);
    final private Duration ONE_WEEK = Duration.ofDays(7);
    final private ArrayList<FoodOverviewListItem> foodDataList = new ArrayList<>();

    /* this map is used to reload invalidated data */
    /* data gets invalidated by edits or adding new journal entries */
    final private HashMap<LocalDate, FoodOverviewListItem> dataInvalidationMap = new HashMap<>();

    private FoodOverviewAdapter adapter;
    public ActivityResultLauncher<Intent> activityResultLauncher;

    public JournalFragment() {
        // Required empty public constructor
    }


    public static JournalFragment newInstance(String param1, String param2) {
        JournalFragment fragment = new JournalFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new Database(this);
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        if (data.getIntExtra("REQUEST_CODE", Utils.WRONG_REQUEST_CODE) == Utils.FOOD_GROUP_DETAILS_ID) {
                            String returnValue = data.getStringExtra(ActivityExtraNames.DATE_RESULT);
                            int groupIdValue = data.getIntExtra(ActivityExtraNames.DATE_RESULT, -1);
                            assert groupIdValue != -1;

                            LocalDateTime dateTime = LocalDateTime.parse(returnValue, Utils.sqliteDatetimeFormat);
                            FoodOverviewListItem dirtyItem = dataInvalidationMap.get(dateTime.toLocalDate());

                            /* the group exists and was just changed */
                            if (dirtyItem != null) {
                                dirtyItem.dirty = true;
                                dirtyItem.update(groupIdValue);
                                adapter.notifyDataSetChanged();
                            } else {
                                adapter.reloadComputationallyExpensive();
                            }
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_journal, container, false);

        /* retrieve items */
        Toolbar toolbar = ((MainActivity) getActivity()).findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.journalToolbarText);
        ImageButton addStuff = toolbar.findViewById(R.id.toolbar_forward);
        addStuff.setImageResource(R.drawable.add_circle_filled);
        ImageButton toolbarBack = toolbar.findViewById(R.id.toolbar_back);
        toolbarBack.setImageResource(R.color.transparent);

        /* set adapter */
        /* this is a list of layout of type journal_day_header, which contains the day-header and
        a nested sublist of the foods (food groups) on this */
        RecyclerView mainListOfFoodsWithDayHeaders = view.findViewById(R.id.mainList);
        adapter = new FoodOverviewAdapter(getContext(), foodDataList, mainListOfFoodsWithDayHeaders, db, (MainActivity) getActivity(), dataInvalidationMap);
        LinearLayoutManager mainListLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mainListOfFoodsWithDayHeaders.setLayoutManager(mainListLayoutManager);
        mainListOfFoodsWithDayHeaders.setAdapter(adapter);

        addStuff.setOnClickListener(v ->
                navigate(FoodGroupFragment.class, (MainActivity)getActivity()));

        return view;
    }
}