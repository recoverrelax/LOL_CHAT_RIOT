package com.recoverrelax.pt.riotxmppchat.ui.fragment;

import android.support.v4.app.Fragment;

import com.recoverrelax.pt.riotxmppchat.Storage.BusHandler;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

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

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }
}
