package com.recoverrelax.pt.riotxmppchat.Network.RxImpl;

import android.support.v4.util.Pair;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils.LOGI;


public class RiotXmppConnectionImpl {

    private final int MAX_LOGIN_TRIES = 3;
    private final int MAX_CONNECTION_TRIES = 4;

    public RiotXmppConnectionImpl() { }

    /**
     * Attempt top connect with the specified connection.
     * @param connection the connection to connect to
     * @return the connect if successfully connected
     */
    private Observable<AbstractXMPPConnection> connect(final AbstractXMPPConnection connection) {
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

    /**
     * Attemps to connect with the input connection and tries at most 4 times with a delay of 1, 2, 3, 4 seconds
     * @param connection
     * @return
     */
    public Observable<AbstractXMPPConnection> connectWithRetry(final AbstractXMPPConnection connection) {
       return connect(connection)
               .retryWhen(attempts -> attempts.zipWith(Observable.range(1, MAX_CONNECTION_TRIES), (throwable, integer) -> new Pair<>(throwable, integer))
                       .flatMap(pair -> {
                           if (pair.second == MAX_LOGIN_TRIES)
                               return Observable.error(pair.first);
                           return Observable.timer(pair.second, TimeUnit.SECONDS);
                       }));
    }

    /**
     * Attempts to login with the specified connection
     * @param connection the connection to login to
     * @return the connection if successfully logged in
     */
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
                .doOnError(throwable -> LOGI("111", "Login OnError called"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    /**
     * Attepts to login to the server at most 3 times, with a delay of 1, 2, 3 seconds accordingly
     * @param connection to login to
     * @return the connection if successfully loggedIn
     */
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
