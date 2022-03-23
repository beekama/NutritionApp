package com.example.nutritionapp.foodJournal.addFoodsLists;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nutritionapp.R;

import java.util.ArrayList;
import com.example.nutritionapp.buttonUtils.UnfocusOnEnter;
import com.example.nutritionapp.other.PortionTypes;
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
            TextView amountSelectorView = convertView.findViewById(R.id.amount_selector);
            TextView portionSelectorView = convertView.findViewById(R.id.portiontype_selector);

            amountSelectorView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    // custom dialog
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.foodgroup_popup);

                    EditText etAmount = (EditText) dialog.findViewById(R.id.input);
                    etAmount.setHint("Input Age");
                    etAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

                    etAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                try{
                                    String sAmount = etAmount.getText().toString();
                                    int newAmount = Integer.parseInt(sAmount);
                                    amountSelectorView.setText(sAmount);
                                    currentItem.food.associatedAmount = newAmount;
                                    if (o != null) o.onDataChanged(newAmount);
                                    dialog.dismiss();}
                                catch (NumberFormatException e) {
                                    Toast toast = Toast.makeText(context, "Amount is not a Number,", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                                return true;
                            }
                            return false;
                        }
                    });
                    dialog.show();
                }
            });
            nameView.setText(currentItem.food.name);
            amountSelectorView.setText(Float.toString(currentItem.food.associatedAmount));
            portionSelectorView.setText(Utils.getStringIdentifier(context, currentItem.food.associatedPortionType.toString()));

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
