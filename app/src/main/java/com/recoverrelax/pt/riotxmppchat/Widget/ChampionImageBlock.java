package com.recoverrelax.pt.riotxmppchat.Widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.TeamInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChampionImageBlock extends LinearLayout {

    @Bind({R.id.championSummoner1, R.id.championSummoner2, R.id.championSummoner3, R.id.championSummoner4, R.id.championSummoner5, R.id.championSummoner6})
    List<ImageView> championTeam;

    @Bind({R.id.summonerName, R.id.summonerName2, R.id.summonerName3, R.id.summonerName4, R.id.summonerName5, R.id.summonerName6})
    List<TextView> summonerNameTeam;

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

    public void inflateLayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.champion_image_block_layout, this);
        ButterKnife.bind(this);
    }

    public void setChampionImagesAndNames(List<TeamInfo> teamUrlList3) {

        List<TeamInfo> teamUrlList = new ArrayList<>();
        teamUrlList.addAll(teamUrlList3);

        if(teamUrlList.size() == 0)
            return;

        int dataSize = teamUrlList.size();
        int viewSize = championTeam.size();

        for (int i = 0; i < viewSize; i++) {
            final ImageView target = this.championTeam.get(i);
            final TextView target2 = this.summonerNameTeam.get(i);

            if(i < dataSize) {
                Picasso.with(context)
                        .load(teamUrlList.get(i).getPlayerImage())
                        .into(target);
                target2.setText(teamUrlList.get(i).getPlayerName());

                target.setVisibility(View.VISIBLE);
                target2.setVisibility(View.VISIBLE);
            }else
                target.setVisibility(View.INVISIBLE);
                target2.setVisibility(View.INVISIBLE);
        }
    }
}
