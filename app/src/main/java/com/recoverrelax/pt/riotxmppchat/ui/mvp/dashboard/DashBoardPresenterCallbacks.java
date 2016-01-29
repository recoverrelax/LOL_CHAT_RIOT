//package com.recoverrelax.pt.riotxmppchat.ui.mvp.dashboard;
//
//import android.graphics.drawable.Drawable;
//
//import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMVPHelper;
//import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.ChampionInfo;
//
//import java.util.List;
//
//public interface DashBoardPresenterCallbacks extends AppMVPHelper.RecyclerViewPresenterCallbacks<DashBoardAdapter> {
//    void onUnreadedMessagesReady(String um);
//
//    void onUnreadedMessagesFailed(String um);
//
//    void onFriendStatusInfoReady(String online, String offline, String playing);
//
//    void onFriendStatusInfoFailed(String online, String offline, String playing);
//
//    void onFreeChampionRotationReady(List<ChampionInfo> championInfos, int size);
//
//    void onFreeChampionRotationFailed();
//
//    void onFreeChampionRotationLoading(boolean state);
//
//    void setMessageIconDrawable(Drawable drawable);
//
//    int getNrChampionsNeeded();
//}
