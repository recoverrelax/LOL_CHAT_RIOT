package com.recoverrelax.pt.riotxmppchat.Network.RxImpl;

import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import org.jivesoftware.smack.packet.Presence;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RiotXmppRosterImpl {

    private RiotRosterManager riotRosterManager;

    @Inject
    public RiotXmppRosterImpl(RiotRosterManager riotRosterManager) {
        this.riotRosterManager = riotRosterManager;
    }

    public Observable<List<Friend>> getFullFriendsList(final boolean getOffline) {
        return riotRosterManager.getRosterEntries()
                .flatMap(riotRosterManager::getFriendFromRosterEntry)
                .filter(friend -> getOffline || friend.isOnline())
                .doOnNext(friend -> riotRosterManager.updateFriend(friend.getUserRosterPresence()))
                .toSortedList((a, b) -> {
                    if (samePresence(a, b))
                        return 0;
                    else if (a.getUserRosterPresence().isAvailable() && !b.getUserRosterPresence().isAvailable())
                        return -1;
                    else
                        return 1;
                })
                .doOnNext(friendList -> riotRosterManager.setEnabled(true))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public boolean samePresence(Friend a, Friend b) {
        if (a.getUserRosterPresence().isAvailable() && b.getUserRosterPresence().isAvailable())
            return true;
        else if (!a.getUserRosterPresence().isAvailable() && !b.getUserRosterPresence().isAvailable()) {
            return true;
        }
        return false;
    }

    public Observable<List<Friend>> searchFriendsList(final String searchString) {
        return riotRosterManager.getRosterEntries()
                .flatMap(riotRosterManager::getFriendFromRosterEntry)
                .filter(friend -> friend.getName().toLowerCase().contains(searchString.toLowerCase()))
                .doOnNext(friend -> riotRosterManager.updateFriend(friend.getUserRosterPresence()))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Friend> getPresenceChanged(final Presence presence) {
        return riotRosterManager.getFriendFromXmppAddress(presence.getFrom())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
