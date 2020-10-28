package com.example.nutritionapp.foodJournal.OverviewFoodsLists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.R;

import java.util.ArrayList;

public class SelectorDialogAdapterAmount extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<Integer> items;

    public SelectorDialogAdapterAmount(Context context, ArrayList<Integer> items){
        this.context = context;
        this.items   = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.journal_selector_dialog_item, parent, false);
        return new LocalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LocalViewHolder lvh = (LocalViewHolder) holder;
        lvh.itemContent.setText(items.get(position).toString());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class LocalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemContent;
        boolean isSelected = false;

        LocalViewHolder(View itemView) {
            super(itemView);
            itemContent = itemView.findViewById(R.id.itemText);
        }

        @Override
        public void onClick(View view) {
        }
    }
}