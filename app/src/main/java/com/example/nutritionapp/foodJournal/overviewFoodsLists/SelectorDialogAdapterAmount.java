package com.example.nutritionapp.foodJournal.overviewFoodsLists;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.PortionTypes;

import java.util.ArrayList;

public class SelectorDialogAdapterAmount extends RecyclerView.Adapter {

    DataTransfer dt;
    public static Float amountSelected = null;
    private final Context context;
    private final ArrayList<Float> items;
    private static int isSelected = -1;
    private static int lastCheckPos = 0;
    private static TextView lastSelected = null;

    public SelectorDialogAdapterAmount(Context context, ArrayList<Float> items, DataTransfer dataTransfer) {
        this.context = context;
        this.items = items;
        this.dt = dataTransfer;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.selector_portion_amount_element, parent, false);
        return new LocalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LocalViewHolder lvh = (LocalViewHolder) holder;
        lvh.itemContent.setText(items.get(position).toString());
        if (position == 0) lvh.itemContent.setSelected(true);
        if (position == 0 && lvh.itemContent.isSelected()) {
            lastSelected = lvh.itemContent;
            lastCheckPos = 0;
        }
        lvh.itemContent.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView t = (TextView) v;
                        int clickPos = lvh.getAdapterPosition();

                        if ((lastSelected != null) && (lastSelected != t)) {
                            lastSelected.setSelected(false);
                        }
                        lastSelected = t;
                        lastCheckPos = clickPos;

                        t.setSelected(true);
                        isSelected = clickPos;
                        amountSelected = items.get(position);
                        dt.setValues(amountSelected);
                       Log.wtf("SELDIAAMOUNT", Float.toString(amountSelected));
                    }
                }

        );
    }

    public void selectedItem() {
        notifyDataSetChanged();
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
        final TextView itemContent;

        LocalViewHolder(View itemView) {
            super(itemView);
            itemContent = itemView.findViewById(R.id.selector_textview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }
    }
}
