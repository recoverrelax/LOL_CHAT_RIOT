package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import android.support.v4.app.Fragment;

import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnFriendChangedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnFriendListFailedLoadingEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnFriendListLoadedEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RiotXmppRosterHelper;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendListFragment;
import com.squareup.otto.Bus;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jxmpp.util.XmppStringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RiotXmppRosterImpl implements RiotXmppRosterHelper, Observer<RiotXmppRosterImpl.FriendList>{

    private Subscription mSubscription;
    private Fragment mFragment;
    private AbstractXMPPConnection connection;
    private Bus busInstance;
    private RiotXmppService riotXmppService;

    private String TAG = this.getClass().getSimpleName();

    public RiotXmppRosterImpl(Fragment frag, AbstractXMPPConnection connection) {
        mFragment = frag;
        this.connection = connection;
        this.busInstance = MainApplication.getInstance().getBusInstance();
        this.riotXmppService = MainApplication.getInstance().getRiotXmppService();
    }

    @Override
    public void getFullFriendsList() {
        mSubscription = AppObservable.bindFragment(mFragment,
                Observable.create(new Observable.OnSubscribe<FriendList>() {
                    @Override
                    public void call(Subscriber<? super FriendList> subscriber) {
                        Collection<RosterEntry> entries = riotXmppService.getRosterEntries();

                        FriendList friendList = new FriendList(new ArrayList<Friend>(), FriendListOperation.FRIEND_LIST);

                        for (RosterEntry entry : entries) {
                            Presence rosterPresence = riotXmppService.getRosterPresence(entry.getUser());
                            String userXmppAddress = AppXmppUtils.parseXmppAddress(entry.getUser());

                            Friend friend = new Friend(entry.getName(), userXmppAddress, rosterPresence);
                            friendList.getFriendList().add(friend);

                            if(friend.isPlaying())
                                riotXmppService.addFriendPlaying(friend.getName(), friend.getUserXmppAddress());
                            else
                                riotXmppService.removeFriendPlaying(friend.getName(), friend.getUserXmppAddress());

                            /**
                             * Sort Friends By Online-First
                             */
                            friendList.sortAlphabetically();
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
    public void searchFriendsList(final String searchString) {
        mSubscription = AppObservable.bindFragment(mFragment,
                Observable.create(new Observable.OnSubscribe<FriendList>() {
                    @Override
                    public void call(Subscriber<? super FriendList> subscriber) {
                        Collection<RosterEntry> entries = riotXmppService.getRosterEntries();

                        FriendList friendList = new FriendList(new ArrayList<Friend>(), FriendListOperation.FRIEND_LIST);

                        for (RosterEntry entry : entries) {
                            Presence rosterPresence = riotXmppService.getRosterPresence(entry.getUser());
                            String userXmppAddress = AppXmppUtils.parseXmppAddress(entry.getUser());

                            Friend friend = new Friend(entry.getName(), userXmppAddress, rosterPresence);

                            if(friend.getName().toLowerCase().contains(searchString.toLowerCase())){
                                friendList.getFriendList().add(friend);
                            }

                            if(friend.isPlaying())
                                riotXmppService.addFriendPlaying(friend.getName(), friend.getUserXmppAddress());
                            else
                                riotXmppService.removeFriendPlaying(friend.getName(), friend.getUserXmppAddress());

                            /**
                             * Sort Friends By Online-First
                             */
                            friendList.sortAlphabetically();
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
    public void getPresenceChanged(final Presence presence) {
        mSubscription = AppObservable.bindFragment(mFragment,
                Observable.create(new Observable.OnSubscribe<FriendList>() {
                    @Override
                    public void call(Subscriber<? super FriendList> subscriber) {
                        FriendList friendList = new FriendList(new ArrayList<Friend>(), FriendListOperation.FRIEND_CHANGED);

                        // Get whose User this presence belongs to
                        String user = presence.getFrom();

                        // Get the presence of specified User
                        Presence bestPresence = riotXmppService.getRosterPresence(presence.getFrom());

                        // Get roster entry for that user
                        RosterEntry entry = riotXmppService.getRosterEntry(user);

                        String userXmppAddress = AppXmppUtils.parseXmppAddress(entry.getUser());

                        Friend friend = new Friend(entry.getName(), userXmppAddress, bestPresence);
                        friendList.getFriendList().add(friend);

                        if(friend.isPlaying())
                            MainApplication.getInstance().getRiotXmppService().addFriendPlaying(friend.getName(), friend.getUserXmppAddress());
                        else
                            MainApplication.getInstance().getRiotXmppService().removeFriendPlaying(friend.getName(), friend.getUserXmppAddress());

                        subscriber.onNext(friendList);
                        subscriber.onCompleted();
                    }
                }))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void onCompleted() {}

    @Override
    public void onError(Throwable e) {
        busInstance.post(new OnFriendListFailedLoadingEvent());
    }

    @Override
    public void onNext(FriendList friendList) {
        
        switch (friendList.getOperation()) {
            case FRIEND_LIST:
                /** {@link FriendListFragment#OnFriendListLoaded(OnFriendListLoadedEvent)}  **/
                busInstance.post(new OnFriendListLoadedEvent(friendList));
                break;
            case FRIEND_ADD:
                break;
            case FRIEND_CHANGED:
                /** {@link FriendListFragment#onFriendChanged(OnFriendChangedEvent)}  **/
               busInstance.post(new OnFriendChangedEvent(friendList.getFriendList().get(0)));
                break;
            case FRIEND_DELETE:
                break;
            case FRIEND_UPDATE:
                break;
        }
    }

    public class FriendList {
        ArrayList<Friend> friendList;
        FriendListOperation operation;

        public FriendList(ArrayList<Friend> friendList, FriendListOperation operation) {
            this.friendList = friendList;
            this.operation = operation;
        }

        public void sortAlphabetically(){
            Collections.sort(friendList, new Friend.OnlineOfflineComparator());
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
