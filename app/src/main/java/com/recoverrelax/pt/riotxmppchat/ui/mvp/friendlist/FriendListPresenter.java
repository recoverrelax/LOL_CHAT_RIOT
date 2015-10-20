package com.recoverrelax.pt.riotxmppchat.ui.mvp.friendlist;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppRosterImpl;

import org.jivesoftware.smack.packet.Presence;

public interface FriendListPresenter {

    void onResume();

    void onPause();

    void getFullFriendList(boolean showOffline, @RiotXmppRosterImpl.FriendListSortMode int sortMode);

    void getSearchFriendsList(String searchString);

    void getSingleFriend(Presence presence);

    void configRecyclerView();

    void configAdapter(RecyclerView recyclerView);

    boolean onOptionsItemSelected(MenuItem item);

    void setOptionsMenu(Menu menu);

    void configSwipeRefresh(SwipeRefreshLayout swipeRefreshLayout);

}
