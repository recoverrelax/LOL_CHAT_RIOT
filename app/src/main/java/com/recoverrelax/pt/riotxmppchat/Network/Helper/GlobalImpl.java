package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppXmppUtils;

import org.jivesoftware.smack.AbstractXMPPConnection;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GlobalImpl {

    private String connectedXmppUser;
    private AbstractXMPPConnection connection;

    public GlobalImpl(AbstractXMPPConnection connection){
        this.connection = connection;
    }

    public Observable<String> getConnectedXmppUser(){
        return Observable.just(AppXmppUtils.parseXmppAddress(connection.getUser()))
                .cache()
                .observeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

}
