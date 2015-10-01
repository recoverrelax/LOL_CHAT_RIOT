package com.recoverrelax.pt.riotxmppchat.Widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SummonerSpellStatBlock2 extends LinearLayout {

    @Bind(R.id.kda)
    TextView kda;

    @Bind(R.id.gold)
    TextView gold;

    @Bind(R.id.cs)
    TextView cs;

    @Bind({R.id.summonerItem1, R.id.summonerItem2, R.id.summonerItem3, R.id.summonerItem4,
           R.id.summonerItem5, R.id.summonerItem6})
    List<ImageView> summonerItems;

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

    public TextView getKda() {
        return kda;
    }

    public TextView getGold() {
        return gold;
    }

    public TextView getCs() {
        return cs;
    }

    public List<ImageView> getSummonerItems() {
        return summonerItems;
    }
}
