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

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.KamehameUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.RecentGameWrapper;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiRealmDataVersion;
import com.recoverrelax.pt.riotxmppchat.Widget.ChampionImageBlock;
import com.recoverrelax.pt.riotxmppchat.Widget.SummonerSpellStatBlock;
import com.recoverrelax.pt.riotxmppchat.Widget.SummonerSpellStatBlock2;

import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pt.reco.myutil.MyContext;

public class RecentGameAdapter extends RecyclerView.Adapter<RecentGameAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final Context context;
    @Inject
    RiotApiRealmDataVersion realmData;
    private List<RecentGameWrapper> recentGameList;
    private Random random = new Random();

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

        String ramdomSkin = holder.game.getRamdomSkin(random);

        holder.statusWLColor.setBackgroundColor(MyContext.getColor(context, holder.game.isWin() ? R.color.win_color_t : R.color.loss_color));

        holder.gameMode.setText(holder.game.getGameType());
        holder.gameWhen.setText(holder.game.getGameWhen());
        holder.playerPosition.setText(holder.game.getPlayerPosition());

        Glide.get(context).setMemoryCategory(MemoryCategory.HIGH);

        Glide.with(context)
                .load(ramdomSkin)
                .into(holder.frameLayout);

        Glide.with(context)
                .load(holder.game.getMyChampionUrl())
                .into(holder.summonerSpellStatBlock.getChampionImageView());

        holder.team1ChampionInfo.setChampionImagesAndNames(
                holder.game.getTeam100()
        );

        holder.team2ChampionInfo.setChampionImagesAndNames(
                holder.game.getTeam200()
        );

        Glide.with(context)
                .load(holder.game.getSummonerSpellUrl1())
                .into(holder.summonerSpellStatBlock.getChampionSS1ImageView());


        Glide.with(context)
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
            Glide.with(context)
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

        final TextView cs;
        @Bind(R.id.recentGameCardView)
        CardView recentGameCardView;
        @Bind(R.id.statusWLColor)
        LinearLayout statusWLColor;
        @Bind(R.id.gameMode)
        TextView gameMode;
        @Bind(R.id.playerPosition)
        TextView playerPosition;
        @Bind(R.id.gameWhen)
        TextView gameWhen;
        @Bind(R.id.summonerSpellStatBlock1)
        SummonerSpellStatBlock summonerSpellStatBlock;
        @Bind(R.id.championBackgroundSkin)
        ImageView frameLayout;
        @Bind(R.id.summonerSpellStatBlock2)
        SummonerSpellStatBlock2 summonerSpellStatBlock2;
        @Bind(R.id.team100ChampionInfo)
        ChampionImageBlock team1ChampionInfo;
        @Bind(R.id.team200ChampionInfo)
        ChampionImageBlock team2ChampionInfo;
        RecentGameWrapper game;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            cs = summonerSpellStatBlock2.getCs();
        }

    }
}
