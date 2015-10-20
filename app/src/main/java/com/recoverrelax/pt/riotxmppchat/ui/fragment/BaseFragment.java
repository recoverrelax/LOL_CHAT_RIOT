package com.recoverrelax.pt.riotxmppchat.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public class BaseFragment extends Fragment {

    @Inject
    protected Bus bus;

    @Inject
    protected RiotXmppDBRepository riotXmppDBRepository;

    @Inject
    protected DataStorage mDataStorage;


    // stuff
    public void setToolbarTitle(CharSequence title){
        getBaseActivity().setTitle(title);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getAppComponent().inject(this);
    }

    public BaseActivity getBaseActivity(){
        return (BaseActivity) getActivity();
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
