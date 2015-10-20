package com.recoverrelax.pt.riotxmppchat.ui.mvp.friendlist;

import android.support.v7.widget.RecyclerView;

public interface FriendListPresenterCallbacks {

    void onFriendListReady(int onlineFriendCount);

    void onFriendListFailed(Throwable e);

    void onFriendListCompleted();

    void onSearchFriendListReady(int onlineFriendCount);

    void onSearchFriendListFailed(Throwable e);

    void onSearchFriendListCompleted();

    void onSingleFriendReady(int onlineFriendCount);

    void onSingleFriendFailed(Throwable e);

    void onSingleFriendCompleted();

    void setRecyclerViewLayoutParams(RecyclerView.LayoutManager layoutManager);

    void setRecyclerViewAdapter(FriendsListAdapter adapter);
}
