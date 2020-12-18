package com.example.nutritionapp.recommendation;

import android.content.Context;
import android.widget.TextView;

import com.example.nutritionapp.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.time.LocalDate;

/* necessary to show marker_view (with text) on click - according to Philipp Jahoda example */
public class CustomMarkerView extends MarkerView {

    private final TextView tv;
    final LocalDate oneWeekAgo = LocalDate.now().minusWeeks(1);

    //set super constructor and select textView

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tv = findViewById(R.id.tv_marker_view);

    }


    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {


        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tv.setText(Utils.formatNumber(ce.getHigh(), 0, true));
            //tv.setText(Utils.formatNumber(ce.get));
        } else {

            String printDate = oneWeekAgo.plusDays((long) e.getX()).compareTo(LocalDate.now())==0? "today" : oneWeekAgo.plusDays((long) e.getX()).toString();
            tv.setText(printDate + "\nNutrition value: " + Utils.formatNumber(e.getY(),2,true));
            //tv.setText(Utils.formatNumber(e.getX(),0,true));
        }

        super.refreshContent(e, highlight);
    }


    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }

}
