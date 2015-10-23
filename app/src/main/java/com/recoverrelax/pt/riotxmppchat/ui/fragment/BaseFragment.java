package com.recoverrelax.pt.riotxmppchat.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Storage.BusHandler;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class BaseFragment extends Fragment {

    @Inject
    protected BusHandler bus;

    @Inject
    protected RiotXmppDBRepository riotXmppDBRepository;

    @Inject
    protected DataStorage mDataStorage;


    // stuff
    public void setToolbarTitle(CharSequence title) {
        getBaseActivity().setTitle(title);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getAppComponent().inject(this);
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
