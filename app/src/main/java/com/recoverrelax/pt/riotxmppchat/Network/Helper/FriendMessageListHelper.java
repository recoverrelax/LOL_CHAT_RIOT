package com.recoverrelax.pt.riotxmppchat.Network.Helper;

public interface FriendMessageListHelper {
    void getPersonalMessageList(String connectedUser);
    void getPersonalMessageSingleItem(String connectedUser, String userToReturn);
}
