package com.recoverrelax.pt.riotxmppchat.ui.fragment;

import android.os.Bundle;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public class RiotXmppCommunicationFragment extends BaseFragment{

    @Inject
    RiotXmppDBRepository riotXmppDBRepository;

    @Inject
    DataStorage mDataStorage;

    @Inject
    Bus bus;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getAppComponent().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }
}
