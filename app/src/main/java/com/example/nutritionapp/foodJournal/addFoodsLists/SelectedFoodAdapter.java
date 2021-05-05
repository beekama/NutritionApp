package com.example.nutritionapp.foodJournal.addFoodsLists;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nutritionapp.R;

import java.util.ArrayList;
import com.example.nutritionapp.buttonUtils.UnfocusOnEnter;
import com.example.nutritionapp.other.PortionTypes;

public class SelectedFoodAdapter extends BaseAdapter {

    private Context context;
    public ArrayList<SelectedFoodItem> items;
    public static OnDataChangeListener o;

    public SelectedFoodAdapter(){
        super();
    }
    public SelectedFoodAdapter(Context context, ArrayList<SelectedFoodItem> items){
        this.context=context;
        this.items=items;
    }

    public int getCount() {
        return items.size();
    }

    public Object getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.journal_add_food_selected_item, parent, false);
        SelectedFoodItem currentItem = items.get(position);
        TextView nameView = convertView.findViewById(R.id.item_name);
        EditText amountSelectorView = convertView.findViewById(R.id.amount_selector);
        TextView portionSelectorView = convertView.findViewById(R.id.portiontype_selector);

        amountSelectorView.setOnKeyListener(new UnfocusOnEnter());
        amountSelectorView.setOnKeyListener((v, key, keyEvent) -> {
            SelectedFoodItem item = (SelectedFoodItem) this.getItem(position);Log.wtf("net null","");
            try{
                Integer amount = Integer.parseInt(amountSelectorView.getText().toString());
                item.food.associatedAmount = amount;
                if (o != null) o.onDataChanged(amount);
            }catch(NumberFormatException e){
                Toast toast = Toast.makeText(context, "Amount is not a Number,", Toast.LENGTH_LONG);
                toast.show();
            }
            return false;
        });

        nameView.setText(currentItem.food.name);
        amountSelectorView.setText(Float.toString(currentItem.food.associatedAmount));
        portionSelectorView.setText(currentItem.food.associatedPortionType.toString());
        return  convertView;
    }


    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){
        o = onDataChangeListener;
    }

    public interface OnDataChangeListener{
        // 'amount'-parameter (currently) does not matter at all. At the momentent its only about recognizing data changes
        public void onDataChanged(int amount);
    }
}
