package com.example.nutritionapp.other;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

public class NestableList extends ListView {

    /* Prof. Dr. Stackoverflow https://stackoverflow.com/questions/18813296/non-scrollable-listview-inside-scrollview */

    public NestableList(Context context) {
        super(context);
    }
    public NestableList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public NestableList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}