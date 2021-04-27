package com.example.nutritionapp.foodJournal.overviewFoodsLists;

import com.example.nutritionapp.other.PortionTypes;

public interface DataTransfer {
    PortionTypes p = null;
    Float a = null;
    void setValues(PortionTypes p);
    void getValues();
    void setValues(Float a);
}
