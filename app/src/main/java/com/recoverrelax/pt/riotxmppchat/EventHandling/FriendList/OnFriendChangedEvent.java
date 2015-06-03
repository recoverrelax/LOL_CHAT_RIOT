package com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList;

import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;

public class OnFriendChangedEvent {
    private Friend friend;

    public OnFriendChangedEvent(Friend friend) {
        this.friend = friend;
    }

    public Friend getFriend() {
        return friend;
    }
}
