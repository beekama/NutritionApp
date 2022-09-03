package com.example.nutritionapp.foodJournal.overviewFoodsLists;

import com.example.nutritionapp.other.PortionType;

public interface DataTransfer {
    PortionType p = null;
    Float a = null;
    void setPortionType(PortionType p);
    void getValues();
    void setAmountSelected(double a);
}
