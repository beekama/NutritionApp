package com.example.nutritionapp.recommendation.nutritionElement;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.highlight.CombinedHighlighter;
import com.github.mikephil.charting.renderer.CombinedChartRenderer;

import java.util.List;

public class ExtendedBarChart extends CombinedChart {

    protected Paint zonePaint;
    float[] pts = new float[2];

    public ExtendedBarChart(Context context) {
        super(context);
    }

    public ExtendedBarChart(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    public ExtendedBarChart(Context context, AttributeSet attributeSet, int defStyle){
        super(context, attributeSet, defStyle);
    }

    @Override
    protected void init(){
        super.init();

        zonePaint = new Paint();
        zonePaint.setStyle(Paint.Style.FILL);
        zonePaint.setColor(Color.parseColor("#A9A9A9"));

        mDrawOrder = new DrawOrder[]{
                DrawOrder.BAR, DrawOrder.BUBBLE, DrawOrder.LINE, DrawOrder.CANDLE, DrawOrder.SCATTER
        };

        setHighlighter(new CombinedHighlighter(this, this));
        setHighlightFullBarEnabled(true);
        mRenderer = new CombinedChartRenderer(this, mAnimator, mViewPortHandler);
    }

    @Override
    protected void  onDraw(Canvas canvas){

        /* get limit lines */
        List<LimitLine> limitLineList = mAxisLeft.getLimitLines();
        if (limitLineList == null || limitLineList.size() != 2){
            super.onDraw(canvas);
            return;
        }

        /* get limits from limit lines */
        pts[0] = limitLineList.get(0).getLimit();
        pts[1] = limitLineList.get(0).getLimit();

        /* transforms limits into pixels */
        mLeftAxisTransformer.pointValuesToPixel(pts);

        /* draw rectangle with transformed pixels */
        canvas.drawRect(mViewPortHandler.contentLeft(), pts[0], mViewPortHandler.contentRight(), pts[1], zonePaint);

        /* draw rest of canvas */
        drawDescription(canvas);
        drawMarkers(canvas);
        super.onDraw(canvas);
    }


}
