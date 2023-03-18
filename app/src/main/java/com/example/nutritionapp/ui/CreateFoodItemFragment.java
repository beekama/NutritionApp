package com.example.nutritionapp.ui;


import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.nutritionapp.MainActivity;
import com.example.nutritionapp.R;
import com.example.nutritionapp.customFoods.CreateFoodNutritionSelectorAdapter;
import com.example.nutritionapp.other.ActivityExtraNames;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Utils;


public class CreateFoodItemFragment extends Fragment {

    RecyclerView mainRv;
    CreateFoodItemViewModel viewModel;

    public CreateFoodItemFragment() {
        // Required empty public constructor
    }


    public static CreateFoodItemFragment newInstance(String param1) {
        CreateFoodItemFragment fragment = new CreateFoodItemFragment();
        Bundle args = new Bundle();
        args.putString(ActivityExtraNames.FDC_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create ViewModel
        ViewModelArgs viewModelArgs = new ViewModelArgs(this, new Database(requireActivity()), getArguments());
        CreateFoodItemViewModelFactory factory = new CreateFoodItemViewModelFactory(viewModelArgs);
        viewModel = new ViewModelProvider(this,factory).get(CreateFoodItemViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_food_item, container, false);

        /* replace actionbar with custom app_toolbar */
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        ImageButton toolbarBack = toolbar.findViewById(R.id.toolbar_back);
        ImageButton submit = toolbar.findViewById(R.id.toolbar_forward);

        /* return  button */
        toolbarBack.setOnClickListener((v -> Utils.navigate(CustomFoodFragment.class, (MainActivity) requireActivity())));
        toolbarBack.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitle(R.string.customItemCreate);
        submit.setImageResource(R.drawable.ic_done_black_24dp);

        /* setup adapter */
        mainRv = view.findViewById(R.id.createFoodNewItem_rv);
        mainRv.addItemDecoration(new DividerItemDecoration(mainRv.getContext(), DividerItemDecoration.VERTICAL));
        Log.wtf("WTF", String.valueOf(viewModel.getAllItems().size()));
        RecyclerView.Adapter<?> adapter = new CreateFoodNutritionSelectorAdapter(getContext(), viewModel.getAllItems());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mainRv.setLayoutManager(linearLayoutManager);
        mainRv.setAdapter(adapter);

        /* setup buttons */
        submit.setOnClickListener(v -> {
            viewModel.submitForm();
            Utils.navigate(CustomFoodFragment.class, (MainActivity) requireActivity());
        });

        return view;
    }


}