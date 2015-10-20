package com.recoverrelax.pt.riotxmppchat.Widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SquareImageViewWidth extends ImageView {
    public SquareImageViewWidth(Context context) {
        super(context);
    }

    public SquareImageViewWidth(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageViewWidth(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SquareImageViewWidth(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int squareSize = getMeasuredWidth();
        setMeasuredDimension(squareSize, squareSize);
    }
}
