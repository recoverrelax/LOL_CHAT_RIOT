package com.recoverrelax.pt.riotxmppchat.Network.RxImpl;

import android.support.v4.util.Pair;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Singleton
public class RiotXmppConnectionImpl {

    private final int MAX_LOGIN_TRIES = 3;
    private final int MAX_CONNECTION_TRIES = 4;

    @Singleton
    @Inject
    public RiotXmppConnectionImpl() {
    }

    private Observable<AbstractXMPPConnection> connect(final AbstractXMPPConnection connection) {
        return Observable.defer(() -> {
                    try {
                        AbstractXMPPConnection connect = connection.connect();
                        if (connection.isConnected())
                            return Observable.just(connect);
                        else
                            return Observable.error(new Exception("Connected but for some reason disconnected just after that"));
                    } catch (SmackException | IOException | XMPPException e) {
                        return Observable.error(e);
                    }
                }
        );
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
        return Observable.defer(() -> {
                    try {
                        connection.login();
                        if (connection.isAuthenticated()) {
                            return Observable.just(connection);
                        }
                        return Observable.error(new Exception("Logged in but for some reason not authenticated exception"));
                    } catch (SmackException | IOException | XMPPException e) {
                        return Observable.error(e);
                    }
                }
        );
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
