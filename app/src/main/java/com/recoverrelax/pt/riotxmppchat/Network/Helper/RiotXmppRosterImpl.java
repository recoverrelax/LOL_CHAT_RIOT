package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import android.support.v4.app.Fragment;

import com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils;
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

public class RiotXmppRosterImpl implements RiotXmppRosterHelper {

    private Observer<FriendList> mCallback;
    private Subscription mSubscription;
    private Fragment mFragment;
    private AbstractXMPPConnection connection;


    private String TAG = this.getClass().getSimpleName();

    public RiotXmppRosterImpl(Observer<FriendList> mCallback, AbstractXMPPConnection connection) {
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
                        // Get a roster from the connection instance
                        Roster roster = Roster.getInstanceFor(connection);

                        FriendList friendList = new FriendList(new ArrayList<Friend>(), FriendListOperation.FRIEND_CHANGED);

                        // Get whose User this presence belongs to
                        String user = presence.getFrom();
                        LogUtils.LOGI(TAG, TAG + "_" + "PresenceChanged for user: " + user);

                        // Get the presence of specified User
                        Presence bestPresence = roster.getPresence(user);

                        // Get roster entry for that user
                        RosterEntry entry = roster.getEntry(user);
                        String name = entry.getName();
                        String userXmppAddress = entry.getUser();

                        LogUtils.LOGI(TAG, TAG + "RosterEntry.getName()" + name);
                        LogUtils.LOGI(TAG, TAG + "RosterEntry.getEntry()" + userXmppAddress);

                        friendList.getFriendList().add(new Friend(name, userXmppAddress, bestPresence));

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
