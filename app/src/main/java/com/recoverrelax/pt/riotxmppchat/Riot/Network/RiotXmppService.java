package com.recoverrelax.pt.riotxmppchat.Riot.Network;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotGlobals;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotServer;
import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RiotXmppConnectionHelper;
import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RiotXmppDataLoaderCallback;
import com.recoverrelax.pt.riotxmppchat.Riot.Network.Helper.RiotXmppConnectionImpl;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import javax.net.ssl.SSLSocketFactory;

import rx.Observer;

import static junit.framework.Assert.assertTrue;

public class RiotXmppService extends Service implements Observer<RiotXmppConnectionImpl.RiotXmppOperations> {

    private final IBinder mBinder = new MyBinder();
    private DataStorage dataStorage;
    private MainApplication mainApplication;

    public static final String INTENT_SERVER_HOST_CONST = "server";
    public static final String INTENT_SERVER_USERNAME = "username";
    public static final String INTENT_SERVER_PASSWORD = "password";

    public static RiotXmppDataLoaderCallback<RiotXmppConnectionImpl.RiotXmppOperations> loginActilivyCallback;

    /**
     * Server Info
     */
    private String serverHost;

    private int serverPort = RiotGlobals.RIOT_PORT;
    private String serverDomain = RiotGlobals.RIOT_DOMAIN;
    private String username;
    private String password;

    private XMPPTCPConnectionConfiguration connectionConfig;
    private AbstractXMPPConnection connection;
    private RiotXmppConnectionHelper connectionHelper;
    private Roster roster;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dataStorage = DataStorage.getInstance();

        /**
         * Get credentials from intent
         */
        if (intent != null) {
            Bundle extras = intent.getExtras();
            username = extras.getString(INTENT_SERVER_USERNAME, null);
            password = extras.getString(INTENT_SERVER_PASSWORD, null);
            serverHost = (RiotServer.getRiotServerByName(extras.getString(INTENT_SERVER_HOST_CONST, null))).getServerHost();
        } else {
            /**
             * Get credentials from SharedPreferences
             */
            username = dataStorage.getUsername();
            password = dataStorage.getPassword();
            serverHost = (RiotServer.getRiotServerByName((dataStorage.getServer())).getServerHost());
        }

        if (intent != null && isUserAlreadyLogged(username, password, serverHost)) {
            /**
             * USER LOGGED IN, JUST START THE APP
             */
            onNext(RiotXmppConnectionImpl.RiotXmppOperations.LOGGED_IN);
        } else {
            /**
             * USER NOT LOGGED IN
             * ---- DO LOGGED_IN
             */
            connectToRiotXmppServer(serverHost, serverPort, serverDomain, username, password);
        }
        return Service.START_STICKY;
    }

    private boolean isUserAlreadyLogged(String username, String password, String serverHost) {
        if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
            if (dataStorage.getUsername().equals(username) && dataStorage.getPassword().equals(password) && dataStorage.getServer().equals(RiotServer.getRiotServerHost(serverHost).getServerName())) {
                /**
                 * USER ALREADY LOGGED IN
                 */
                return true;
            } else {
                /**
                 * USER NOT LOGGED IN OR ANOTHER USER LOGGED IN
                 */
                return false;
            }
        } else
            return false;
    }

    public void connectToRiotXmppServer(String serverHost, int serverPort, String serverDomain,
                                        String username, String password) {

        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.serverDomain = serverDomain;

        this.username = username;
        this.password = password;

        prepareConnectionConfig(serverDomain, serverHost, serverPort);
        connection = new XMPPTCPConnection(connectionConfig);
        connectionHelper = new RiotXmppConnectionImpl(this);

        connect();
    }

    public void prepareConnectionConfig(String serverDomain, String serverHost, int serverPort) {
        this.connectionConfig = XMPPTCPConnectionConfiguration.builder()
                .setSocketFactory(SSLSocketFactory.getDefault())
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setServiceName(serverDomain)
                .setHost(serverHost)
                .setUsernameAndPassword(username, "AIR_" + password)
                .setPort(serverPort)
                .build();
    }

    public void connect() {

        assertTrue("To start a connection to the server, you must first call init() method!",
                this.connectionConfig != null);

        connectionHelper.connect(connection);
    }

    public void login() {

        assertTrue("To start a connection to the server, you must first call init() method!",
                this.connectionConfig != null || this.connection != null);

        connectionHelper.login(connection);
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        if (loginActilivyCallback != null) {
            loginActilivyCallback.onFailure(e);
        }
    }

    @Override
    public void onNext(RiotXmppConnectionImpl.RiotXmppOperations result) {
        switch (result) {
            case CONNECTED:
                login();
                break;
            case LOGGED_IN:
                if (loginActilivyCallback != null) {
                    loginActilivyCallback.onSuccess(result);
                }
                break;
        }
    }

    public void addRosterListener(RosterListener listener) {
        if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
            this.roster = Roster.getInstanceFor(connection);
            roster.addRosterListener(listener);
        }
    }
    public void removeRosterListener(RosterListener listener) {
        if (roster != null && listener != null) {
            roster.removeRosterListener(listener);
        }
    }

    public AbstractXMPPConnection getConnection() {
        return connection;
    }

    @Override
    public void onDestroy() {
        if (connection != null)
            connection.disconnect();
        connection = null;
        super.onDestroy();
    }

    public class MyBinder extends Binder {
        public RiotXmppService getService() {
            return RiotXmppService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
