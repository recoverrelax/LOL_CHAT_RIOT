package com.recoverrelax.pt.riotxmppchat.Riot.Network.Helper;

import android.support.v4.app.Fragment;

import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RiotXmppRosterHelper;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.Collection;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RiotXmppRiotXmppRosterImpl implements RiotXmppRosterHelper {

    private Observer<FriendList> mCallback;
    private Subscription mSubscription;
    private Fragment mFragment;
    private AbstractXMPPConnection connection;

    public RiotXmppRiotXmppRosterImpl(Observer<FriendList> mCallback, AbstractXMPPConnection connection) {
        mFragment = (Fragment)mCallback;
        this.mCallback = mCallback;
        this.connection = connection;
    }

    @Override
    public void getFullFriendsList() {
        mSubscription = AppObservable.bindFragment(mFragment,
                Observable.create(new Observable.OnSubscribe<FriendList>() {
                    @Override
                    public void call(Subscriber<? super FriendList> subscriber) {
                        Roster roster = Roster.getInstanceFor(connection);
                        Collection<RosterEntry> entries = roster.getEntries();

                        FriendList friendList = new FriendList(new ArrayList<Friend>(), FriendListOperation.FRIEND_LIST);

                        for (RosterEntry entry : entries) {
                            friendList.getFriendList().add(new Friend(entry.getName(), entry.getUser(), roster.getPresence(entry.getUser())));
                        }
                        subscriber.onNext(friendList);
                        subscriber.onCompleted();
                    }
                }))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mCallback);
    }

    @Override
    public void getPresenceChanged(final Presence presence) {
        mSubscription = AppObservable.bindFragment(mFragment,
                Observable.create(new Observable.OnSubscribe<FriendList>() {
                    @Override
                    public void call(Subscriber<? super FriendList> subscriber) {
                        Roster roster = Roster.getInstanceFor(connection);
                        FriendList friendList = new FriendList(new ArrayList<Friend>(), FriendListOperation.FRIEND_CHANGED);

                        String user = presence.getFrom();
                        Presence bestPresence = roster.getPresence(user);
                        RosterEntry entry = roster.getEntry(user);

                        friendList.getFriendList().add(new Friend(entry.getName(), entry.getUser(), bestPresence));

                        subscriber.onNext(friendList);
                        subscriber.onCompleted();
                    }
                }))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mCallback);
    }

    public class FriendList {
        ArrayList<Friend> friendList;
        FriendListOperation operation;

        public FriendList(ArrayList<Friend> friendList, FriendListOperation operation) {
            this.friendList = friendList;
            this.operation = operation;
        }

        public ArrayList<Friend> getFriendList() {
            return friendList;
        }

        public FriendListOperation getOperation() {
            return operation;
        }
    }

    public enum FriendListOperation {
        FRIEND_LIST,
        FRIEND_ADD,
        FRIEND_UPDATE,
        FRIEND_DELETE,
        FRIEND_CHANGED;
    }
}
