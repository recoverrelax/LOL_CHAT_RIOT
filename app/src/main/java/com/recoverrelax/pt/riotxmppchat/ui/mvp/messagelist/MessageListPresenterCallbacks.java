package com.recoverrelax.pt.riotxmppchat.ui.mvp.messagelist;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMVPHelper;

public interface MessageListPresenterCallbacks extends AppMVPHelper.RecyclerViewPresenterCallbacks<MessageListAdapter> {

    void showProgressBar(boolean state);
}
