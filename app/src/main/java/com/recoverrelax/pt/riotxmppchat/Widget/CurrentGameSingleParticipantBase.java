package com.recoverrelax.pt.riotxmppchat.Widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class CurrentGameSingleParticipantBase extends PercentRelativeLayout {

    @Bind(R.id.championPlaying)
    ImageView championPlaying;

    @Bind(R.id.playerName)
    TextView playerName;

    @Bind(R.id.summonerSpell1)
    ImageView summonerSpell1;

    @Bind(R.id.summonerSpell2)
    ImageView summonerSpell2;

    private Context context;

    public CurrentGameSingleParticipantBase(Context context) {
        super(context);
        this.context = context;
        inflateLayout();
    }

    public CurrentGameSingleParticipantBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflateLayout();
    }

    public CurrentGameSingleParticipantBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        inflateLayout();
    }

    public void inflateLayout(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(getLayout(), this);
        ButterKnife.bind(this);
    }

    public abstract int getLayout();

    public ImageView getChampionPlaying() {
        return championPlaying;
    }

    public TextView getPlayerName() {
        return playerName;
    }

    public ImageView getSummonerSpell1() {
        return summonerSpell1;
    }

    public ImageView getSummonerSpell2() {
        return summonerSpell2;
    }

    public void setChampionPlayingDrawable(Drawable drawable){
        this.championPlaying.setImageDrawable(drawable);
    }

    public void setSpell1Drawable(Drawable drawable){
        this.summonerSpell1.setImageDrawable(drawable);
    }

    public void setSpell2Drawable(Drawable drawable){
        this.summonerSpell1.setImageDrawable(drawable);
    }

    public void setSummonerName(String text){
        this.playerName.setText(text);
    }
}
