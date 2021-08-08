package com.example.nutritionapp.customFoods;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.MainActivity;
import com.example.nutritionapp.R;

import java.util.ArrayList;

public class CreateFoodNutritionSelectorAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final ArrayList<CreateFoodNutritionSelectorItem> items;
    View convertView;

    public CreateFoodNutritionSelectorAdapter(Context context, ArrayList<CreateFoodNutritionSelectorItem> items){
        this.context = context;
        this.items   = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        convertView = inflater.inflate(R.layout.create_food_nutrition_selection_item, parent, false);
        return new LocalViewHolder(convertView);}


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LocalViewHolder lvh = (LocalViewHolder) holder;

        /* item at position */
        CreateFoodNutritionSelectorItem item = items.get(position);

        if(item.inputTypeString){
            lvh.value.setInputType(EditText.AUTOFILL_TYPE_TEXT);
        }

        lvh.name.setText(item.tag);
        Log.wtf("SET", item.tag);
        if(!item.inputTypeString) {
            if(item.amount > 0 ) {
                lvh.value.setText(Integer.toString(item.amount));
            }
        }else if(item.data != null){
            lvh.value.setText(item.data);
        }else{
            lvh.value.setText("");
        }
        lvh.value.setHint(item.unit);

        lvh.value.setOnFocusChangeListener((v, i) -> {
            String cur = lvh.value.getText().toString();
            if(cur.equals("")){
                return;
            }
            try{
                if(item.inputTypeString){
                    item.data = lvh.value.getText().toString();
                }else {
                    item.amount = Integer.parseInt(cur);
                }
            }catch(NumberFormatException e){
                Log.e("ERROR", cur);
                Toast toast = Toast.makeText(context, "Amount is not a Number when it should be.", Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private class LocalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name;
        EditText value;

        public LocalViewHolder(View itemView) {
            super(itemView);
            // itemView.setOnClickListener(this);
             name = convertView.findViewById(R.id.name);
             value = convertView.findViewById(R.id.value);
        }

        @Override
        public void onClick(View v) {

        }
    }

}
