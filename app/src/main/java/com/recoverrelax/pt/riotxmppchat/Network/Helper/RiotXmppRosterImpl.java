package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RiotXmppRosterHelper;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RiotXmppRosterImpl implements RiotXmppRosterHelper {

    private RiotXmppRosterImplCallbacks callback;
    private AbstractXMPPConnection connection;

    private RiotXmppService riotXmppService;

    public RiotXmppRosterImpl(RiotXmppRosterImplCallbacks callback, AbstractXMPPConnection connection) {
        this.callback = callback;
        this.connection = connection;
        this.riotXmppService = MainApplication.getInstance().getRiotXmppService();
    }

    @Override
    public void getFullFriendsList(final boolean getOffline) {
        Observable.create(new Observable.OnSubscribe<List<Friend>>() {
                    @Override
                    public void call(Subscriber<? super List<Friend>> subscriber) {
                        Collection<RosterEntry> entries = riotXmppService.getRiotRosterManager().getRosterEntries();

                        List<Friend> friendList = new ArrayList<>();

                        for (RosterEntry entry : entries) {
                            Presence rosterPresence = riotXmppService.getRiotRosterManager().getRosterPresence(entry.getUser());
                            String userXmppAddress = AppXmppUtils.parseXmppAddress(entry.getUser());

                            Friend friend = new Friend(entry.getName(), userXmppAddress, rosterPresence);

                            if(!getOffline){
                                if(friend.isOnline())
                                    friendList.add(friend);
                            }else{
                                friendList.add(friend);
                            }

                            if(friend.isPlaying())
                                riotXmppService.getRiotRosterManager().addFriendPlaying(friend.getName(), friend.getUserXmppAddress());
                            else
                                riotXmppService.getRiotRosterManager().removeFriendPlaying(friend.getName(), friend.getUserXmppAddress());
                        }
                        /**
                         * Sort Friends By Online-First
                         */
                        Collections.sort(friendList, new Friend.OnlineOfflineComparator());

                        subscriber.onNext(friendList);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Friend>>() {
                    @Override public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        if(callback != null)
                            callback.onFullFriendsListFailedReception();
                    }

                    @Override
                    public void onNext(List<Friend> friendList) {
                        if(callback != null)
                            callback.onFullFriendsListReceived(friendList);
                    }
                });
    }

    @Override
    public void searchFriendsList(final String searchString) {
        Observable.create(new Observable.OnSubscribe<List<Friend>>() {
                    @Override
                    public void call(Subscriber<? super List<Friend>> subscriber) {
                        Collection<RosterEntry> entries = riotXmppService.getRiotRosterManager().getRosterEntries();

                        List<Friend> friendList = new ArrayList<>();

                        for (RosterEntry entry : entries) {
                            Presence rosterPresence = riotXmppService.getRiotRosterManager().getRosterPresence(entry.getUser());
                            String userXmppAddress = AppXmppUtils.parseXmppAddress(entry.getUser());

                            Friend friend = new Friend(entry.getName(), userXmppAddress, rosterPresence);

                            if(friend.getName().toLowerCase().contains(searchString.toLowerCase())){
                                friendList.add(friend);
                            }

                            if(friend.isPlaying())
                                riotXmppService.getRiotRosterManager().addFriendPlaying(friend.getName(), friend.getUserXmppAddress());
                            else
                                riotXmppService.getRiotRosterManager().removeFriendPlaying(friend.getName(), friend.getUserXmppAddress());
                        }

                        Collections.sort(friendList, new Friend.OnlineOfflineComparator());

                        subscriber.onNext(friendList);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Friend>>() {
                    @Override public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        if(callback != null)
                            callback.onSearchedFriendListFailedReception();
                    }

                    @Override
                    public void onNext(List<Friend> friendList) {
                        if(callback != null)
                            callback.onSearchedFriendListReceived(friendList);
                    }
                });
    }

    @Override
    public void getPresenceChanged(final Presence presence) {
        Observable.create(new Observable.OnSubscribe<Friend>() {
                    @Override
                    public void call(Subscriber<? super Friend> subscriber) {

                        Friend friend;
                        // Get whose User this presence belongs to
                        String user = presence.getFrom();

                        // Get the presence of specified User
                        Presence bestPresence = riotXmppService.getRiotRosterManager().getRosterPresence(presence.getFrom());

                        // Get roster entry for that user
                        RosterEntry entry = riotXmppService.getRiotRosterManager().getRosterEntry(user);

                        String userXmppAddress = AppXmppUtils.parseXmppAddress(entry.getUser());

                        friend = new Friend(entry.getName(), userXmppAddress, bestPresence);

                        if(friend.isPlaying())
                            MainApplication.getInstance().getRiotXmppService().getRiotRosterManager().addFriendPlaying(friend.getName(), friend.getUserXmppAddress());
                        else
                            MainApplication.getInstance().getRiotXmppService().getRiotRosterManager().removeFriendPlaying(friend.getName(), friend.getUserXmppAddress());

                        subscriber.onNext(friend);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Friend>() {
                    @Override public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        if(callback != null)
                            callback.onSingleFriendFailedReception();
                    }

                    @Override
                    public void onNext(Friend friend) {
                        if(callback != null)
                            callback.onSingleFriendReceived(friend);
                    }
                });
    }

    public interface RiotXmppRosterImplCallbacks {
        void onFullFriendsListReceived(List<Friend> friendList);
        void onFullFriendsListFailedReception();

        void onSearchedFriendListReceived(List<Friend> friendList);
        void onSearchedFriendListFailedReception();

        void onSingleFriendReceived(Friend friend);
        void onSingleFriendFailedReception();
    }
}
