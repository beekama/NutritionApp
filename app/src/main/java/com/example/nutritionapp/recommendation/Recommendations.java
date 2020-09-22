package com.example.nutritionapp.recommendation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.nutritionapp.R;
import com.example.nutritionapp.Test_chart;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.NutritionElement;
import org.threeten.bp.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;


public class Recommendations extends AppCompatActivity {

    private Database db;
    HashMap<Integer, ArrayList<Food>> foodList;
    ArrayList<Food> allFood = new ArrayList<>();
    NutritionAnalysis nutritionAnalysis;


    LocalDate startDateParsed = LocalDate.now();
    LocalDate endDateParsed = LocalDate.now();


    public void onCreate(Bundle savedInstanceState) {
        //splash screen when needed:
        setTheme(R.style.AppTheme);

        //basic settings:
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendation);
        db = new Database(this);
        foodList = db.getLoggedFoodsByDate(startDateParsed, endDateParsed);


        //replace actionbar with custom app_toolbar:
        Toolbar tb = findViewById(R.id.toolbar);
        TextView tb_title = findViewById(R.id.toolbar_title);
        ImageButton tb_back = findViewById(R.id.toolbar_back);
        ImageButton tb_forward = findViewById(R.id.toolbar_forward);

        //visible title:
        tb_back.setImageResource(R.drawable.ic_arrow_back_black_24dp);

        //FOR CHART TESTING
        tb_forward.setImageResource(R.drawable.add_circle_filled);
        tb.setTitle("");
        tb_title.setText("RECOMMENDATIONS");
        setSupportActionBar(tb);
        //refresh:
        tb_forward.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // updateRecommendations(true);
                Intent myIntent = new Intent(v.getContext(), Test_chart.class);
                startActivity(myIntent);
            }
        }));

        // NutritionAnalysis-data:
        //todo: implement in db?
        for (ArrayList<Food> al : foodList.values()) {
            for (Food food : al) {
                if (food != null) {
                    allFood.add(food);
                }
            }
        }


        // add nutrition items:
        ArrayList<RecommendationListItem> nutritionItems = new ArrayList<>();
        ListView mainLv = findViewById(R.id.listview);
        if (!(allFood == null)) {
            nutritionAnalysis = new NutritionAnalysis(allFood);
            for (NutritionElement ne : NutritionElement.values()) {
                nutritionItems.add(new RecommendationListItem(ne.toString(), nutritionAnalysis.getNutritionPercentage().get(ne)));
            }

        }

        //adapter:
        RecommendationAdapter newAdapter = new RecommendationAdapter(getApplicationContext(), nutritionItems);
        mainLv.setAdapter(newAdapter);


        //back home button:
        tb_back.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }));
    }

}

class RecommendationListItem {
        final String tag;
        float percentage;

        public RecommendationListItem(String tag, float percentage) {
            this.tag = tag;
            this.percentage = percentage;
        }
    }



class RecommendationAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<RecommendationListItem> items;

    public RecommendationAdapter() {
        super();
    }

    public RecommendationAdapter(Context context, ArrayList<RecommendationListItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RecommendationListItem item = this.items.get(position);

        /* initiate LayoutInflater */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.recommendation_nutritions, parent, false);
        }

        /* sub views */
        TextView rec_item = convertView.findViewById(R.id.nutritionName);

        rec_item.setText(item.tag);


        return convertView;
    }}