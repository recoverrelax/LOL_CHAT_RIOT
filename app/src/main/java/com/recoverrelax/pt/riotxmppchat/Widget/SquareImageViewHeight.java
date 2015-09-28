package com.recoverrelax.pt.riotxmppchat.Widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SquareImageViewHeight extends ImageView{
    public SquareImageViewHeight(Context context) {
        super(context);
    }

    public SquareImageViewHeight(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageViewHeight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SquareImageViewHeight(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int squareSize = getMeasuredHeight();
        setMeasuredDimension(squareSize, squareSize);
    }
}
