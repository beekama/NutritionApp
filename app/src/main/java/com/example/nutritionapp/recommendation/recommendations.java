package com.example.nutritionapp.recommendation;

import android.content.Context;
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
import com.example.nutritionapp.other.Database;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;


public class recommendations extends AppCompatActivity {

    //time-data:
    private LocalDate now = LocalDate.now();
    private LocalDate oldestDateShown = LocalDate.now().minusWeeks(1);
    final private Duration ONE_DAY = Duration.ofDays(1);
    final private Duration ONE_WEEK = Duration.ofDays(7);
/*    final private ArrayList<RecommendationListItem> inputList = new ArrayList<RecommendationListItem>();*/
    final private ArrayList<com.example.nutritionapp.recommendation.recommendationListItem> inputList = new ArrayList<>();

    private recommendationAdapter adapter;
    private Database db;
    private ListView deficienciesList;


    public void onCreate(Bundle savedInstanceState) {
        //splash screen when needed:
        setTheme(R.style.AppTheme);

        //basic settings:
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendation);
        Database db =new Database(this);

        //retrieve items:
        updateRecommendations(false);   //todo

        //adapter:
        adapter = new recommendationAdapter(this, inputList);
        deficienciesList = (ListView) findViewById(R.id.listview);
        deficienciesList.setAdapter(adapter);
        deficienciesList.setTextFilterEnabled(true);

        //replace actionbar with custom app_toolbar:
        Toolbar tb = findViewById(R.id.toolbar);
        TextView tb_title = findViewById(R.id.toolbar_title);
        ImageButton tb_back = findViewById(R.id.toolbar_back);
        ImageButton tb_forward = findViewById(R.id.toolbar_forward);
        //visible title:
        tb_back.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        tb_forward.setImageResource(R.drawable.add_circle_filled);
        tb.setTitle("");
        tb_title.setText("RECOMMENDATIONS");
        setSupportActionBar(tb);
        //refresh:
        tb_forward.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRecommendations(true);
            }
        }));


        //back home button:
        tb_back.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }));}

        private void updateRecommendations(boolean runInvalidation){
            LocalDate startDate = now.atStartOfDay().toLocalDate();
            /* z.B. db.getByDate(now, now - week); n = new NutritionAnalysis(); n.getPercentagesSorted(); */
            inputList.clear();
            inputList.add(new WeekBreakHeader("current week"));
            inputList.add(new DeficiencyItem("iron"));
        }


    }

class WeekBreakHeader implements recommendationListItem{
    private final String title;

    public WeekBreakHeader(String title){
        this.title = title;
    }

    @Override
    public boolean isSection() {
        return true;
    }

    @Override
    public String getTitle() {
        return title;
    }
}

class DeficiencyItem implements recommendationListItem{
    private final String title;
    public DeficiencyItem(String title){
        this.title = title;
    }


    @Override
    public boolean isSection() {
        return false;
    }

    @Override
    public String getTitle() {
        return title;
    }
}

interface recommendationListItem{
    public boolean isSection();
    public String getTitle();
        }

class recommendationAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<recommendationListItem> item;

    public recommendationAdapter(){
        super();
    }

    public recommendationAdapter(Context context, ArrayList<recommendationListItem> item){
        this.context = context;
        this.item = item;
    }

    @Override
    public int getCount() {
        return item.size();
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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (item.get(position).isSection()){
            convertView = inflater.inflate(R.layout.recommendation_deficiencyheader,parent,false);
            TextView tv_header = (TextView) convertView.findViewById(R.id.HeaderTextView);
            tv_header.setText(( item.get(position).getTitle()));
        } else {
            convertView = inflater.inflate(R.layout.recommendation_deficiencygroup,parent,false);
            //title:
            TextView tv_itemList = (TextView)convertView.findViewById(R.id.ListTextView_deficiencyTitle);
            tv_itemList.setText((item.get(position).getTitle()).toUpperCase());
            //percentage:
            TextView tv_percentage =(TextView) convertView.findViewById(R.id.ListTextView_deficiencyPercentage);
            tv_percentage.setText("80%");
            //occurance:
            TextView occurance = (TextView) convertView.findViewById(R.id.ListTextView_deficiencyOccurance);
            StringBuilder StrOccurance = new StringBuilder();
            StrOccurance.append("! ");
            StrOccurance.append("! ");
            StrOccurance.append("_ ");
            StrOccurance.append("_ ");
            StrOccurance.append("_ ");
            StrOccurance.append(" ");
            StrOccurance.append("_ ");
            occurance.setText(StrOccurance);
        }
        return convertView;
    }
}