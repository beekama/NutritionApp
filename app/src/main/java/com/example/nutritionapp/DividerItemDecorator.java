package com.example.nutritionapp;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DividerItemDecorator extends RecyclerView.ItemDecoration {
    private final Drawable divider;
    private final Boolean header;

    public DividerItemDecorator(Drawable divider, Boolean header){
        this.divider = divider;
        this.header = header;
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, RecyclerView parent, @NonNull RecyclerView.State state) {
        int dividerLeft = parent.getPaddingLeft() + 25;
        int dividerRight = parent.getWidth() - parent.getPaddingRight() - 25;

        int childCount = parent.getChildCount();
        int start = header? 1 : 0;
        for (int i = start; i <= childCount - 2; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int dividerTop = child.getBottom() + params.bottomMargin;
            int dividerBottom = dividerTop + divider.getIntrinsicHeight();

            divider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
            divider.draw(canvas);
        }
    }
}
