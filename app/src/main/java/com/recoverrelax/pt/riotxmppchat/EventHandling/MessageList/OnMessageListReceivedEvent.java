package com.recoverrelax.pt.riotxmppchat.EventHandling.MessageList;

import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;

import java.util.List;

public class OnMessageListReceivedEvent {

    private List<FriendListChat> friendListChats;

    public OnMessageListReceivedEvent(List<FriendListChat> friendListChats) {
        this.friendListChats = friendListChats;
    }

    public List<FriendListChat> getFriendListChats() {
        return friendListChats;
    }
}
