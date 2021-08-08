package com.example.nutritionapp.customFoods;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Database;

import java.util.ArrayList;

public class FoodOverviewAdapter extends RecyclerView.Adapter {

    private Context context;
    public ArrayList<FoodOverviewItem> items;
    View convertView;
    Database db;

    public FoodOverviewAdapter(Context context, ArrayList<FoodOverviewItem> items, Database db) {
        this.context = context;
        this.items = items;
        this.db = db;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        convertView = inflater.inflate(R.layout.food_overview_item, parent, false);
        return new LocalViewHolder(convertView);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LocalViewHolder lvh = (LocalViewHolder) holder;
        FoodOverviewItem item = items.get(position);
        lvh.nameView.setText(item.food.name);
        lvh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodOverviewItem item = items.get(position);
                Intent editCustomFood = new Intent(convertView.getContext(), CreateNewFoodItem.class);
                editCustomFood.putExtra("fdc_id", item.food.id);
                context.startActivity(editCustomFood);
            }
        });
        lvh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FoodOverviewItem item = items.get(position);
                db.deleteCustomFood(item.food);
                items.remove(item);
                notifyDataSetChanged();
                return true;
            }
        });
    }


    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    private class LocalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameView;
        public LocalViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            nameView = convertView.findViewById(R.id.custom_food_name);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
