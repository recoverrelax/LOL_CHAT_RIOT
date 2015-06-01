package com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList;

import com.recoverrelax.pt.riotxmppchat.Network.Helper.RiotXmppRosterImpl;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;

import java.util.ArrayList;

public class OnFriendListLoadedEvent {

    private RiotXmppRosterImpl.FriendList friendList;

    public OnFriendListLoadedEvent(RiotXmppRosterImpl.FriendList friendList) {
        this.friendList = friendList;
    }

    public ArrayList<Friend> getFriendList() {
        return friendList.getFriendList();
    }
}
