package com.recoverrelax.pt.riotxmppchat.Widget;

import android.content.Context;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FreeChampionRotation extends LinearLayout {

    private Context context;

    public FreeChampionRotation(Context context) {
        super(context);
        this.context = context;
        inflateLayout();
    }

    public FreeChampionRotation(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflateLayout();
    }

    public FreeChampionRotation(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        inflateLayout();
    }

    public void inflateLayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.free_champ_rotation_layout, this);
        ButterKnife.bind(this);
    }
}
