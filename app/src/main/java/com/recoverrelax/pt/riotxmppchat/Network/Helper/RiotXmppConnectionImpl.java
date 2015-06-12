package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RiotXmppConnectionHelper;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RiotXmppConnectionImpl implements RiotXmppConnectionHelper {

    private RiotXmppConnectionImplCallbacks callback;

    public RiotXmppConnectionImpl(RiotXmppConnectionImplCallbacks callback) {
        this.callback = callback;
    }

    @Override
    public void connect(final AbstractXMPPConnection connection) {
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {

                try {
                    connection.connect();
                } catch (SmackException | IOException | XMPPException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }

                if(connection.isConnected())
                    subscriber.onNext(true);

            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        if(callback != null)
                            callback.onFailedConnecting();
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if(callback != null)
                            callback.onConnected();
                    }
                });
    }

    @Override
    public void login(final AbstractXMPPConnection connection) {
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {

                try {
                    connection.login();
                } catch (SmackException | IOException | XMPPException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }

            if(connection.isConnected() && connection.isAuthenticated())
                    subscriber.onNext(true);

            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if(callback != null)
                            callback.onFailedLoggin();
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if(callback != null)
                            callback.onLoggedIn();
                    }
                });
    }

    public interface RiotXmppConnectionImplCallbacks {
        void onConnected();
        void onFailedConnecting();
        void onLoggedIn();
        void onFailedLoggin();
    }
}
