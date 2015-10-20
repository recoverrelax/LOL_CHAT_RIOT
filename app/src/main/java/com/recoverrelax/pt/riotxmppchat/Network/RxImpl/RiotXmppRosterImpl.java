package com.recoverrelax.pt.riotxmppchat.Network.RxImpl;

import android.support.annotation.IntDef;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppGlobals;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiRealmDataVersion;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;

import org.jivesoftware.smack.packet.Presence;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Singleton
public class RiotXmppRosterImpl {

    public static final int SORT_MODE_NAME = 0;
    public static final int SORT_MODE_STATUS = 1;
    @Inject RiotRosterManager riotRosterManager;
    @Inject RiotApiRealmDataVersion realmData;
    @Singleton
    @Inject
    public RiotXmppRosterImpl() {
    }

    public Observable<List<Friend>> getFullFriendsList(final boolean getOffline, final int sortMode) {
        return riotRosterManager.getRosterEntries()
                .flatMap(riotRosterManager::getFriendFromRosterEntry)
                .filter(friend -> getOffline || friend.isOnline()) // filter onlineOffline
                .observeOn(Schedulers.computation())
                .doOnNext(friend -> riotRosterManager.updateFriend(friend.getUserRosterPresence()))
                .toList()
                .map(friends -> {
                    if (sortMode == SORT_MODE_STATUS)
                        return sortByStatus(friends);
                    else
                        return sortByName(friends);
                })
                .doOnNext(friendList -> riotRosterManager.setEnabled(true))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private List<Friend> sortByStatus(List<Friend> friends) {
        List<Friend> online = new ArrayList<>();
        List<Friend> offline = new ArrayList<>();

        for (Friend f : friends) {
            if (f.isOnline())
                online.add(f);
            if (f.isOffline())
                offline.add(f);
        }

        List<Friend> onlinePlaying = new ArrayList<>();
        List<Friend> onlineChatting = new ArrayList<>();
        List<Friend> onlineDnd = new ArrayList<>();
        List<Friend> onlineAway = new ArrayList<>();

        for (Friend f : online) {
            if (f.isPlaying())
                onlinePlaying.add(f);
            else if (f.isChatting())
                onlineChatting.add(f);
            else if (f.isAway())
                onlineAway.add(f);
            else onlineDnd.add(f);
        }

        // Sort everything alphabetically
        Collections.sort(onlinePlaying, (a, b) -> a.getName().compareTo(b.getName()));
        Collections.sort(onlineChatting, (a, b) -> a.getName().compareTo(b.getName()));
        Collections.sort(onlineDnd, (a, b) -> a.getName().compareTo(b.getName()));
        Collections.sort(onlineAway, (a, b) -> a.getName().compareTo(b.getName()));
        Collections.sort(offline, (a, b) -> a.getName().compareTo(b.getName()));

        List<Friend> friendListSorted = new ArrayList<>();
        friendListSorted.addAll(onlinePlaying);
        friendListSorted.addAll(onlineChatting);
        friendListSorted.addAll(onlineDnd);
        friendListSorted.addAll(onlineAway);
        friendListSorted.addAll(offline);

        return friendListSorted;
    }

    public List<Friend> sortByName(List<Friend> friends) {
        List<Friend> online = new ArrayList<>();
        List<Friend> offline = new ArrayList<>();

        for (Friend f : friends) {
            if (f.isOnline())
                online.add(f);
            if (f.isOffline())
                offline.add(f);
        }

        Collections.sort(online, (a, b) -> a.getName().toLowerCase().compareTo(b.getName().toLowerCase()));
        Collections.sort(offline, (a, b) -> a.getName().toLowerCase().compareTo(b.getName().toLowerCase()));

        List<Friend> friendListSorted = new ArrayList<>();
        friendListSorted.addAll(online);
        friendListSorted.addAll(offline);

        return friendListSorted;
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

    public Observable<List<Friend>> updateFriendListWithChampAndProfileUrl(List<Friend> friends) {
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

    public Observable<Friend> updateFriendWithChampAndProfileUrl(Friend f) {
        return Observable.zip(realmData.getProfileIconBaseUrl(), realmData.getChampionDDBaseUrl(), (profileUrl, championUrl) -> {

            String profileIconId = f.getProfileIconId();
            f.setProfileIconWithUrl(profileUrl + profileIconId + AppGlobals.DD_VERSION.PROFILEICON_EXTENSION);

            String champNameId = f.getChampionNameFormatted();
            f.setChampIconWithUrl(championUrl + champNameId + AppGlobals.DD_VERSION.CHAMPION_EXTENSION);

            return f;
        });
    }

    public Observable<Friend> getPresenceChanged(final Presence presence) {
        return riotRosterManager.getFriendFromXmppAddress(presence.getFrom())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SORT_MODE_NAME, SORT_MODE_STATUS})
    public @interface FriendListSortMode {
    }
}
