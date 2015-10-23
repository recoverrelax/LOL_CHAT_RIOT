package com.recoverrelax.pt.riotxmppchat.ui.mvp.friendlist;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMVPHelper;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppRosterImpl;

import org.jivesoftware.smack.packet.Presence;

public interface FriendListPresenter extends AppMVPHelper.RecyclerViewPresenter {

    void getFullFriendList(boolean showOffline, @RiotXmppRosterImpl.FriendListSortMode int sortMode);

    void getSearchFriendsList(String searchString);

    void getSingleFriend(Presence presence);

    boolean onOptionsItemSelected(MenuItem item);

    void setOptionsMenu(Menu menu);

    void configSwipeRefresh(SwipeRefreshLayout swipeRefreshLayout);

}
