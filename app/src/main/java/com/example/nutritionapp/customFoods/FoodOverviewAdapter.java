package com.example.nutritionapp.customFoods;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.R;
import com.example.nutritionapp.foodJournal.FoodGroupOverview;
import com.example.nutritionapp.other.Database;

import java.util.ArrayList;

public class FoodOverviewAdapter extends RecyclerView.Adapter {

    private final Context context;
    public final ArrayList<FoodOverviewItem> items;
    final int VIEW_TYPE_HEADER = 0;
    final int VIEW_TYPE_ITEM = 1;
    final Database db;

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

        if(viewType == VIEW_TYPE_ITEM) {
            View view = inflater.inflate(R.layout.food_overview_item, parent, false);
            return new LocalViewHolder(view);
        }else if(viewType == VIEW_TYPE_HEADER){
            View view = inflater.inflate(R.layout.food_overview_header, parent, false);
            return new FoodOverviewAdapter.LocalHeaderViewHolder(view);
        }
        throw new AssertionError("Bad ViewType in Personal Information Adapter");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FoodOverviewItem item = items.get(position);

        /* distinguish header-item and item */
        if(holder instanceof FoodOverviewAdapter.LocalHeaderViewHolder){
            FoodOverviewAdapter.LocalHeaderViewHolder headerViewHolder = (FoodOverviewAdapter.LocalHeaderViewHolder) holder;
            if (getItemCount() == 1){
                headerViewHolder.textView.setText(R.string.customFoodHeaderEmpty);
            } else {
                headerViewHolder.textView.setText(R.string.customFoodHeader);
            }
            return;
        } else {
            FoodOverviewAdapter.LocalViewHolder lvh = (FoodOverviewAdapter.LocalViewHolder) holder;
            lvh.nameView.setText(item.food.name);
            lvh.itemView.setOnClickListener(v -> {
                FoodOverviewItem item12 = items.get(position);
                if(item12.isGroup) {
                    /* open indent from journal to edit a group of goods */
                    Intent editCustomFoodGroup = new Intent(context, FoodGroupOverview.class);
                    editCustomFoodGroup.putExtra("groupId", Integer.parseInt(item12.food.id));
                    editCustomFoodGroup.putExtra("isTemplateMode", true);
                    context.startActivity(editCustomFoodGroup);
                }else {
                    /* open indent for editing a single food */
                    Intent editCustomFood = new Intent(context, CreateNewFoodItem.class);
                    editCustomFood.putExtra("fdc_id", item12.food.id);
                    context.startActivity(editCustomFood);
                }
            });
            lvh.itemView.setOnLongClickListener(v -> {
                FoodOverviewItem item1 = items.get(position);
                db.deleteCustomFood(item1.food);
                items.remove(item1);
                notifyDataSetChanged();
                return true;
            });
        }
    }


    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }


    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    private static class LocalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView nameView;
        public LocalViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            nameView = itemView.findViewById(R.id.custom_food_name);
        }

        @Override
        public void onClick(View v) {

        }
    }

    static class LocalHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView textView;

        LocalHeaderViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.custom_food_header_text);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
