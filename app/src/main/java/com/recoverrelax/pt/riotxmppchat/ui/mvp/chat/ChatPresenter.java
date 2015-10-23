package com.recoverrelax.pt.riotxmppchat.ui.mvp.chat;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMVPHelper;

public interface ChatPresenter extends AppMVPHelper.RecyclerViewPresenter {
    void getLastXPersonalMessageList(String friendXmppName);

    void sendMessage(String message, String friendXmppName);
}
