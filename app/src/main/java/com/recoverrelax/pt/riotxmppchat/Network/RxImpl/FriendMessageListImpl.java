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

    /**
     * Get the personal messages returned by the user found from the input userXmppAddress
     * Get last message for that friend
     * convert it to a FriendListChatObject
     * @param userXmppAddress
     * @return
     */
    public Observable<FriendListChat> getPersonalMessage(final String userXmppAddress) {
        return riotRosterManager.getFriendFromXmppAddress(userXmppAddress)
                    .flatMap(friend -> riotRosterManager.getFriendLastMessage(friend.getUserXmppAddress())
                                        .map(messageDb -> new FriendListChat(friend, messageDb))
                    )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * get roster entries FROM
     * get messages for each roster entry, returns FriendListChat
     * filter to only return friends that have messages
     * convert back to a list
     * sort the list by last message
     * @return
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
