package com.recoverrelax.pt.riotxmppchat.Riot.Model;

import java.util.Date;

import LolChatRiotDb.MessageDb;

public class FriendListChat {

    private Friend friend;
    private MessageDb lastMessage;
    private static final String EMPTY_MESSAGE = "";

    public FriendListChat(Friend friend, MessageDb lastMessage) {
        this.friend = friend;
        this.lastMessage = lastMessage;

    }

    public String getFriendName(){
        return this.friend.getName();
    }

    public String getFriendLastMessage(){
        return this.lastMessage == null
                ? EMPTY_MESSAGE
                : this.lastMessage.getMessage_message();
    }

    public Date getFriendLastMessageDate(){
        return this.lastMessage == null
                ? null
                : this.lastMessage.getMessage_date();
    }

    public String getFriendLastMessageDateAsString(){
        Date friendLastMessageDate = getFriendLastMessageDate();

        return friendLastMessageDate == null
                ? EMPTY_MESSAGE
                : friendLastMessageDate.toString();
    }


    public Friend getFriend() {
        return friend;
    }

    public MessageDb getLastMessage() {
        return lastMessage;
    }

}
