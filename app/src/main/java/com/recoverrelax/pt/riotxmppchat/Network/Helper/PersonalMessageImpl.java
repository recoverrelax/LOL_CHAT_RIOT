package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;

import java.util.List;

import LolChatRiotDb.MessageDb;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PersonalMessageImpl implements PersonalMessageHelper {

    private PersonalMessageImplCallbacks callbacks;

    public PersonalMessageImpl(PersonalMessageImplCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public void getLastXPersonalMessageList(final int x, final String userToGetMessagesFrom) {
        Observable.create(new Observable.OnSubscribe<List<MessageDb>>() {
                    @Override
                    public void call(Subscriber<? super List<MessageDb>> subscriber) {
                        List<MessageDb> messageList = RiotXmppDBRepository.getLastXMessages(x, userToGetMessagesFrom);
                        subscriber.onNext(messageList);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<MessageDb>>() {

                    @Override public void onCompleted() { }
                    @Override public void onError(Throwable e) {
                        if(callbacks != null)
                            callbacks.onLastXPersonalMessageListFailedReception(e);
                    }

                    @Override
                    public void onNext(List<MessageDb> messageList) {
                        if(callbacks != null)
                            callbacks.onLastXPersonalMessageListReceived(messageList);
                    }
                });
    }

    @Override
    public void getLastPersonalMessage(final String userToGetMessagesFrom) {
        Observable.create(new Observable.OnSubscribe<MessageDb>() {
                    @Override
                    public void call(Subscriber<? super MessageDb> subscriber) {

                        MessageDb lastMessage = RiotXmppDBRepository.getLastMessage(userToGetMessagesFrom);
                        subscriber.onNext(lastMessage);
                        subscriber.onCompleted();
                    }
                })
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
