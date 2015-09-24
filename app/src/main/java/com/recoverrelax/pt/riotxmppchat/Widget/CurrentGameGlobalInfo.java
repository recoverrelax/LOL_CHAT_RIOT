package com.recoverrelax.pt.riotxmppchat.Widget;

import android.content.Context;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CurrentGameGlobalInfo extends PercentRelativeLayout{

     @Bind(R.id.mapName)
     TextView mapName;

    @Bind(R.id.gameQueueType)
    TextView gameQueueType;

    @Bind(R.id.gameMode)
    TextView gameMode;

    @Bind(R.id.gameDuration)
    TextView gameDuration;

    @Bind(R.id.gameDurationLabel)
    TextView gameDurationLabel;

    private Context context;

    public CurrentGameGlobalInfo(Context context) {
        super(context);
        this.context = context;
        inflateLayout();
    }

    public CurrentGameGlobalInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflateLayout();
    }

    public CurrentGameGlobalInfo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        inflateLayout();
    }

    public void inflateLayout(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.current_game_global_info_layout, this);
        ButterKnife.bind(this);
    }

    public void setMapName(String mapName) {
        this.mapName.setText(mapName);
    }

    public void setGameQueueType(String gameQueueType) {
        this.gameQueueType.setText(gameQueueType);
    }

    public void setGameMode(String gameMode) {
        this.gameMode.setText(gameMode);
    }

    public void setGameDuration(String gameDuration) {
        this.gameDuration.setText(gameDuration);
    }

    public void setGameDurationLabel(String gameDurationLabel) {
        this.gameDurationLabel.setText(gameDurationLabel);
    }
}
