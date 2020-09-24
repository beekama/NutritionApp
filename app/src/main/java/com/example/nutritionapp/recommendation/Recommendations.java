package com.example.nutritionapp.recommendation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nutritionapp.R;
import com.example.nutritionapp.Test_chart;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.NutritionElement;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


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
        if (!(allFood.isEmpty())) {
            nutritionAnalysis = new NutritionAnalysis(allFood);
            for (NutritionElement ne : NutritionElement.values()) {
                nutritionItems.add(new RecommendationListItem(ne.toString(), nutritionAnalysis.getNutritionPercentage().get(ne), new ArrayList<>()));// ;
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
    ArrayList<PieEntry> pieEntryList;

    public RecommendationListItem(String tag, float percentage, ArrayList<PieEntry> pieEntryList) {
        this.tag = tag;
        this.percentage = percentage;
        this.pieEntryList = pieEntryList;
    }
}


class RecommendationAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<RecommendationListItem> items;
    // List<PieEntry> pieEntryList = new ArrayList<>();
    PieData pieData;


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
        PieChart rec_chart = convertView.findViewById(R.id.pieChar);

        /*Text-column*/
        rec_item.setText(item.tag);

        /*chart*/
        ArrayList<PieEntry> pieEntryList = item.pieEntryList;
        pieEntryList.add(new PieEntry(item.percentage, item.tag));
        pieEntryList.add(new PieEntry(100 - item.percentage, "missing"));
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "alldata");
        //Log.wtf("ZZZ--"+item.tag,((Float)item.percentage).toString());

        //STYLING:
        rec_chart.setUsePercentValues(true);
        pieDataSet.setColors(Color.parseColor("#006400"), Color.parseColor("#C8FFC8"), Color.GRAY, Color.BLACK, Color.BLUE); //!! extra color for debugging
        pieDataSet.setDrawValues(false);
        // rec_chart.setCenterText(generateCenterSpannableText((Float)item.percentage));
        // rec_chart.getDescription().setEnabled(false);
        rec_chart.getLegend().setEnabled(false);
        rec_chart.setDrawSliceText(false);
        rec_chart.setRotationEnabled(false);
        rec_chart.setHighlightPerTapEnabled(false);
        rec_chart.setHoleColor(Color.TRANSPARENT);

        //PERCENTAGE-LABEL:
        rec_chart.getDescription().setText(String.format("%.2f %%", item.percentage));
        rec_chart.getDescription().setPosition(300f, 25f);   //!! anpassen bei einfügen von 'recommendations'-textview
        rec_chart.notifyDataSetChanged();

        pieData = new PieData(pieDataSet);
        rec_chart.setData(pieData);



        return convertView;
    }

    private SpannableString generateCenterSpannableText(Float in) {
        String inString = String.format("%.2f %%", in);
        SpannableString s = new SpannableString(inString);
        s.setSpan(new RelativeSizeSpan(0.8f), 0, inString.length(), 0);
        return s;
    }
}