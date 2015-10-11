package com.recoverrelax.pt.riotxmppchat.Network.RxImpl;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppGlobals;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiRealmDataVersion;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import org.jivesoftware.smack.packet.Presence;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Singleton
public class RiotXmppRosterImpl {

    @Inject RiotRosterManager riotRosterManager;
    @Inject RiotApiRealmDataVersion realmData;

    @Singleton
    @Inject
    public RiotXmppRosterImpl() {

    }

    /**
     * get roster entries FROM
     * get Friends for roster entry FROM
     * filter offline friends
     * each friend, update friend status
     * join the list and sort
     * enable friend tracker
     * @param getOffline: filter to show or not offline friends
     * @return
     */
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

    private boolean samePresence(Friend a, Friend b) {
        if (a.getUserRosterPresence().isAvailable() && b.getUserRosterPresence().isAvailable())
            return true;
        else if (!a.getUserRosterPresence().isAvailable() && !b.getUserRosterPresence().isAvailable()) {
            return true;
        }
        return false;
    }

    /**
     * get roster entries FROM
     * get friend for each entry
     * filter and get each friend for the search query
     * for each result, update friend status
     * convert back to a list
     * @param searchString
     * @return
     */
    public Observable<List<Friend>> searchFriendsList(final String searchString) {
        return riotRosterManager.getRosterEntries()
                .flatMap(riotRosterManager::getFriendFromRosterEntry)
                .filter(friend -> friend.getName().toLowerCase().contains(searchString.toLowerCase()))
                .doOnNext(friend -> riotRosterManager.updateFriend(friend.getUserRosterPresence()))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Friend>> updateFriendListWithChampAndProfileUrl(List<Friend> friends){
        return Observable.zip(realmData.getProfileIconBaseUrl(), realmData.getChampionDDBaseUrl(), (profileUrl, championUrl) -> {

            for (Friend f : friends) {
                String profileIconId = f.getProfileIconId();
                f.setProfileIconWithUrl(profileUrl + profileIconId + AppGlobals.DD_VERSION.PROFILEICON_EXTENSION);

                String champNameId = f.getChampionNameFormatted();
                f.setChampIconWithUrl(championUrl + champNameId + AppGlobals.DD_VERSION.CHAMPION_EXTENSION);
            }
            return friends;
        });
    }

    public Observable<Friend> updateFriendWithChampAndProfileUrl(Friend f){
        return Observable.zip(realmData.getProfileIconBaseUrl(), realmData.getChampionDDBaseUrl(), (profileUrl, championUrl) -> {


                String profileIconId = f.getProfileIconId();
                f.setProfileIconWithUrl(profileUrl + profileIconId + AppGlobals.DD_VERSION.PROFILEICON_EXTENSION);

                String champNameId = f.getChampionNameFormatted();
                f.setChampIconWithUrl(championUrl + champNameId + AppGlobals.DD_VERSION.CHAMPION_EXTENSION);

            return f;
        });
    }

    /**
     * get a friend from the roster manager
     * @param presence
     * @return
     */
    public Observable<Friend> getPresenceChanged(final Presence presence) {
        return riotRosterManager.getFriendFromXmppAddress(presence.getFrom())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
