package com.example.nutritionapp.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import com.example.nutritionapp.other.Database;

public class ViewModelArgs {
    private Fragment fragment;
    private Database db;
    private Bundle args;

    public ViewModelArgs(Fragment fragment, Database db, Bundle args){
        this.fragment = fragment;
        this.db = db;
        this.args = args;
    }

    public Database getDatabase(){
        return db;
    }
    public Fragment getFragment(){
        return fragment;
    }
    public Bundle getArgs(){
        return args;
    }
}
