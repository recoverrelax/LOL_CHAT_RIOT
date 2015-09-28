package com.recoverrelax.pt.riotxmppchat.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.recoverrelax.pt.riotxmppchat.R;

import java.util.List;

import LolChatRiotDb.InAppLogDb;
import butterknife.Bind;
import butterknife.ButterKnife;

public class RecentGameAdapter extends RecyclerView.Adapter<RecentGameAdapter.ViewHolder> {

    private List<InAppLogDb> logList;
    private final LayoutInflater inflater;
    private final Context context;

    public RecentGameAdapter(Context context, List<InAppLogDb> logList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.logList = logList;
    }

    @Override
    public RecentGameAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recent_game_child_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecentGameAdapter.ViewHolder holder, int position) {

    }

    public void setItems(List<InAppLogDb> items) {
        logList = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.recentGameCardView)
        CardView recentGameCardView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
