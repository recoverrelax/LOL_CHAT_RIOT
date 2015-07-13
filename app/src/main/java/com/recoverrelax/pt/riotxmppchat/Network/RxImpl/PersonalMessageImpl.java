package com.recoverrelax.pt.riotxmppchat.Network.RxImpl;

import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import java.util.List;
import javax.inject.Inject;
import LolChatRiotDb.MessageDb;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PersonalMessageImpl {

    private RiotRosterManager riotRosterManager;

    @Inject
    public PersonalMessageImpl(RiotRosterManager riotRosterManager) {
        this.riotRosterManager = riotRosterManager;
    }

    public Observable<List<MessageDb>> getLastXPersonalMessageList(final int x, final String userToGetMessagesFrom) {
        return riotRosterManager.getLastXMessages(x, userToGetMessagesFrom)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread());
    }

    public Observable<MessageDb> getLastPersonalMessage(final String userToGetMessagesFrom) {
        return riotRosterManager.getFriendLastMessage(userToGetMessagesFrom)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
