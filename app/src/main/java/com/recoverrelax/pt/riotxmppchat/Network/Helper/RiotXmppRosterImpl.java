package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import org.jivesoftware.smack.packet.Presence;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RiotXmppRosterImpl {

    private RiotXmppService riotXmppService;
    private RiotRosterManager riotRosterManage;

    public RiotXmppRosterImpl() {
        this.riotXmppService = MainApplication.getInstance().getRiotXmppService();
        this.riotRosterManage = MainApplication.getInstance().getRiotXmppService().getRiotRosterManager();
    }

    public Observable<List<Friend>> getFullFriendsList(final boolean getOffline) {
        return riotRosterManage.getRosterEntries()
                .flatMap(riotRosterManage::getFriendFromRosterEntry)
                .filter(friend -> getOffline || friend.isOnline())
                .doOnNext(friend -> riotRosterManage.getFriendStatusTracker().updateFriend(friend))
                .toList()
                .flatMap(friendList -> {
                    Collections.sort(friendList, new Friend.OnlineOfflineComparator());
                    return Observable.just(friendList);
                })
                .doOnNext(friendList -> riotXmppService.getRiotRosterManager().getFriendStatusTracker().setEnabled(true))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Friend>> searchFriendsList(final String searchString) {
        return riotRosterManage.getRosterEntries()
                .flatMap(riotRosterManage::getFriendFromRosterEntry)
                .filter(friend -> friend.getName().toLowerCase().contains(searchString.toLowerCase()))
                .doOnNext(friend -> riotXmppService.getRiotRosterManager().getFriendStatusTracker().updateFriend(friend))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Friend> getPresenceChanged(final Presence presence) {
        return riotRosterManage.getFriendFromXmppAddress(presence.getFrom())
                .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }
}
