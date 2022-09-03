package com.example.nutritionapp.foodJournal.addFoodsLists;

import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nutritionapp.R;

import java.util.ArrayList;

import com.example.nutritionapp.other.Utils;

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
        SelectedFoodItem currentItem = items.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.journal_add_food_selected_item, parent, false);
            TextView nameView = convertView.findViewById(R.id.item_name);
            Button amountSelectorButton = convertView.findViewById(R.id.amount_selector);
            Button portionSelectorButton = convertView.findViewById(R.id.portion_type_selector);

            amountSelectorButton.setOnClickListener(v -> {

                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.foodgroup_popup);

                EditText etAmount = dialog.findViewById(R.id.input);
                etAmount.setHint("Amount");
                etAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

                etAmount.setOnEditorActionListener((v1, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        try {
                            String sAmount = etAmount.getText().toString();
                            int newAmount = Integer.parseInt(sAmount);
                            amountSelectorButton.setText(sAmount);
                            currentItem.food.associatedAmount = newAmount;
                            if (o != null) o.onDataChanged(newAmount);
                            dialog.dismiss();
                        } catch (NumberFormatException e) {
                            Toast toast = Toast.makeText(context, "Amount is not a Number,", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        return true;
                    }
                    return false;
                });
                dialog.show();
            });
            nameView.setText(currentItem.food.name);
            amountSelectorButton.setText(String.valueOf(currentItem.food.associatedAmount));

            // TODO: button clickbar mit auswahlmenue
            portionSelectorButton.setText(Utils.getStringIdentifier(context, currentItem.food.associatedPortionType.toString()));

        return  convertView;
    }


    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){
        o = onDataChangeListener;
    }

    public interface OnDataChangeListener{
        /* FIXME: see comment below */
        // 'amount'-parameter (currently) does not matter at all. At the moment its only about recognizing data changes
        void onDataChanged(int amount);
    }
}
