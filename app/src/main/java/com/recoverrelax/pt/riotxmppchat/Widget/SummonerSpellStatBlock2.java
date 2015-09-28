package com.recoverrelax.pt.riotxmppchat.Widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.recoverrelax.pt.riotxmppchat.R;

import butterknife.ButterKnife;

public class SummonerSpellStatBlock2 extends LinearLayout {

    private Context context;

    public SummonerSpellStatBlock2(Context context) {
        super(context);
        this.context = context;
        inflateLayout();
    }

    public SummonerSpellStatBlock2(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflateLayout();
    }

    public SummonerSpellStatBlock2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        inflateLayout();
    }

    public void inflateLayout(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.summoner_spell_stat_block_layout2, this);
        ButterKnife.bind(this);
    }
}
