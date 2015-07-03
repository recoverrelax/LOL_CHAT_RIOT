package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;
import java.util.Collections;
import java.util.List;

import LolChatRiotDb.MessageDb;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FriendMessageListImpl {

    private RiotRosterManager riotRosterManager;

    public FriendMessageListImpl() {
        this.riotRosterManager = MainApplication.getInstance().getRiotXmppService().getRiotRosterManager();
    }

    public Observable<FriendListChat> getPersonalMessageSingleItem(final String userToReturn) {

        Observable<Friend> friendFromRosterEntry = riotRosterManager.getFriendFromXmppAddress(userToReturn);
        Observable<MessageDb> friendLastMessage = riotRosterManager.getFriendLastMessage(friendFromRosterEntry);

        return Observable.zip(friendFromRosterEntry, friendLastMessage, FriendListChat::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<FriendListChat>> getPersonalMessageList() {
        return riotRosterManager.getRosterEntries()
                .flatMap(rosterEntry -> {
                    Observable<Friend> friendFromRosterEntry = riotRosterManager.getFriendFromRosterEntry(rosterEntry);
                    Observable<MessageDb> friendLastMessage = riotRosterManager.getFriendLastMessage(friendFromRosterEntry);

                    return Observable.zip(friendFromRosterEntry, friendLastMessage, FriendListChat::new);
                })
                .filter(friendListChat -> friendListChat.getLastMessage() != null)
                .toList()
                .flatMap(friendListChatList -> {
                    Collections.sort(friendListChatList, new FriendListChat.LastMessageComparable());
                    Collections.reverse(friendListChatList);
                    return Observable.just(friendListChatList);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
