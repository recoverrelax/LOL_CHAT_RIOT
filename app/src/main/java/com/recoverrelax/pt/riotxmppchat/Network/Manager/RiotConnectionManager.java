package com.recoverrelax.pt.riotxmppchat.Network.Manager;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.OnDisconnectPublish;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.OnReconnectPublish;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppRosterImpl;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.recoverrelax.pt.riotxmppchat.Storage.BusHandler;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.XMPPConnection;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;

import static junit.framework.Assert.assertTrue;

@Singleton
public class RiotConnectionManager implements ConnectionListener {
    @Inject RiotRosterManager riotRosterManager;
    @Inject RiotXmppRosterImpl rosterImpl;
    @Inject BusHandler bus;
    @Inject DataStorage dataStorage;
    private AbstractXMPPConnection connection;

    @Singleton
    @Inject
    public RiotConnectionManager() {
    }

    public void init(AbstractXMPPConnection connection) {
        this.connection = connection;
        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(connection);
        reconnectionManager.enableAutomaticReconnection();
        reconnectionManager.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.RANDOM_INCREASING_DELAY);
    }

    public void checkConnectionInit() {
        assertTrue("Must first call init", connection != null);
    }

    public void addConnectionListener() {
        checkConnectionInit();
        if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
            this.connection.addConnectionListener(this);
        }
    }

    public Observable<AbstractXMPPConnection> getConnection() {
        checkConnectionInit();
        return Observable.just(connection);
    }

    public AbstractXMPPConnection getConnectionNO() {
        checkConnectionInit();
        return connection;
    }

    public Observable<String> getConnectedUser() {
        return getConnection()
                .map(connection -> AppXmppUtils.parseXmppAddress(connection.getUser()));
    }

    public String getConnectedUserNO() {
        return AppXmppUtils.parseXmppAddress(getConnectionNO().getUser());
    }


    /**
     * ConnectionListener Interfaces
     */

    @Override public void connected(XMPPConnection connection) {
    }

    @Override public void authenticated(XMPPConnection connection, boolean resumed) {
    }

    @Override public void connectionClosed() {
//        bus.post(new OnConnectionLostListenerEvent());
//        riotRosterManager.setEnabled(false);
        riotRosterManager.disableNotifications();
    }

    @Override public void connectionClosedOnError(Exception e) {
        bus.post(new OnDisconnectPublish());
//        riotRosterManager.setEnabled(false);
        riotRosterManager.disableNotifications();
    }

    @Override
    public void reconnectionSuccessful() {
        bus.post(new OnReconnectPublish());

        this.riotRosterManager.clearFriendList();

        int sortMode = dataStorage.getSortMode();
        boolean showOffline = dataStorage.showOfflineUsers();

        rosterImpl.getFullFriendsList(showOffline, sortMode)
                .subscribe(new Subscriber<List<Friend>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<Friend> friends) {
                        riotRosterManager.enableNotifications();
                    }
                });
    }

    @Override public void reconnectingIn(int seconds) {
    }

    @Override public void reconnectionFailed(Exception e) {
    }
}
