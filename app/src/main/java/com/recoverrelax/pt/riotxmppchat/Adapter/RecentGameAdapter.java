package com.recoverrelax.pt.riotxmppchat.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.KamehameUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Game.GameDto;
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

    private List<GameDto> recentGameList;
    private final LayoutInflater inflater;
    private final Context context;

    @Inject
    RiotApiRealmDataVersion realmData;

    public RecentGameAdapter(Context context, List<GameDto> recentGameList) {
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
//        holder.resetVisibility();

        Picasso.with(context)
                .load(holder.game.getChampionImage())
                .into(holder.summonerSpellStatBlock1.getChampionImageView());

        for (int i = 0; i < holder.game.getFellowPlayersTeam100().size(); i++) {
            Picasso.with(context)
                    .load(holder.game.getFellowPlayersTeam100().get(i).getChampionImage())
                    .into(holder.team1ChampionInfo.getChampionTeam().get(i));
        }

        for (int i = 0; i < holder.game.getFellowPlayersTeam200().size(); i++) {
            Picasso.with(context)
                    .load(holder.game.getFellowPlayersTeam200().get(i).getChampionImage())
                    .into(holder.team2ChampionInfo.getChampionTeam().get(i));
        }


        Picasso.with(context)
                .load(holder.game.getSpell1Image())
                .into(holder.summonerSpellStatBlock1.getChampionSS1ImageView());


        Picasso.with(context)
                .load(holder.game.getSpell2Image())
                .into(holder.summonerSpellStatBlock1.getChampionSS2ImageView());


        holder.summonerSpellStatBlock2.getKda().setText(
                KamehameUtils.transformKillDeathAssistIntoKda(
                        holder.game.getStats().getChampionsKilled(),
                        holder.game.getStats().getNumDeaths(),
                        holder.game.getStats().getAssists()
                )
        );

        holder.summonerSpellStatBlock2.getGold().setText(
                KamehameUtils.transformGoldIntoKMode(
                        holder.game.getStats().getGoldEarned()
                )
        );

        holder.cs.setText(String.valueOf(holder.game.getStats().getMinionsKilled()));


        for (int i = 0; i < holder.game.getStats().getItemsImage().size(); i++) {
            Picasso.with(context)
                    .load(holder.game.getStats().getItemsImage().get(i))
                    .into(holder.summonerSpellStatBlock2.getSummonerItems().get(i));
        }
    }

    public void setItems(List<GameDto> recentGamesList) {
        this.recentGameList.clear();
        this.recentGameList.addAll(recentGamesList);
        notifyDataSetChanged();
        Log.i("123", recentGamesList.size() + "");
    }

    @Override
    public int getItemCount() {
        return recentGameList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.recentGameCardView)
        CardView recentGameCardView;

        @Bind(R.id.summonerSpellStatBlock1)
        SummonerSpellStatBlock summonerSpellStatBlock1;

        @Bind(R.id.summonerSpellStatBlock2)
        SummonerSpellStatBlock2 summonerSpellStatBlock2;

        @Bind(R.id.team100ChampionInfo)
        ChampionImageBlock team1ChampionInfo;

        @Bind(R.id.team200ChampionInfo)
        ChampionImageBlock team2ChampionInfo;

        GameDto game;

        final TextView cs;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            cs = summonerSpellStatBlock2.getCs();
        }

        public void resetVisibility() {
            team1ChampionInfo.resetVisibility();
            team2ChampionInfo.resetVisibility();
        }
    }
}
