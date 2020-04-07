package com.example.myapplication.lists;

import com.example.myapplication.food_journal;

public class ListHeaderItem implements  GenericListItem{
        private final String title;

        public ListHeaderItem(String title) {
            this.title = title;
        }

        public boolean isSection() {
            return true;
        }

        public String getTitle() {
            return title;
        }
}
