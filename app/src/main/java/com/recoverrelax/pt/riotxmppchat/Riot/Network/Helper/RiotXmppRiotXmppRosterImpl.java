package com.recoverrelax.pt.riotxmppchat.Riot.Network.Helper;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RiotXmppDataLoaderCallback;
import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RiotXmppRosterHelper;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RiotXmppRiotXmppRosterImpl implements RiotXmppRosterHelper, Observer<List<Friend>> {

    private RiotXmppDataLoaderCallback<List<Friend>> mCallback;
    private Subscription mSubscription;
    private Fragment mFragment;

    public RiotXmppRiotXmppRosterImpl(@NonNull Fragment frag, RiotXmppDataLoaderCallback<List<Friend>> callback) {
        mCallback = callback;
        mFragment = frag;
    }

    @Override
    public void getFullFriendsList(final AbstractXMPPConnection connection) {
        mSubscription = AppObservable.bindFragment(mFragment,
                Observable.create(new Observable.OnSubscribe<List<Friend>>() {
                    @Override
                    public void call(Subscriber<? super List<Friend>> subscriber) {

                        Roster roster = Roster.getInstanceFor(connection);
                        Collection<RosterEntry> entries = roster.getEntries();

                        ArrayList<Friend> friendList = new ArrayList<>();
                        for (RosterEntry entry : entries) {
                            friendList.add(new Friend(entry.getName(), entry.getUser(),
                                    roster.getPresence(entry.getUser())));
                        }
                        subscriber.onNext(friendList);
                        subscriber.onCompleted();
                    }
                }))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void onCompleted() {
        if(mCallback!=null)
            mCallback.onComplete();
    }

    @Override
    public void onError(Throwable e) {
        if(mCallback!=null)
            mCallback.onFailure(e);
    }

    @Override
    public void onNext(List<Friend> f) {
        if(mCallback!=null)
            mCallback.onSuccess(f);
    }
}
