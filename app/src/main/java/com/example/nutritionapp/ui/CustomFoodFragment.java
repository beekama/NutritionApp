package com.example.nutritionapp.ui;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.nutritionapp.MainActivity;
import com.example.nutritionapp.R;
import com.example.nutritionapp.customFoods.CustomFoodOverviewItem;
import com.example.nutritionapp.customFoods.CustomGroupOverviewItem;
import com.example.nutritionapp.customFoods.CustomOverviewItem;
import com.example.nutritionapp.customFoods.FoodOverviewAdapter;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;


public class CustomFoodFragment extends Fragment {
    private Database db;
    private RecyclerView mainRv;
    final ArrayList<CustomOverviewItem> foodItems = new ArrayList<>();
    RecyclerView.Adapter<?> foodOverviewAdapter;

    public CustomFoodFragment() {
        // Required empty public constructor
    }

    public static CustomFoodFragment newInstance(String param1, String param2) {
        CustomFoodFragment fragment = new CustomFoodFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new Database((MainActivity)getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_food, container, false);

        /* replace actionbar with custom app_toolbar */
        Toolbar toolbar = ((MainActivity) getActivity()).findViewById(R.id.toolbar);
        ImageButton toolbarForward = toolbar.findViewById(R.id.toolbar_forward);
        toolbarForward.setImageResource(R.drawable.add_circle_filled);
        toolbar.setTitle(R.string.customItems);

        mainRv = view.findViewById(R.id.createFoodOverview_rv);
        mainRv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        /* add new food item */
        toolbarForward.setOnClickListener((v -> {
            Class fragmentClass = CreateFoodItemFragment.class;
            Utils.navigate(fragmentClass, (MainActivity)getActivity());
        }));


        /* display existing custom foods */
        updateFoodList();


        return view;
    }

    private void updateFoodList() {

        foodItems.clear();

        /* get custom defined foods */
        ArrayList<Food> displayedFoods = db.getAllCustomFoods();
        foodItems.add(new CustomFoodOverviewItem(new Food("header", "header")));
        for (Food f : displayedFoods) {
            foodItems.add(new CustomFoodOverviewItem(f));
        }

        /* get food group templates */
        LinkedHashMap<Integer, ArrayList<Food>> foodGroupTemplates = db.getTemplateFoodGroups();
        for (Integer key : foodGroupTemplates.keySet()) {
            ArrayList<Food> fl = Objects.requireNonNull(foodGroupTemplates.get(key));
            foodItems.add(new CustomGroupOverviewItem(fl, key));
        }

        foodOverviewAdapter = new FoodOverviewAdapter(getContext(), foodItems, db);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mainRv.setLayoutManager(linearLayoutManager);
        mainRv.setAdapter(foodOverviewAdapter);
    }
}