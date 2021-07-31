package com.example.nutritionapp.recommendation;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.NutritionElement;
import com.example.nutritionapp.recommendation.nutritionElement.RecommendationNutritionAdapter;
import com.example.nutritionapp.recommendation.nutritionElement.RecommendationsElement;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieEntry;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.*;

public class RecommendationProteinListAdapter extends RecyclerView.Adapter {

    private int[] colors;
    private List<PieEntry> pieEntries;
    private List<Integer> allowances;
    private Context context;

    private static final int HEADER_TYPE = 0;
    private static final int ITEM_TYPE = 1;


    public RecommendationProteinListAdapter(Context context, PieData data, List<Integer> allowances) {
        this.context = context;
        this.colors = data.getColors();
        this.pieEntries = data.getDataSet().getEntriesForXValue(0);
        this.allowances = allowances;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view;
        if (viewType == HEADER_TYPE){
            view = inflater.inflate(R.layout.recommendation_protein_chart_listheader, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == ITEM_TYPE){
            view = inflater.inflate(R.layout.recommendation_protein_chart_listitem, parent, false);
            return new LocalViewHolder(view);
        } else {
            throw new RuntimeException("item matches no viewType. No implementation for type : " + viewType);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder){
            HeaderViewHolder hvh = (HeaderViewHolder) holder;
            hvh.currentVal.setText("current");
            hvh.targetVal.setText("target");
        } else if (holder instanceof LocalViewHolder){
            LocalViewHolder lvh = (LocalViewHolder) holder;
            int relPosition = position - 1;
            lvh.currentVal.setText(String.format("%.0f", pieEntries.get(relPosition).getValue()));
            lvh.targetVal.setText(Integer.toString(allowances.get(relPosition)));
            lvh.label.setText(pieEntries.get(relPosition).getLabel());
            lvh.color.setColorFilter(colors[relPosition]);

    }}

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return HEADER_TYPE;
        return ITEM_TYPE;
    }
        @Override
        public int getItemCount() {
            // listItems + header
            return pieEntries.size() + 1;
        }

    static class LocalViewHolder extends RecyclerView.ViewHolder{
        final ImageView color;
        final TextView label;
        final TextView currentVal;
        final TextView targetVal;

        LocalViewHolder(View itemView) {
            super(itemView);
            color = itemView.findViewById(R.id.labelColor);
            label = itemView.findViewById(R.id.label);
            currentVal = itemView.findViewById(R.id.currentVal);
            targetVal = itemView.findViewById(R.id.targetVal);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder{
        final TextView currentVal;
        final TextView targetVal;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            currentVal = itemView.findViewById(R.id.currentVal);
            targetVal = itemView.findViewById(R.id.targetVal);
        }
    }
}



