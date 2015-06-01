package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import android.support.v4.app.Fragment;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
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

public class PersonalMessageImpl implements PersonalMessageHelper, Observer<List<MessageDb>>{

    private Subscription mSubscription;
    private Fragment mFragment;

    private String TAG = this.getClass().getSimpleName();

    public PersonalMessageImpl(Fragment frag) {
        mFragment = frag;
    }

    @Override
    public void getLastXPersonalMessageList(final int x, final String connectedUser, final String userToGetMessagesFrom) {
        mSubscription = AppObservable.bindFragment(mFragment,
                Observable.create(new Observable.OnSubscribe<List<MessageDb>>() {
                    @Override
                    public void call(Subscriber<? super List<MessageDb>> subscriber) {

                        List<MessageDb> messageList = RiotXmppDBRepository.getLastXMessages(x, connectedUser, userToGetMessagesFrom);
                        Collections.reverse(messageList);

                        subscriber.onNext(messageList);
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
    public void onNext(List<MessageDb> messageDbs) {
        /** {@link PersonalMessageFragment#LastXMessageListReceived(OnLastXPersonalMessageListReceivedEvent)} **/
        MainApplication.getInstance().getBusInstance().post(new OnLastXPersonalMessageListReceivedEvent(messageDbs));
    }
}
