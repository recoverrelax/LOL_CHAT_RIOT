package com.recoverrelax.pt.riotxmppchat.Widget;

import android.content.Context;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CurrentGameBanList extends PercentRelativeLayout {

    @Bind(R.id.yourTeam)
    TextView yourTeam;

    @Bind(R.id.enemyTeam)
    TextView enemyTeam;

    @Bind({R.id.ban1, R.id.ban2, R.id.ban3})
    List<ImageView> team100Bans;

    @Bind({R.id.ban4, R.id.ban5, R.id.ban6})
    List<ImageView> team200Bans;

    private Context context;

    public CurrentGameBanList(Context context) {
        super(context);
        this.context = context;
        inflateLayout();
    }

    public CurrentGameBanList(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflateLayout();
    }

    public CurrentGameBanList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        inflateLayout();
    }

    public void inflateLayout(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.current_game_ban_list_layout, this);
        ButterKnife.bind(this);
    }

    public void setLeftTeamText(String text){
        yourTeam.setText(text);
    }

    public void setRightTeamText(String text){
        enemyTeam.setText(text);
    }

    public List<ImageView> getTeam100Bans(){
        return this.team100Bans;
    }

    public List<ImageView> getTeam200Bans(){
        return this.team200Bans;
    }

    public int getSize(){
        return this.team100Bans.size() + this.team200Bans.size();
    }
}
