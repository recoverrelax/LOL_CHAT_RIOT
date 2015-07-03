package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import android.support.v4.util.Pair;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public class RiotXmppConnectionImpl {

    private final int MAX_LOGIN_TRIES = 3;
    private final int MAX_CONNECTION_TRIES = 4;

    public RiotXmppConnectionImpl() { }


    public Observable<AbstractXMPPConnection> connect(final AbstractXMPPConnection connection) {
        return Observable.<AbstractXMPPConnection>create(subscriber -> {
            try {
                AbstractXMPPConnection connection2 = connection.connect();
                if (connection2.isConnected()) {
                    subscriber.onNext(connection2);
                    subscriber.onCompleted();
                }
            } catch (SmackException | IOException | XMPPException e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        })
        .doOnError(throwable -> LOGI("111", "Connection OnError called"));
    }

    public Observable<AbstractXMPPConnection> connectWithRetry(final AbstractXMPPConnection connection) {
       return connect(connection)
               .retryWhen(attempts -> attempts.zipWith(Observable.range(1, MAX_CONNECTION_TRIES), (throwable, integer) -> new Pair<>(throwable, integer))
                       .flatMap(pair -> {
                           if (pair.second == MAX_LOGIN_TRIES)
                               return Observable.error(pair.first);
                           return Observable.timer(pair.second, TimeUnit.SECONDS);
                       }));
    }

    public Observable<AbstractXMPPConnection> login(final AbstractXMPPConnection connection) {
        return Observable.<AbstractXMPPConnection>create(subscriber -> {
            try {
                connection.login();
            } catch (SmackException | IOException | XMPPException e) {
                e.printStackTrace();
                subscriber.onError(e);
            }

            if (connection.isAuthenticated()) {
                subscriber.onNext(connection);
                subscriber.onCompleted();
            }
        })
                .doOnError(throwable -> {
                    LOGI("111", "Login OnError called");
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<AbstractXMPPConnection> loginWithRetry(final AbstractXMPPConnection connection) {
        return login(connection)
                .retryWhen(attempts -> attempts.zipWith(Observable.range(1, MAX_LOGIN_TRIES), (throwable, integer) -> new Pair<>(throwable, integer))
                        .flatMap(pair -> {
                            if (pair.second == MAX_LOGIN_TRIES)
                                return Observable.error(pair.first);
                            return Observable.timer(pair.second, TimeUnit.SECONDS);
                        }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }
}
