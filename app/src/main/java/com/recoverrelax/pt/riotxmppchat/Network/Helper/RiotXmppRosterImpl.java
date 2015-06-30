package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public class RiotXmppRosterImpl implements RiotXmppRosterHelper {

    private RiotXmppRosterImplCallbacks callback;
    private AbstractXMPPConnection connection;

    private RiotXmppService riotXmppService;
    private GlobalImpl globalImpl;

    public RiotXmppRosterImpl(RiotXmppRosterImplCallbacks callback, AbstractXMPPConnection connection) {
        this.callback = callback;
        this.connection = connection;
        this.riotXmppService = MainApplication.getInstance().getRiotXmppService();
        this.globalImpl = new GlobalImpl(connection);
    }

    @Override
    public void getFullFriendsList(final boolean getOffline) {
        globalImpl.getRosterEntries(riotXmppService)
                .flatMap(rosterEntry -> globalImpl.getFriendFromRosterEntry(rosterEntry, riotXmppService))
                .filter(friend -> getOffline || friend.isOnline())
                .doOnNext(friend -> riotXmppService.getRiotRosterManager().getFriendStatusTracker().updateFriend(friend))
                .toList()
                .flatMap(friendList -> {
                    Collections.sort(friendList, new Friend.OnlineOfflineComparator());
                    return Observable.just(friendList);
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Friend>>() {
                    @Override public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        if (callback != null)
                            callback.onFullFriendsListFailedReception();
                    }

                    @Override
                    public void onNext(List<Friend> friendList) {
                        LOGI("1111", friendList.size() + "");
                        riotXmppService.getRiotRosterManager().getFriendStatusTracker().setEnabled(true);
                        if (callback != null) {
                            callback.onFullFriendsListReceived(friendList);
                        }
                    }
                });
    }

    @Override
    public void searchFriendsList(final String searchString) {
        globalImpl.getRosterEntries(riotXmppService)
                .flatMap(rosterEntry -> globalImpl.getFriendFromRosterEntry(rosterEntry, riotXmppService))
                .filter(friend -> friend.getName().toLowerCase().contains(searchString.toLowerCase()))
                .doOnNext(friend -> riotXmppService.getRiotRosterManager().getFriendStatusTracker().updateFriend(friend))
                .toList()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Friend>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (callback != null)
                            callback.onSearchedFriendListFailedReception();
                    }

                    @Override
                    public void onNext(List<Friend> friendList) {
                        if (callback != null)
                            callback.onSearchedFriendListReceived(friendList);
                    }
                });
    }

    @Override
    public void getPresenceChanged(final Presence presence, final boolean getOffline) {
        globalImpl.getFriendFromXmppAddress(presence.getFrom(), riotXmppService)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Friend>() {
                    @Override public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        if (callback != null)
                            callback.onSingleFriendFailedReception();
                    }

                    @Override
                    public void onNext(Friend friend) {
                        if (callback != null)
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
