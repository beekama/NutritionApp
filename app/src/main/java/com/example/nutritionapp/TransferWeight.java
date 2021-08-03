package com.example.nutritionapp;

import java.time.LocalDate;

public interface TransferWeight {
    LocalDate date = null;
    int weight = 0;
    void removeEntry(int weight, LocalDate date);
}
