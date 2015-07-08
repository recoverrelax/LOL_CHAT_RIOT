package com.recoverrelax.pt.riotxmppchat.Network.Manager;

import com.recoverrelax.pt.riotxmppchat.EventHandling.OnConnectionLostListenerEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.OnReconnectSuccessListenerEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppRosterImpl;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.XMPPConnection;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class RiotConnectionManager implements ConnectionListener {
    private AbstractXMPPConnection connection;
    private RiotRosterManager riotRosterManager;

    public RiotConnectionManager(AbstractXMPPConnection connection, RiotRosterManager riotRosterManager) {
        this.connection = connection;
        this.riotRosterManager = riotRosterManager;

        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(connection);
        reconnectionManager.enableAutomaticReconnection();
        reconnectionManager.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.RANDOM_INCREASING_DELAY);
    }

    public void addConnectionListener(){
        if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
            this.connection.addConnectionListener(this);
        }
    }

    public Observable<AbstractXMPPConnection> getConnection() {
        return Observable.just(connection);
    }

    public Observable<String> getConnectedUser() {
        return getConnection()
                .map(connection -> AppXmppUtils.parseXmppAddress(connection.getUser()));
    }


    /**
     * ConnectionListener Interfaces
     */

    @Override public void connected(XMPPConnection connection) { }
    @Override public void authenticated(XMPPConnection connection, boolean resumed) { }
    @Override public void connectionClosed() {
        MainApplication.getInstance().getBusInstance().post(new OnConnectionLostListenerEvent());
        riotRosterManager.getFriendStatusTracker().setEnabled(false);
    }
    @Override public void connectionClosedOnError(Exception e) {
        MainApplication.getInstance().getBusInstance().post(new OnConnectionLostListenerEvent());
        riotRosterManager.getFriendStatusTracker().setEnabled(false);
    }

    @Override
    public void reconnectionSuccessful() {
        MainApplication.getInstance().getBusInstance().post(new OnReconnectSuccessListenerEvent());

        this.riotRosterManager.getFriendStatusTracker().clear();
        new RiotXmppRosterImpl().getFullFriendsList(true)
                .subscribe(new Subscriber<List<Friend>>() {
                    @Override public void onCompleted() { }
                    @Override public void onError(Throwable e) { }
                    @Override public void onNext(List<Friend> friends) {}
                });
    }

    @Override public void reconnectingIn(int seconds) { }
    @Override public void reconnectionFailed(Exception e) { }
}
