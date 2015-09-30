package com.recoverrelax.pt.riotxmppchat.Widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.recoverrelax.pt.riotxmppchat.R;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SummonerSpellStatBlock extends LinearLayout {

    @Bind(R.id.championImage)
    ImageView championImage;

    @Bind(R.id.championSS1)
    ImageView championSS1;

    @Bind(R.id.championSS2)
    ImageView championSS2;

    private Context context;

    public SummonerSpellStatBlock(Context context) {
        super(context);
        this.context = context;
        inflateLayout();
    }

    public SummonerSpellStatBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflateLayout();
    }

    public SummonerSpellStatBlock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        inflateLayout();
    }

    public void inflateLayout(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.summoner_spell_stat_block_layout, this);
        ButterKnife.bind(this);
    }

    public ImageView getChampionImageView() {
        return championImage;
    }

    public ImageView getChampionSS1ImageView() {
        return championSS1;
    }

    public ImageView getChampionSS2ImageView() {
        return championSS2;
    }
}
