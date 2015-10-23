package com.recoverrelax.pt.riotxmppchat.ui.mvp.friendlist;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMVPHelper;

public interface FriendListPresenterCallbacks extends AppMVPHelper.RecyclerViewPresenterCallbacks<FriendsListAdapter> {

    void onFriendListReady(int onlineFriendCount);

    void onFriendListFailed(Throwable e);

    void onFriendListCompleted();

    void onSearchFriendListReady(int onlineFriendCount);

    void onSearchFriendListFailed(Throwable e);

    void onSearchFriendListCompleted();

    void onSingleFriendReady(int onlineFriendCount);

    void onSingleFriendFailed(Throwable e);

    void onSingleFriendCompleted();
}
