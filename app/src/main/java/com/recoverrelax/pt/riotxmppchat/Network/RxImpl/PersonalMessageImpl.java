package com.recoverrelax.pt.riotxmppchat.Network.RxImpl;

import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

import LolChatRiotDb.MessageDb;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Singleton
public class PersonalMessageImpl {

    @Inject RiotRosterManager riotRosterManager;

    @Singleton
    @Inject
    public PersonalMessageImpl() {

    }

    /**
     *  Get the last messages for the input user
     * @param x number of messages to return
     * @param userToGetMessagesFrom user to get messages from
     * @return list of messages
     */
    public Observable<List<MessageDb>> getLastXPersonalMessageList(final int x, final String userToGetMessagesFrom) {
        return riotRosterManager.getLastXMessages(x, userToGetMessagesFrom)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread());
    }

    /**
     * Get the last message from the input friend
     * @param userToGetMessagesFrom user to get last message from
     * @return the message
     */
    public Observable<MessageDb> getLastPersonalMessage(final String userToGetMessagesFrom) {
        return riotRosterManager.getFriendLastMessage(userToGetMessagesFrom)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
