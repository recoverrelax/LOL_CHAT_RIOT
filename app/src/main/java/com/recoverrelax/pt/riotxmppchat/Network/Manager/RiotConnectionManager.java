package com.recoverrelax.pt.riotxmppchat.Network.Manager;

import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnReconnectListener;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.XMPPConnection;

public class RiotConnectionManager implements ConnectionListener {
    private AbstractXMPPConnection connection;

    public RiotConnectionManager(AbstractXMPPConnection connection){
        this.connection = connection;

        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(connection);
        reconnectionManager.enableAutomaticReconnection();
        reconnectionManager.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.RANDOM_INCREASING_DELAY);
    }

    public void addConnectionListener(){
        if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
            this.connection.addConnectionListener(this);
        }
    }


    /**
     * ConnectionListener Interfaces
     */

    @Override public void connected(XMPPConnection connection) { }
    @Override public void authenticated(XMPPConnection connection, boolean resumed) { }
    @Override public void connectionClosed() { }
    @Override public void connectionClosedOnError(Exception e) { }

    @Override
    public void reconnectionSuccessful() {
        MainApplication.getInstance().getBusInstance().post(new OnReconnectListener());
    }

    @Override public void reconnectingIn(int seconds) { }
    @Override public void reconnectionFailed(Exception e) { }
}
