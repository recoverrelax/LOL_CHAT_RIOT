package com.recoverrelax.pt.riotxmppchat.ui.mvp.chat;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMVPHelper;

public interface ChatPresenterCallbacks extends AppMVPHelper.RecyclerViewPresenterCallbacks<ChatAdapter> {

    boolean isListRefreshing();

    void setListRefreshing(boolean state);

    void writeToChat(String s);

    void clearChatText();

    String getChatText();

    String getFriendXmppName();

    String getFriendUsername();

    int getMessageSize();

    void sendFriendOfflineSnack();
}
