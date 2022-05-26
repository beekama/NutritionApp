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

import java.util.ArrayList;

public class SelectorDialogAdapterAmount extends RecyclerView.Adapter {

    final int VIEW_TYPE_INPUT = 0;
    final int VIEW_TYPE_ITEM = 1;

    final DataTransfer dt;

    /* FIXME: why is amountSelected static and why is it not double and why is capital F-Float and why is it's default value null and not NaN? */
    public static Float amountSelected = null;
    private final Context context;
    private final ArrayList<Float> items;
    private static int isSelected = -1;
    private static int lastCheckPos = 0;
    private TextView lastSelected = null;

    public SelectorDialogAdapterAmount(Context context, ArrayList<Float> items, DataTransfer dataTransfer, Float defaultAmount) {
        this.context = context;
        this.items = items;
        this.dt = dataTransfer;
        this.amountSelected = defaultAmount;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;

        if (viewType == VIEW_TYPE_INPUT) {
            View view = inflater.inflate(R.layout.selector_portion_amount_inputelement, parent, false);
            return new LocalViewHolder_edit(view);
        } else if (viewType == VIEW_TYPE_ITEM) {
            View view = inflater.inflate(R.layout.selector_portion_amount_element, parent, false);
            return new LocalViewHolder(view);
        }
        throw new AssertionError("Bad ViewType in Amount Selection");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        /* EDIT-TEXT: allow numeric input */
        if (holder instanceof LocalViewHolder_edit){
            LocalViewHolder_edit editHolder = (LocalViewHolder_edit) holder;
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
                    amountSelected = Float.valueOf(s.toString());
                    dt.setValues(amountSelected);
                }
            });
        }
        /* TEXTVIEW: get currently selected */
        else {
            LocalViewHolder lvh = (LocalViewHolder) holder;
            lvh.itemContent.setText(String.valueOf(items.get(position)));
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
                        dt.setValues(amountSelected);
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
        return items.get(position) == -99.f ? VIEW_TYPE_INPUT : VIEW_TYPE_ITEM;
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

    static class LocalViewHolder_edit extends RecyclerView.ViewHolder implements View.OnClickListener {
        final EditText itemContent;

        public LocalViewHolder_edit(View itemView) {
            super(itemView);
            itemContent = itemView.findViewById(R.id.selector_edittext);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }
    }
}
