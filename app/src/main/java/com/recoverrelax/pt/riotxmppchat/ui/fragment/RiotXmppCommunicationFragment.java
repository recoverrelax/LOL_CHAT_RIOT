package com.recoverrelax.pt.riotxmppchat.ui.fragment;

import com.recoverrelax.pt.riotxmppchat.MainApplication;

public class RiotXmppCommunicationFragment extends BaseFragment{

    @Override
    public void onResume() {
        super.onResume();
        MainApplication.getInstance().getBusInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MainApplication.getInstance().getBusInstance().unregister(this);
    }
}
