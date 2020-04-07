package com.example.nutritionapp.AddFoodsLists;

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
