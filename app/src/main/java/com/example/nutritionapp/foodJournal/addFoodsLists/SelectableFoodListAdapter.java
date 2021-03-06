package com.example.nutritionapp.foodJournal.addFoodsLists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nutritionapp.R;

import java.util.ArrayList;

public class SelectableFoodListAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<GroupListItem> item;
    private ArrayList<GroupListItem> originalItem;

    public SelectableFoodListAdapter(Context context, ArrayList<GroupListItem> item){
        this.context=context;
        this.item=item;
    }

    public int getCount() {
        return item.size();
    }

    public Object getItem(int position) {
        return item.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (item.get(position).isSection()){
            convertView = inflater.inflate(R.layout.journal_add_food_selection_list, parent, false);
            TextView HeaderTextView = convertView.findViewById(R.id.dateText);
            HeaderTextView.setText(( item.get(position).getTitle()));
        }
        else{
            convertView = inflater.inflate(R.layout.journal_add_food_selection_item,parent,false);
            TextView ItemListView = convertView.findViewById(R.id.ListTextView);
            ItemListView.setText(( item.get(position).getTitle()));
        }
        return convertView;
    }
}
