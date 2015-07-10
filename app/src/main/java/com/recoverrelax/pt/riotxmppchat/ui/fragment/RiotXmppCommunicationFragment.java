package com.recoverrelax.pt.riotxmppchat.ui.fragment;

import android.os.Bundle;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;

import javax.inject.Inject;

public class RiotXmppCommunicationFragment extends BaseFragment{

    @Inject
    RiotXmppDBRepository riotXmppDBRepository;

    @Inject
    DataStorage mDataStorage;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getAppComponent().inject(this);
    }

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
