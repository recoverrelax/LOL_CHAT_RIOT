package com.recoverrelax.pt.riotxmppchat.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.KamehameUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.RecentGameWrapper;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiRealmDataVersion;
import com.recoverrelax.pt.riotxmppchat.Widget.ChampionImageBlock;
import com.recoverrelax.pt.riotxmppchat.Widget.SummonerSpellStatBlock;
import com.recoverrelax.pt.riotxmppchat.Widget.SummonerSpellStatBlock2;
import com.squareup.picasso.Picasso;

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
                .into(holder.summonerSpellStatBlock.getChampionImageView());

        holder.team1ChampionInfo.setChampionImagesAndNames(
                holder.game.getTeam100()
        );

        holder.team2ChampionInfo.setChampionImagesAndNames(
                holder.game.getTeam200()
        );

        Picasso.with(context)
                .load(holder.game.getSummonerSpellUrl1())
                .into(holder.summonerSpellStatBlock.getChampionSS1ImageView());


        Picasso.with(context)
                .load(holder.game.getSummonerSpellUrl2())
                .into(holder.summonerSpellStatBlock.getChampionSS2ImageView());


        holder.summonerSpellStatBlock2.getKda().setText(
                KamehameUtils.transformKillDeathAssistIntoKda(
                        holder.game.getKill(),
                        holder.game.getDead(),
                        holder.game.getAssists()
                )
        );

        holder.summonerSpellStatBlock2.getGold().setText(
                KamehameUtils.transformGoldIntoKMode(
                        holder.game.getGold()
                )
        );

        holder.cs.setText(String.valueOf(holder.game.getCs()));


        for (int i = 0; i < holder.game.getItemList().size(); i++) {
            Picasso.with(context)
                    .load(holder.game.getItemList().get(i))
                    .into(holder.summonerSpellStatBlock2.getSummonerItems().get(i));
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
        SummonerSpellStatBlock summonerSpellStatBlock;

        @Bind(R.id.summonerSpellStatBlock2)
        SummonerSpellStatBlock2 summonerSpellStatBlock2;

        @Bind(R.id.team100ChampionInfo)
        ChampionImageBlock team1ChampionInfo;

        @Bind(R.id.team200ChampionInfo)
        ChampionImageBlock team2ChampionInfo;

        RecentGameWrapper game;

        final TextView cs;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            cs = summonerSpellStatBlock2.getCs();
        }

    }
}
