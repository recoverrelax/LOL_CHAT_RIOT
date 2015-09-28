package com.recoverrelax.pt.riotxmppchat.Widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.recoverrelax.pt.riotxmppchat.R;

import butterknife.ButterKnife;

public class ChampionImageBlock extends LinearLayout {

    private Context context;

    public ChampionImageBlock(Context context) {
        super(context);
        this.context = context;
        inflateLayout();
    }

    public ChampionImageBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflateLayout();
    }

    public ChampionImageBlock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        inflateLayout();
    }

    public void inflateLayout(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.champion_image_block_layout, this);
        ButterKnife.bind(this);
    }
}
