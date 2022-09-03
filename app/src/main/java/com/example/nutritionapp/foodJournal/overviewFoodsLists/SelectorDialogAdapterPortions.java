package com.example.nutritionapp.foodJournal.overviewFoodsLists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.PortionType;

import java.util.ArrayList;

public class SelectorDialogAdapterPortions extends RecyclerView.Adapter<SelectorDialogAdapterPortions.LocalViewHolder> {
    final DataTransfer dt;
    public PortionType typeSelected ;
    private final Context context;
    private final ArrayList<PortionType> items;
    private static int isSelected = -1;
    private static int lastCheckPos = 0;
    private TextView lastSelected = null;


    public SelectorDialogAdapterPortions(Context context, ArrayList<PortionType> items, DataTransfer dataTransfer, PortionType type) {
        this.context = context;
        this.items = items;
        this.dt = dataTransfer;
        this.typeSelected = type;
    }

    @NonNull
    @Override
    public LocalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.selector_portion_element, parent, false);
        return new LocalViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull LocalViewHolder holder, int position) {

        // TODO: make type string lowercase/only first letter upper case
        String typeString = items.get(position).toString();
        holder.itemContent.setText(typeString);

        if (items.get(position) == typeSelected){
            holder.itemContent.setSelected(true);
            lastSelected = holder.itemContent;
        } else{
            holder.itemContent.setSelected(false);
        }

        if (position == 0 && holder.itemContent.isSelected()) {
            lastSelected = holder.itemContent;
            lastCheckPos = 0;
        }

        holder.itemContent.setOnClickListener(v -> {
            TextView t = (TextView) v;
            int clickPos = holder.getAdapterPosition();

            if ((lastSelected != null) && (lastSelected != t)) {
                lastSelected.setSelected(false);
            }
            lastSelected = t;
            lastCheckPos = clickPos;

            t.setSelected(true);
            isSelected = clickPos;
            typeSelected = items.get(position);
            dt.setPortionType(typeSelected);
        });
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

    public int findPortionPositionInItems(PortionType typeSelected) {
        return Math.max(items.indexOf(typeSelected), 0);
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
