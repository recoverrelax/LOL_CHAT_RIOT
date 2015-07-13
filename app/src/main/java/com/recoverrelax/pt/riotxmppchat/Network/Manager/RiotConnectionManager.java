package com.recoverrelax.pt.riotxmppchat.Network.Manager;

import com.recoverrelax.pt.riotxmppchat.EventHandling.OnConnectionLostListenerEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.OnReconnectSuccessListenerEvent;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppRosterImpl;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.squareup.otto.Bus;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.XMPPConnection;

import java.util.List;

import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;

import static junit.framework.Assert.assertTrue;

public class RiotConnectionManager implements ConnectionListener {
    private RiotRosterManager riotRosterManager;
    private AbstractXMPPConnection connection;
    private RiotXmppRosterImpl rosterImpl;
    private Bus bus;

    @Inject
    public RiotConnectionManager(Bus bus, RiotRosterManager riotRosterManager, RiotXmppRosterImpl rosterImpl) {
        this.riotRosterManager = riotRosterManager;
        this.rosterImpl = rosterImpl;
        this.bus = bus;
    }

    public void init(AbstractXMPPConnection connection){
        this.connection = connection;
        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(connection);
        reconnectionManager.enableAutomaticReconnection();
        reconnectionManager.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.RANDOM_INCREASING_DELAY);
    }

    public void checkConnectionInit(){
        assertTrue("Must first call init", connection != null);
    }

    public void addConnectionListener(){
        checkConnectionInit();
        if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
            this.connection.addConnectionListener(this);
        }
    }

    public Observable<AbstractXMPPConnection> getConnection() {
        checkConnectionInit();
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
        bus.post(new OnConnectionLostListenerEvent());
        riotRosterManager.setEnabled(false);
    }
    @Override public void connectionClosedOnError(Exception e) {
        bus.post(new OnConnectionLostListenerEvent());
        riotRosterManager.setEnabled(false);
    }

    @Override
    public void reconnectionSuccessful() {
        bus.post(new OnReconnectSuccessListenerEvent());

        this.riotRosterManager.clear();
        /**
         * TODO: Without this, notifications are not being sent because the Presence Collection
         * is not innitialized
         */

        rosterImpl.getFullFriendsList(true)
                .subscribe(new Subscriber<List<Friend>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<Friend> friends) {
                    }
                });
    }

    @Override public void reconnectingIn(int seconds) { }
    @Override public void reconnectionFailed(Exception e) { }
}
