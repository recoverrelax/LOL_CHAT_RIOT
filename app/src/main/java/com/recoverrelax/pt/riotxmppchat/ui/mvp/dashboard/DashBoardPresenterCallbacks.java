package com.recoverrelax.pt.riotxmppchat.ui.mvp.dashboard;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;

import com.recoverrelax.pt.riotxmppchat.Adapter.DashBoardLogAdapter;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.ChampionInfo;

import java.util.List;

public interface DashBoardPresenterCallbacks {
    void onUnreadedMessagesReady(String um);
    void onUnreadedMessagesFailed(String um);

    void onFriendStatusInfoReady(String online, String offline, String playing);
    void onFriendStatusInfoFailed(String online, String offline, String playing);

    void onFreeChampionRotationReady(List<ChampionInfo> championInfos, int size);
    void onFreeChampionRotationFailed();
    void onFreeChampionRotationLoading(boolean state);

    void setMessageIconDrawable(Drawable drawable);

    void setRecyclerViewLayoutParams(RecyclerView.LayoutManager layoutManager);

    void setRecyclerViewAdapter(DashBoardLogAdapter adapter);
}
