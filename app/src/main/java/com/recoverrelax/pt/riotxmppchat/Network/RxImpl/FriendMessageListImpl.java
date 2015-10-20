package com.recoverrelax.pt.riotxmppchat.Network.RxImpl;

import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Singleton
public class FriendMessageListImpl {

    @Inject RiotRosterManager riotRosterManager;

    @Singleton
    @Inject
    public FriendMessageListImpl() {
    }

    /**
     * Get the personal messages returned by the user found from the input userXmppAddress
     * Get last message for that friend
     * convert it to a FriendListChatObject
     *
     * @param userXmppAddress userXmppAddress
     * @return observable of friendListChat
     */
    public Observable<FriendListChat> getPersonalMessage(final String userXmppAddress) {
        return riotRosterManager.getFriendFromXmppAddress(userXmppAddress)
                .flatMap(friend -> riotRosterManager.getFriendLastMessage(friend.getUserXmppAddress())
                                .map(messageDb -> new FriendListChat(friend, messageDb))
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    /**
     * get roster entries FROM
     * get messages for each roster entry, returns FriendListChat
     * filter to only return friends that have messages
     * convert back to a list
     * sort the list by last message
     *
     * @return observable of list friendListChat
     */
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation());
    }
}
