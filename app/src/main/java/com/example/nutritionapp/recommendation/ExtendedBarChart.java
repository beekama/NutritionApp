package com.example.nutritionapp.recommendation;

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
       // mGridBackgroundPaint.setColor(Color.GREEN);

        // Default values are not ready here yet
        mDrawOrder = new DrawOrder[]{
                DrawOrder.BAR, DrawOrder.BUBBLE, DrawOrder.LINE, DrawOrder.CANDLE, DrawOrder.SCATTER
        };

        setHighlighter(new CombinedHighlighter(this, this));

        // Old default behaviour
        setHighlightFullBarEnabled(true);

        mRenderer = new CombinedChartRenderer(this, mAnimator, mViewPortHandler);
    }

    @Override
    protected void  onDraw(Canvas canvas){
        List<LimitLine> limitLineList = mAxisLeft.getLimitLines();

        if (limitLineList == null || limitLineList.size() != 2){
            super.onDraw(canvas);
            return;
        }

        LimitLine ll1 = limitLineList.get(0);
        LimitLine ll2 = limitLineList.get(1);

        float pts[] = new float[4];
        pts[1] = ll1.getLimit();
        pts[3] = ll2.getLimit();

        mLeftAxisTransformer.pointValuesToPixel(pts);
        canvas.drawRect(mViewPortHandler.contentLeft(), pts[1], mViewPortHandler.contentRight(), pts[3], zonePaint);


        drawDescription(canvas);

        drawMarkers(canvas);


        super.onDraw(canvas);
    }


}
