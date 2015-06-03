package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import android.support.v4.app.Fragment;
import android.util.Pair;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.EventHandling.PersonalMessageList.OnLastPersonalMessageReceivedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.PersonalMessageList.OnLastXPersonalMessageListReceivedEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.PersonalMessageFragment;

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

public class PersonalMessageImpl implements PersonalMessageHelper, Observer<Pair<PersonalMessageImpl.Method, List<MessageDb>>>{

    private Subscription mSubscription;
    private Fragment mFragment;

    private String TAG = this.getClass().getSimpleName();

    public PersonalMessageImpl(Fragment frag) {
        mFragment = frag;
    }

    @Override
    public void getLastXPersonalMessageList(final int x, final String connectedUser, final String userToGetMessagesFrom) {
        mSubscription = AppObservable.bindFragment(mFragment,
                Observable.create(new Observable.OnSubscribe<Pair<PersonalMessageImpl.Method, List<MessageDb>>>() {
                    @Override
                    public void call(Subscriber<? super Pair<PersonalMessageImpl.Method, List<MessageDb>>> subscriber) {

                        List<MessageDb> messageList = RiotXmppDBRepository.getLastXMessages(x, connectedUser, userToGetMessagesFrom);
                        subscriber.onNext(new Pair<Method, List<MessageDb>>(Method.RETURN_ALL, messageList));
                        subscriber.onCompleted();
                    }
                }))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void getLastPersonalMessage(final String connectedUser, final String userToGetMessagesFrom) {
        mSubscription = AppObservable.bindFragment(mFragment,
                Observable.create(new Observable.OnSubscribe<Pair<PersonalMessageImpl.Method, List<MessageDb>>>() {
                    @Override
                    public void call(Subscriber<? super Pair<PersonalMessageImpl.Method, List<MessageDb>>> subscriber) {

                        List<MessageDb> lastMessage = RiotXmppDBRepository.getLastMessageAsList(connectedUser, userToGetMessagesFrom);
                        subscriber.onNext(new Pair<Method, List<MessageDb>>(Method.RETURN_ONE, lastMessage));
                        subscriber.onCompleted();
                    }
                }))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void onCompleted() {}

    @Override
    public void onError(Throwable e) {
    }

    @Override
    public void onNext(Pair<Method, List<MessageDb>> result) {
        if(result.first.isReturnAll()) {
            /** {@link PersonalMessageFragment#LastXMessageListReceived(OnLastXPersonalMessageListReceivedEvent)} **/
            MainApplication.getInstance().getBusInstance().post(new OnLastXPersonalMessageListReceivedEvent(result.second));
        }else if(result.first.isReturnOne()){
            /** {@link PersonalMessageFragment#LastMessageReceived(OnLastPersonalMessageReceivedEvent)} **/
            MainApplication.getInstance().getBusInstance().post(new OnLastPersonalMessageReceivedEvent(result.second.get(0)));
        }
    }

    public enum Method {
        RETURN_ALL,
        RETURN_ONE;

        public boolean isReturnAll(){
            return this.equals(Method.RETURN_ALL);
        }
        public boolean isReturnOne(){
            return this.equals(Method.RETURN_ONE);
        }
    }
}
