package com.recoverrelax.pt.riotxmppchat.EventHandling.MessageList;

import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;

import java.util.List;

public class OnMessageListListReceivedEvent {

    private List<FriendListChat> friendListChats;

    public OnMessageListListReceivedEvent(List<FriendListChat> friendListChats) {
        this.friendListChats = friendListChats;
    }

    public List<FriendListChat> getFriendListChats() {
        return friendListChats;
    }
}
