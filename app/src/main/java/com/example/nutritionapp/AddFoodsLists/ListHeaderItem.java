package com.example.nutritionapp.AddFoodsLists;

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
