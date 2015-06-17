package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import android.support.v4.util.Pair;
import android.util.Log;

import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RiotXmppConnectionHelper;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public class RiotXmppConnectionImpl implements RiotXmppConnectionHelper {

    private RiotXmppConnectionImplCallbacks callback;
    private final int MAX_LOGIN_TRIES = 5;
    private final int MAX_CONNECTION_TRIES = 10;

    public RiotXmppConnectionImpl(RiotXmppConnectionImplCallbacks callback) {
        this.callback = callback;
    }


    @Override
    public void connect(final AbstractXMPPConnection connection) {
        Observable.<AbstractXMPPConnection>create(subscriber -> {
            try {
                connection.connect();
                subscriber.onNext(connection);
            } catch (SmackException | IOException | XMPPException e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        })
                .retryWhen(attempts -> attempts.zipWith(Observable.range(1, MAX_CONNECTION_TRIES), (throwable, integer) -> new Pair<>(throwable, integer))
                        .flatMap(pair -> {
                            if(pair.second == MAX_CONNECTION_TRIES)
                                return Observable.error(pair.first);
                            return Observable.timer(pair.second, TimeUnit.SECONDS);
                        }))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AbstractXMPPConnection>() {
                    @Override public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        if (callback != null)
                            callback.onFailedConnecting();
                    }

                    @Override
                    public void onNext(AbstractXMPPConnection conn) {
                        if (callback != null)
                            callback.onConnected();
                    }
                });
    }
    @Override
    public void login(final AbstractXMPPConnection connection) {
        Observable.<AbstractXMPPConnection>create(subscriber -> {
            try {
                connection.login();
                subscriber.onNext(connection);
            } catch (SmackException | IOException | XMPPException e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        })
                .retryWhen(attempts -> attempts.zipWith(Observable.range(1, MAX_LOGIN_TRIES), (throwable, integer) -> new Pair<>(throwable, integer))
                        .flatMap(pair -> {
                            if (pair.second == MAX_LOGIN_TRIES)
                                return Observable.error(pair.first);
                            return Observable.timer(pair.second, TimeUnit.SECONDS);
                        }))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AbstractXMPPConnection>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (callback != null)
                            callback.onFailedLoggin();
                    }

                    @Override
                    public void onNext(AbstractXMPPConnection connection) {
                        if (callback != null)
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
