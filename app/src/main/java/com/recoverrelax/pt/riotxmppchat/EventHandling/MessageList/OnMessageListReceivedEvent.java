package com.recoverrelax.pt.riotxmppchat.EventHandling.MessageList;

import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;

import java.util.List;

public class OnMessageListReceivedEvent {

    private FriendListChat friendList;

    public OnMessageListReceivedEvent(FriendListChat friendListC) {
        this.friendList = friendList;
    }

    public FriendListChat getFriendList() {
        return friendList;
    }
}
