package com.example.nutritionapp.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CreateFoodItemViewModelFactory implements ViewModelProvider.Factory {
    private final ViewModelArgs args;
    public CreateFoodItemViewModelFactory(ViewModelArgs args) {
        this.args = args;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CreateFoodItemViewModel.class)) {
            return (T) new CreateFoodItemViewModel(args.getDatabase(), args.getFragment(), args.getArgs());
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
