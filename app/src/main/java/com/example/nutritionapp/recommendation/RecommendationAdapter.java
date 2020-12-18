package com.example.nutritionapp.recommendation;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nutritionapp.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;

class RecommendationAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<RecommendationListItem> items;
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
        //item.pieEntryList.clear();

        /* sub views */
        TextView rec_item = convertView.findViewById(R.id.nutritionName);
        PieChart rec_chart = convertView.findViewById(R.id.pieChar);

        /*Text-column*/
        rec_item.setText(item.nutritionElement.toString());

        /*chart*/
        PieDataSet pieDataSet = item.pieDataSet;
        //Log.wtf("ZZZ--"+item.tag,((Float)item.percentage).toString());

        //STYLING:
        rec_chart.setUsePercentValues(true);

        // set chartColors - AmpelTheme:
        if (item.percentage< 40){
            item.pieDataSet.setColors(context.getResources().getIntArray(R.array.DayChartRed));
        }else if (item.percentage < 80) {
            item.pieDataSet.setColors(context.getResources().getIntArray(R.array.DayChartOrange));
        }else {
            item.pieDataSet.setColors(context.getResources().getIntArray(R.array.DayChartGreen));
        }
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
        rec_chart.getDescription().setPosition(225f, 25f);

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
