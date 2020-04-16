package com.example.nutritionapp.foodJournal.AddFoodsLists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nutritionapp.R;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<GroupListItem> item;
    private ArrayList<GroupListItem> originalItem;

    public ListAdapter(){
        super();
    }
    public ListAdapter(Context context, ArrayList<GroupListItem> item){
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
            TextView HeaderTextView = (TextView) convertView.findViewById(R.id.dateText);
            HeaderTextView.setText(( item.get(position).getTitle()));
        }
        else{
            convertView = inflater.inflate(R.layout.journal_add_food_selection_item,parent,false);
            TextView ItemListView = (TextView) convertView.findViewById(R.id.ListTextView);
            ItemListView.setText(( item.get(position).getTitle()));
        }
        return convertView;
    }
}
