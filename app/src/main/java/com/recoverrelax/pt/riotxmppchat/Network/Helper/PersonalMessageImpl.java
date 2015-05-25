package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import android.support.v4.app.Fragment;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;

import java.util.Collections;
import java.util.List;

import LolChatRiotDb.MessageDb;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PersonalMessageImpl implements PersonalMessageHelper {

    private Observer<List<MessageDb>> mCallback;
    private Subscription mSubscription;
    private Fragment mFragment;

    private String TAG = this.getClass().getSimpleName();

    public PersonalMessageImpl(Observer<List<MessageDb>> mCallback) {
        mFragment = (Fragment)mCallback;
        this.mCallback = mCallback;
    }

    @Override
    public void getPersonalMessageList() {
        mSubscription = AppObservable.bindFragment(mFragment,
                Observable.create(new Observable.OnSubscribe<List<MessageDb>>() {
                    @Override
                    public void call(Subscriber<? super List<MessageDb>> subscriber) {

                        List<MessageDb> messageList = RiotXmppDBRepository.getAllMessages();
                        Collections.reverse(messageList);
                        subscriber.onNext(messageList);
                        subscriber.onCompleted();
                    }
                }))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mCallback);
    }

    @Override
    public void getLastXPersonalMessageList(final int x) {
        mSubscription = AppObservable.bindFragment(mFragment,
                Observable.create(new Observable.OnSubscribe<List<MessageDb>>() {
                    @Override
                    public void call(Subscriber<? super List<MessageDb>> subscriber) {

                        List<MessageDb> messageList = RiotXmppDBRepository.getLastXMessages(x);
                        Collections.reverse(messageList);

                        subscriber.onNext(messageList);
                        subscriber.onCompleted();
                    }
                }))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mCallback);
    }
}
