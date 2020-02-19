package com.mindlinksw.schoolmeals.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by N16326 on 2018. 9. 19..
 */

public class SquareLayout extends RelativeLayout {

    public SquareLayout(Context context) {
        super(context);
    }

    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

        // 가로길이 기준으로 정사각형
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        // Optimization so we don't measure twice unless we need to
        if (width != height) {
            setMeasuredDimension(width, width);
        }
    }

}