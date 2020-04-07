package com.example.nutritionapp.foodJournal;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritionapp.ButtonUtils.UnfocusOnEnter;
import com.example.nutritionapp.foodJournal.AddFoodsLists.*;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Food;

import java.util.ArrayList;

public class AddFoodToJournal extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_food_to_journal);
        EditText searchFieldEditText = findViewById(R.id.searchField);

        final ListView lv = (ListView) findViewById(R.id.listview);
        final ArrayList<GenericListItem> inputList = new ArrayList<GenericListItem>();
        Database db = new Database(this);
        //ListItem nextItem = new ListItem("Hallo");
        //inputList.add(nextItem);

        TextWatcher filterNameTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Food> foods = db.getFoodsByPartialName(s.toString());
                inputList.clear();
                for(Food f : foods){
                    inputList.add(new ListItem(f.name));
                }

                /* I know this looks bad but notifyDatasetChanged() is way too slow */
                lv.invalidate();
                ListAdapter adapter = new ListAdapter(getApplicationContext(), inputList);
                lv.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        searchFieldEditText.addTextChangedListener(filterNameTextWatcher);
        searchFieldEditText.setOnKeyListener(new UnfocusOnEnter());


        Button back = findViewById(R.id.backToJournal);
        back.setOnClickListener(v -> { finish(); db.close(); });

    }
}
