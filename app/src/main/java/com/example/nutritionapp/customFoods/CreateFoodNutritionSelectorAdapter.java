package com.example.nutritionapp.customFoods;

import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.R;

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
            return;
        } else {
            CreateFoodNutritionSelectorAdapter.LocalViewHolder lvh = (CreateFoodNutritionSelectorAdapter.LocalViewHolder) holder;
//            lvh.name.setText(item.tag);
            if (!item.inputTypeString) {
                if (item.amount > 0) {
                    lvh.value.setText(String.valueOf(item.amount));
                }
            } else if (item.data != null) {
                lvh.value.setText(item.data);
            } else {
                lvh.value.setText("");
            }
            lvh.name.setText(item.tag);
            lvh.background.setOnClickListener(v -> {
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.create_food_popup);

                EditText et = dialog.findViewById(R.id.input);
                et.setHint("Input");

                if (!item.inputTypeString) et.setInputType(InputType.TYPE_CLASS_NUMBER);
                else et.setInputType(InputType.TYPE_CLASS_TEXT);

                et.setOnEditorActionListener((v1, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        try {
                            String etString = et.getText().toString();
                            lvh.value.setText(etString);
                            if (item.inputTypeString) {
                                item.data = etString;
                            } else {
                                item.amount = Integer.parseInt(etString);
                            }
                            dialog.dismiss();
                        } catch (IllegalArgumentException e) {
                            Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        return true;
                    }
                    return false;
                });
                dialog.show();
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
        final View background;
        final TextView name;
        final TextView value;

        LocalViewHolder(View itemView) {
            super(itemView);
            background = itemView;
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
