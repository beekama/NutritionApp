package com.example.nutritionapp.customFoods;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.SimpleInputPopup;

import java.util.ArrayList;

public class CreateFoodNutritionSelectorAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final ArrayList<CreateFoodNutritionSelectorItem> items;
    final int VIEW_TYPE_HEADER = 0;
    final int VIEW_TYPE_ITEM = 1;
    View convertView;

    public CreateFoodNutritionSelectorAdapter(Context context, ArrayList<CreateFoodNutritionSelectorItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;

        if (viewType == VIEW_TYPE_ITEM) {
            View view = inflater.inflate(R.layout.create_food_nutrition_selection_item, parent, false);
            return new CreateFoodNutritionSelectorAdapter.LocalViewHolder(view);
        } else if (viewType == VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.create_food_nutrition_selection_header, parent, false);
            return new CreateFoodNutritionSelectorAdapter.LocalHeaderViewHolder(view);
        }

        convertView = inflater.inflate(R.layout.create_food_nutrition_selection_item, parent, false);
        return new LocalViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        /* item at position */
        CreateFoodNutritionSelectorItem item = items.get(position);

        /* distinguish header-item and item */
        if (holder instanceof CreateFoodNutritionSelectorAdapter.LocalHeaderViewHolder) {

            CreateFoodNutritionSelectorAdapter.LocalHeaderViewHolder headerViewHolder = (CreateFoodNutritionSelectorAdapter.LocalHeaderViewHolder) holder;
            headerViewHolder.textView.setText(item.tag);

        } else {

            CreateFoodNutritionSelectorAdapter.LocalViewHolder localeViewHolder = (CreateFoodNutritionSelectorAdapter.LocalViewHolder) holder;
            localeViewHolder.name.setText(item.tag);

            /* set current value */
            if (item.amount > 0) {
                localeViewHolder.value.setText(String.valueOf(item.amount));
            } else if (item.data != null) {
                localeViewHolder.value.setText(item.data);
            } else {
                localeViewHolder.value.setText("");
            }

            localeViewHolder.root.setOnClickListener(v -> {

                /* determine input type */
                int inputType;
                if (item.inputTypeString){
                    inputType = InputType.TYPE_CLASS_TEXT;
                }else{
                    inputType = InputType.TYPE_CLASS_NUMBER;
                }

                /* display popup */
                final SimpleInputPopup inputPopup = new SimpleInputPopup(context, item.tag.toString(), item.tag.toString(), inputType);
                inputPopup.setOnDismissListener(dialog -> {
                    item.amount = (int)inputPopup.numberValue;
                    item.data = inputPopup.getStringValue();

                    /* show the new values in the overview */
                    if (item.inputTypeString){
                        localeViewHolder.value.setText(item.data);
                    } else {
                        localeViewHolder.value.setText(String.valueOf(item.amount));
                    }
                });
                inputPopup.show();

            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).header ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
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
        final View root;
        final TextView name;
        final TextView value;

        LocalViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            name = itemView.findViewById(R.id.create_foods_item_text);
            value = itemView.findViewById(R.id.create_foods_item_input);
        }

        @Override
        public void onClick(View view) {

        }
    }

    static class LocalHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View background;
        final TextView textView;

        LocalHeaderViewHolder(View itemView) {
            super(itemView);
            background = itemView;
            textView = itemView.findViewById(R.id.create_foods_header_text);
        }

        @Override
        public void onClick(View view) {

        }
    }

}
