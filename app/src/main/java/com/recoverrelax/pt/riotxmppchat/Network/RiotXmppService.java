package com.recoverrelax.pt.riotxmppchat.Network;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnConnectionLostListenerEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnReconnectSuccessListenerEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Login.OnConnectionOrLoginFailureEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Login.OnSuccessLoginEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.RiotXmppConnectionImpl;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotChatManager;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotConnectionManager;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotGlobals;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotServer;
import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RiotXmppConnectionHelper;
import com.recoverrelax.pt.riotxmppchat.ui.activity.LoginActivity;
import com.squareup.otto.Subscribe;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import javax.net.ssl.SSLSocketFactory;

import rx.Subscriber;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;
import static junit.framework.Assert.assertTrue;

public class RiotXmppService extends Service implements RiotXmppConnectionImpl.RiotXmppConnectionImplCallbacks {

    private static final String TAG = RiotXmppService.class.getSimpleName();
    private static final int ONGOING_SERVICE_NOTIFICATION_ID = 12345;

    private final IBinder mBinder = new MyBinder();
    private DataStorage dataStorage;

    public static final String INTENT_SERVER_HOST_CONST = "server";
    public static final String INTENT_SERVER_USERNAME = "username";
    public static final String INTENT_SERVER_PASSWORD = "password";

    private static final long DELAY_BEFORE_ROSTER_LISTENER = 500;

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

    private RiotConnectionManager riotConnectionManager;
    private RiotRosterManager riotRosterManager;
    private RiotChatManager riotChatManager;



    /**
     * Callbacks
     */

    /**
     * New Message Notification
     */

    public class MyBinder extends Binder {
        public RiotXmppService getService() {
            return RiotXmppService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


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
            onLoggedIn();
        } else {
            /**
             * USER NOT LOGGED IN
             * ---- DO LOGGED_IN
             */
            connectToRiotXmppServer(serverHost, serverPort, serverDomain, username, password);
        }

        NotificationCompat.Builder mNotificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.profile_icon_example)
                        .setContentTitle("Lol Friend's Alerter")
                        .setContentText("LOL Friend Alerter is now running.");


        Notification notification = mNotificationBuilder.build();
        startForeground(ONGOING_SERVICE_NOTIFICATION_ID, notification);

        return Service.START_STICKY;
    }

    public void stopService() {
        stopForeground(true);
        stopService(new Intent(this, RiotXmppService.class));
        this.stopSelf();
        onDestroy();
    }

    private boolean isUserAlreadyLogged(String username, String password, String serverHost) {
        /**
         * USER ALREADY LOGGED IN
         * OR
         * USER NOT LOGGED IN OR ANOTHER USER LOGGED IN
         */
        return connection != null
                && connection.isConnected()
                && connection.isAuthenticated()
                && dataStorage.getUsername().equals(username)
                && dataStorage.getPassword().equals(password)
                && dataStorage.getServer().equals(RiotServer.getRiotServerHost(serverHost).getServerName());
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

    public void onConnected() {
        login();
    }

    public void onFailedConnecting() {
        /**{@link LoginActivity#onFailure(OnConnectionOrLoginFailureEvent)} */
        Log.i("1212", "Reaches here");
        MainApplication.getInstance().getBusInstance().post(new OnConnectionOrLoginFailureEvent());
    }

    public void login() {

        assertTrue("To start a connection to the server, you must first call init() method!",
                this.connectionConfig != null || this.connection != null);

        connectionHelper.login(connection);
    }

    @Override
    public void onLoggedIn() {
        /**{@link LoginActivity#onSuccessLogin(OnSuccessLoginEvent)} */
        MainApplication.getInstance().getBusInstance().post(new OnSuccessLoginEvent());

        new Handler().postDelayed(() -> {

            MainApplication.getInstance().getConnectedUser().subscribe(new Subscriber<String>() {
                @Override public void onCompleted() { }
                @Override public void onError(Throwable e) { }

                @Override
                public void onNext(String connectedUSer) {
                    riotRosterManager = new RiotRosterManager(RiotXmppService.this, connection);
                    riotRosterManager.addRosterListener();

                    riotChatManager = new RiotChatManager(RiotXmppService.this, connection, connectedUSer, getRiotRosterManager());
                    riotChatManager.addChatListener();

                    riotConnectionManager = new RiotConnectionManager(connection);
                    riotConnectionManager.addConnectionListener();
                }
            });

        }, DELAY_BEFORE_ROSTER_LISTENER);
    }

    @Override
    public void onFailedLoggin() {
        /**{@link LoginActivity#onFailure(OnConnectionOrLoginFailureEvent)} */
        MainApplication.getInstance().getBusInstance().post(new OnConnectionOrLoginFailureEvent());
    }

    public AbstractXMPPConnection getConnection() {
        return connection;
    }

    @Override
    public void onDestroy() {
        LOGI(TAG, "Service onDestroy was called");
        if (connection != null)
            new Thread(connection::disconnect);

        connection = null;
        super.onDestroy();
    }

    @Subscribe
    public void onReconnect(OnReconnectSuccessListenerEvent event){
        getRiotRosterManager().getFriendStatusTracker().setEnabled(true);
    }

    @Subscribe
    public void onConnectionLost(OnConnectionLostListenerEvent event){
        getRiotRosterManager().getFriendStatusTracker().setEnabled(false);
    }

    public RiotRosterManager getRiotRosterManager() {
        return riotRosterManager;
    }

    public RiotChatManager getRiotChatManager() {
        return riotChatManager;
    }

    public RiotConnectionManager getRiotConnectionManager() {
        return riotConnectionManager;
    }
}
