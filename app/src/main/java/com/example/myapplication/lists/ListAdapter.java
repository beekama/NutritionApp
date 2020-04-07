package com.example.myapplication.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.food_journal;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<GenericListItem> item;
    private ArrayList<GenericListItem> originalItem;

    public ListAdapter(){
        super();
    }
    public ListAdapter(Context context, ArrayList<GenericListItem> item){
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
            convertView = inflater.inflate(R.layout.list_header, parent, false);
            TextView HeaderTextView = (TextView) convertView.findViewById(R.id.HeaderTextView);
            HeaderTextView.setText(( item.get(position).getTitle()));
        }
        else{
            convertView = inflater.inflate(R.layout.list_item,parent,false);
            TextView ItemListView = (TextView) convertView.findViewById(R.id.ListTextView);
            ItemListView.setText(( item.get(position).getTitle()));
        }
        return convertView;
    }
}
