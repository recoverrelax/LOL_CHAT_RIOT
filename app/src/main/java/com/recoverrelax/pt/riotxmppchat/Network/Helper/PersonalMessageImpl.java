package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MainApplication;

import java.util.List;

import LolChatRiotDb.MessageDb;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PersonalMessageImpl implements PersonalMessageHelper {

    private PersonalMessageImplCallbacks callbacks;
    private RiotXmppDBRepository riotXmppDBRepository;
    private GlobalImpl globalImpl;

    public PersonalMessageImpl(PersonalMessageImplCallbacks callbacks) {
        this.callbacks = callbacks;
        this.riotXmppDBRepository = new RiotXmppDBRepository();
        this.globalImpl = new GlobalImpl(MainApplication.getInstance().getConnection());
    }

    @Override
    public void getLastXPersonalMessageList(final int x, final String userToGetMessagesFrom) {
        globalImpl.getLastXMessages(x, userToGetMessagesFrom)
                .observeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<MessageDb>>() {
                    @Override public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        if (callbacks != null)
                            callbacks.onLastXPersonalMessageListFailedReception(e);
                    }

                    @Override
                    public void onNext(List<MessageDb> messageList) {
                        if (callbacks != null)
                            callbacks.onLastXPersonalMessageListReceived(messageList);
                    }
                });
    }

    @Override
    public void getLastPersonalMessage(final String userToGetMessagesFrom) {
        globalImpl.getLastMessage(userToGetMessagesFrom)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageDb>() {
                    @Override public void onCompleted() { }
                    @Override public void onError(Throwable e) {
                        if(callbacks != null)
                            callbacks.onLastPersonalMessageFailedReception(e);
                    }

                    @Override
                    public void onNext(MessageDb messageDbs) {
                        if(callbacks != null)
                            callbacks.onLastPersonalMessageReceived(messageDbs);
                    }
                });
    }

    public interface PersonalMessageImplCallbacks{
        void onLastXPersonalMessageListReceived(List<MessageDb> messageList);
        void onLastXPersonalMessageListFailedReception(Throwable e);

        void onLastPersonalMessageReceived(MessageDb message);
        void onLastPersonalMessageFailedReception(Throwable e);
    }
}
