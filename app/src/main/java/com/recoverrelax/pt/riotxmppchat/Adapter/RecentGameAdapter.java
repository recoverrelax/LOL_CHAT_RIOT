package com.recoverrelax.pt.riotxmppchat.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppViewUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.KamehameUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.RecentGameWrapper;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.TeamInfo;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiRealmDataVersion;
import com.recoverrelax.pt.riotxmppchat.Widget.SquareImageViewWidth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecentGameAdapter extends RecyclerView.Adapter<RecentGameAdapter.ViewHolder> {

    private List<RecentGameWrapper> recentGameList;
    private final LayoutInflater inflater;
    private final Context context;

    @Inject
    RiotApiRealmDataVersion realmData;

    public RecentGameAdapter(Context context, List<RecentGameWrapper> recentGameList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.recentGameList = recentGameList;
        MainApplication.getInstance().getAppComponent().inject(this);
    }

    @Override
    public RecentGameAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recent_game_child_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecentGameAdapter.ViewHolder holder, final int position) {
        holder.game = recentGameList.get(position);

        Picasso.with(context)
                .load(holder.game.getMyChampionUrl())
                .into(holder.championImage);

        holder.setChampionImagesAndNames(holder.game.getTeam100(), holder.championTeam1, holder.summonerNameTeam1);
        holder.setChampionImagesAndNames(holder.game.getTeam200(), holder.championTeam2, holder.summonerNameTeam2);

        Picasso.with(context)
                .load(holder.game.getSummonerSpellUrl1())
                .into(holder.championSS1);


        Picasso.with(context)
                .load(holder.game.getSummonerSpellUrl2())
                .into(holder.championSS2);


        holder.kda.setText(
                KamehameUtils.transformKillDeathAssistIntoKda(
                        holder.game.getKill(),
                        holder.game.getDead(),
                        holder.game.getAssists()
                )
        );

        holder.gold.setText(
                KamehameUtils.transformGoldIntoKMode(
                        holder.game.getGold()
                )
        );

        holder.cs.setText(String.valueOf(holder.game.getCs()));


        for (int i = 0; i < holder.game.getItemList().size(); i++) {
            Picasso.with(context)
                    .load(holder.game.getItemList().get(i))
                    .into(holder.summonerItems.get(i));
        }
    }

    public void setItems(List<RecentGameWrapper> recentGamesList) {
        this.recentGameList.clear();
        this.recentGameList.addAll(recentGamesList);

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return recentGameList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.recentGameCardView)
        CardView recentGameCardView;

        @Bind(R.id.summonerSpellStatBlock1)
        LinearLayout summonerSpellStatBlock;

        ImageView championImage;
        ImageView championSS1;
        ImageView championSS2;

        @Bind(R.id.summonerSpellStatBlock2)
        LinearLayout summonerSpellStatBlock2;

        TextView kda;
        TextView gold;
        TextView cs;
        List<SquareImageViewWidth> summonerItems = new ArrayList<>();

        @Bind(R.id.team100ChampionInfo)
        LinearLayout team1ChampionInfo;

        List<ImageView> championTeam1 = new ArrayList<>();
        List<TextView> summonerNameTeam1 = new ArrayList<>();

        @Bind(R.id.team200ChampionInfo)
        LinearLayout team2ChampionInfo;

        List<ImageView> championTeam2 = new ArrayList<>();
        List<TextView> summonerNameTeam2 = new ArrayList<>();

        RecentGameWrapper game;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            /**
             * summonerSpellStatBlock
             */

            championImage = ButterKnife.findById(summonerSpellStatBlock, R.id.championImage);
            championSS1 = ButterKnife.findById(summonerSpellStatBlock, R.id.championSS1);
            championSS2 = ButterKnife.findById(summonerSpellStatBlock, R.id.championSS2);

            /**
             * summonerSpellStatBlock2
             */

            kda = ButterKnife.findById(summonerSpellStatBlock2, R.id.kda);
            gold = ButterKnife.findById(summonerSpellStatBlock2, R.id.gold);
            cs = ButterKnife.findById(summonerSpellStatBlock2, R.id.cs);


            AppViewUtils.initAndBindView(summonerItems,
                    summonerSpellStatBlock2,
                    R.id.summonerItem1, R.id.summonerItem2, R.id.summonerItem3, R.id.summonerItem4, R.id.summonerItem5, R.id.summonerItem6
            );


            /**
             * team2ChampionInfo
             */

            AppViewUtils.initAndBindView(championTeam1,
                    team1ChampionInfo,
                    R.id.championSummoner1, R.id.championSummoner2, R.id.championSummoner3, R.id.championSummoner4, R.id.championSummoner5, R.id.championSummoner6
            );

            AppViewUtils.initAndBindView(summonerNameTeam1,
                    team1ChampionInfo,
                    R.id.summonerName, R.id.summonerName2, R.id.summonerName3, R.id.summonerName4, R.id.summonerName5, R.id.summonerName6
            );

            /**
             * team2ChampionInfo
             */

            AppViewUtils.initAndBindView(championTeam2,
                    team2ChampionInfo,
                    R.id.championSummoner1, R.id.championSummoner2, R.id.championSummoner3, R.id.championSummoner4, R.id.championSummoner5, R.id.championSummoner6
            );

            AppViewUtils.initAndBindView(summonerNameTeam2,
                    team2ChampionInfo,
                    R.id.summonerName, R.id.summonerName2, R.id.summonerName3, R.id.summonerName4, R.id.summonerName5, R.id.summonerName6
            );
        }

        public void setChampionImagesAndNames(List<TeamInfo> teamUrlList3, List<ImageView> championTeam, List<TextView> summonerNameTeam) {
            if (teamUrlList3 == null || teamUrlList3.size() == 0)
                return;

            List<TeamInfo> teamUrlList = new ArrayList<>();
            teamUrlList.addAll(teamUrlList3);

            int dataSize = teamUrlList.size();
            int viewSize = championTeam.size();

            for (int i = 0; i < viewSize; i++) {
                ImageView target = championTeam.get(i);
                TextView target2 = summonerNameTeam.get(i);

                if (i < dataSize) {
                    Picasso.with(context)
                            .load(teamUrlList.get(i).getPlayerImage())
                            .into(target);

                    target2.setText(teamUrlList.get(i).getPlayerName());
                    target.setVisibility(View.VISIBLE);
                    target2.setVisibility(View.VISIBLE);
                } else {
                    target.setVisibility(View.GONE);
                    target2.setVisibility(View.GONE);
                }
            }
        }

    }
}
