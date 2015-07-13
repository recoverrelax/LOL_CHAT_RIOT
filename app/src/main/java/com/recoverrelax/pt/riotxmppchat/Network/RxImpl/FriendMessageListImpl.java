package com.recoverrelax.pt.riotxmppchat.Network.RxImpl;

import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FriendMessageListImpl {

    private RiotRosterManager riotRosterManager;

    @Inject
    public FriendMessageListImpl(RiotRosterManager riotRosterManager) {
        this.riotRosterManager = riotRosterManager;
    }

    public Observable<FriendListChat> getPersonalMessage(final String userXmppAddress) {
        return riotRosterManager.getFriendFromXmppAddress(userXmppAddress)
                    .flatMap(friend -> riotRosterManager.getFriendLastMessage(friend.getUserXmppAddress())
                                        .map(messageDb -> new FriendListChat(friend, messageDb))
                    )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<FriendListChat>> getPersonalMessageList() {
        return riotRosterManager.getRosterEntries()
                .flatMap(rosterEntry -> getPersonalMessage(rosterEntry.getUser()))
                .filter(friendListChat -> friendListChat.getLastMessage() != null)
                .toList()
                .map(friendListChatList -> {
                    Collections.sort(friendListChatList, new FriendListChat.LastMessageComparable());
                    Collections.reverse(friendListChatList);
                    return friendListChatList;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
