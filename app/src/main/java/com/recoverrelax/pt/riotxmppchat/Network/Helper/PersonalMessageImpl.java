package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;

import java.util.List;

import LolChatRiotDb.MessageDb;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PersonalMessageImpl implements PersonalMessageHelper {

    private PersonalMessageImplCallbacks callbacks;
    private RiotXmppDBRepository riotXmppDBRepository;

    public PersonalMessageImpl(PersonalMessageImplCallbacks callbacks) {
        this.callbacks = callbacks;
        this.riotXmppDBRepository = new RiotXmppDBRepository();
    }

    @Override
    public void getLastXPersonalMessageList(final int x, final String userToGetMessagesFrom) {
        riotXmppDBRepository.getLastXMessages(x, userToGetMessagesFrom)
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
        riotXmppDBRepository.getLastMessage(userToGetMessagesFrom)
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
