package com.recoverrelax.pt.riotxmppchat.ui.mvp.dashboard;

import android.support.v7.widget.RecyclerView;

public interface DashBoardPresenter {

    void onResume(int size);

    void onPause();

    void getUnreadedMessageCount();

    void getFriendStatusInfo();

    void getFreeChampRotationList(int size);

    void getLogLast20();

    void getLogSingleItem();

    void getMessageIconDrawable();

    void configRecyclerView();

    void configAdapter(RecyclerView recyclerView);
}
