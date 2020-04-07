package com.example.myapplication.lists;

import com.example.myapplication.food_journal;

public class ListItem implements GenericListItem{
        public final String title;

        public ListItem(String title) {
            this.title = title;
        }

        public boolean isSection() {
            return false;
        }

        public String getTitle() {
            return title;
        }
}
