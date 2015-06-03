package com.recoverrelax.pt.riotxmppchat.EventHandling.MessageList;

import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;

public class OnMessageSingleItemReceivedEvent {

    private FriendListChat friendList;

    public OnMessageSingleItemReceivedEvent(FriendListChat friendListC) {
        this.friendList = friendListC;
    }

    public FriendListChat getFriendList() {
        return friendList;
    }
}
