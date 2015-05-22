package com.recoverrelax.pt.riotxmppchat.Riot.Network.Helper;

import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RiotXmppConnectionHelper;
import com.recoverrelax.pt.riotxmppchat.Riot.Network.RiotXmppService;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RiotXmppConnectionImpl implements RiotXmppConnectionHelper {

    private Observer<RiotXmppConnectionImpl.RiotXmppOperations> observer;
    private Subscription mSubscription;

    public RiotXmppConnectionImpl(RiotXmppService callback) {
        observer = callback;
    }

    @Override
    public void connect(final AbstractXMPPConnection connection) {
        mSubscription = Observable.create(new Observable.OnSubscribe<RiotXmppConnectionImpl.RiotXmppOperations>() {
            @Override
            public void call(Subscriber<? super RiotXmppOperations> subscriber) {

                try {
                    connection.connect();
                } catch (SmackException | IOException | XMPPException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }

                if(connection.isConnected())
                    subscriber.onNext(RiotXmppOperations.CONNECTED);

            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void login(final AbstractXMPPConnection connection) {
        mSubscription = Observable.create(new Observable.OnSubscribe<RiotXmppConnectionImpl.RiotXmppOperations>() {
            @Override
            public void call(Subscriber<? super RiotXmppOperations> subscriber) {

                try {
                    connection.login();
                } catch (SmackException | IOException | XMPPException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }

            if(connection.isConnected() && connection.isAuthenticated())
                    subscriber.onNext(RiotXmppOperations.LOGGED_IN);

            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public enum RiotXmppOperations{
        LOGGED_IN,
        CONNECTED;
    }
}
