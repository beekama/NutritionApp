package com.example.nutritionapp.foodJournal.overviewFoodsLists;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Utils;

import java.util.ArrayList;

public class SelectorDialogAdapterAmount extends RecyclerView.Adapter {

    final int VIEW_TYPE_INPUT = 0;
    final int VIEW_TYPE_ITEM = 1;

    final DataTransfer parent;

    public double amountSelected = Double.NaN;
    private final Context context;
    private ArrayList<Double> items;
    private static int isSelected = -1;
    private static int lastCheckPos = 0;
    private TextView lastSelected = null;

    public SelectorDialogAdapterAmount(Context context, ArrayList<Double> items, DataTransfer dataTransfer, double defaultAmount) {
        this.context = context;
        this.items = items;
        this.parent = dataTransfer;
        this.amountSelected = defaultAmount;
    }

    public int findValuePositionInItems(double d){
        return Math.max(items.indexOf(d), 0);
    }

    public void setItems(ArrayList<Double> items){
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;

        if (viewType == VIEW_TYPE_INPUT) {
            View view = inflater.inflate(R.layout.selector_portion_amount_custom_element, parent, false);
            return new LocalViewHolderCustomAmount(view);
        } else if (viewType == VIEW_TYPE_ITEM) {
            View view = inflater.inflate(R.layout.selector_amount_element, parent, false);
            return new LocalViewHolder(view);
        }
        throw new AssertionError("Bad ViewType in Amount Selection");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        /* handle initial selected state */


        /* EDIT-TEXT: allow numeric input */
        if (holder instanceof LocalViewHolderCustomAmount){
            LocalViewHolderCustomAmount editHolder = (LocalViewHolderCustomAmount) holder;
            editHolder.itemContent.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (lastSelected != null) {
                        lastSelected.setSelected(false);
                    }

                    lastCheckPos = holder.getAdapterPosition();
                    try {
                        amountSelected = Utils.parseVisualizedDouble(s.toString());
                    }catch(NumberFormatException e){
                        amountSelected = 0;
                    }
                    parent.setAmountSelected(amountSelected);
                }
            });
        }
        /* TEXTVIEW: get currently selected */
        else {
            LocalViewHolder lvh = (LocalViewHolder) holder;
            lvh.itemContent.setText(Utils.visualizedDouble(items.get(position)));
            if (items.get(position).equals(amountSelected)) {
                lvh.itemContent.setSelected(true);
                lastSelected = lvh.itemContent;
            } else lvh.itemContent.setSelected(false);
            if (position == 0 && lvh.itemContent.isSelected()) {
                lastSelected = lvh.itemContent;
                lastCheckPos = 0;
            }
            lvh.itemContent.setOnClickListener(
                    v -> {
                        Log.wtf("GIVEN POSITION", Integer.toString(position));
                        Log.wtf("CALCULATED POSITION", Integer.toString(lvh.getLayoutPosition()));
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
                        parent.setAmountSelected(amountSelected);
                    }

            );
        }
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

    @Override
    public int getItemViewType(int position) {
        return items.get(position) <= 0 ? VIEW_TYPE_INPUT : VIEW_TYPE_ITEM;
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

    static class LocalViewHolderCustomAmount extends RecyclerView.ViewHolder implements View.OnClickListener {
        final EditText itemContent;

        public LocalViewHolderCustomAmount(View itemView) {
            super(itemView);
            itemContent = itemView.findViewById(R.id.selector_edittext);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }
    }
}
