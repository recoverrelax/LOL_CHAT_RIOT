package com.recoverrelax.pt.riotxmppchat.Network;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.recoverrelax.pt.riotxmppchat.EventHandling.OnConnectionFailureEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.OnLoginFailureEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.OnSuccessLoginEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppConnectionImpl;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotChatManager;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotConnectionManager;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotGlobals;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotServer;
import com.recoverrelax.pt.riotxmppchat.ui.activity.LoginActivity;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.net.ssl.SSLSocketFactory;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils.LOGI;
import static junit.framework.Assert.assertTrue;

public class RiotXmppService extends Service {

    private static final String TAG = RiotXmppService.class.getSimpleName();
    private static final int ONGOING_SERVICE_NOTIFICATION_ID = 12345;

    private final IBinder mBinder = new MyBinder();

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

    @Inject RiotXmppConnectionImpl connectionHelper;
    @Inject DataStorage dataStorage;
    private RiotConnectionManager connectionManager;

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
        MainApplication.getInstance().getAppComponent().inject(this);
//        MainApplication.getInstance().getStorageComponent().inject(this);

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
            onLoggedIn(connection);
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

        connectionHelper.connectWithRetry(connection)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<AbstractXMPPConnection>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LOGI(TAG, "ConnectionHelper Connection onError\n");

                        /**{@link LoginActivity#onConnectionFailure(OnConnectionFailureEvent)} */
                        MainApplication.getInstance().getBusInstance().post(new OnConnectionFailureEvent());
                    }

                    @Override
                    public void onNext(AbstractXMPPConnection connection) {
                        LOGI(TAG, "ConnectionHelper Connection onNext");
                        onConnected();
                    }
                });
    }

    public void onConnected() {
        login();
    }

    public void login() {

        assertTrue("To start a connection to the server, you must first call init() method!",
                this.connectionConfig != null || this.connection != null);

        connectionHelper.loginWithRetry(connection)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<AbstractXMPPConnection>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LOGI(TAG, "ConnectionHelper Login onError\n");
                        LOGI(TAG, e.toString());

                        /**{@link LoginActivity#onLoginFailure(OnLoginFailureEvent)} */
                        MainApplication.getInstance().getBusInstance().post(new OnLoginFailureEvent());
                    }

                    @Override
                    public void onNext(AbstractXMPPConnection connection) {
                        LOGI(TAG, "ConnectionHelper Login onNext");
                        onLoggedIn(connection);
                    }
                });
    }

    public void onLoggedIn(AbstractXMPPConnection connection) {
        LOGI(TAG, "onLoggedIn entered");

        Observable.timer(DELAY_BEFORE_ROSTER_LISTENER, TimeUnit.MILLISECONDS)
                .flatMap(aLong -> createListeners(connection))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Boolean>() {
                    @Override public void onCompleted() {
                        LOGI(TAG, "onLoggedIn onCompleted\n");
                    }
                    @Override public void onError(Throwable e) {
                        LOGI(TAG, "Error Creating Chat, Roster, and Connection Listeners\n");
                        LOGI(TAG, e.toString());
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        LOGI(TAG, "onLoggedIn onNext\n");
                        if(aBoolean)
                        /**{@link LoginActivity#onSuccessLogin(OnSuccessLoginEvent)} */
                            MainApplication.getInstance().getBusInstance().post(new OnSuccessLoginEvent());
                    }
                });
    }

    public Observable<Boolean> createListeners(AbstractXMPPConnection connection){
        LOGI(TAG, "Enters createListeners\n");
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    LOGI(TAG, "Enters createListeners try \n");
                    String connectedUser = AppXmppUtils.parseXmppAddress(connection.getUser());

                    riotRosterManager = new RiotRosterManager(RiotXmppService.this, connection);
                    riotRosterManager.addRosterListener();

                    riotChatManager = new RiotChatManager(RiotXmppService.this, connection, connectedUser, getRiotRosterManager());
                    riotChatManager.addChatListener();

                    connectionManager = new RiotConnectionManager(connection, riotRosterManager);
                    connectionManager.addConnectionListener();

                    subscriber.onNext(true);
                    subscriber.onCompleted();
                }catch(Exception e){
                    LOGI(TAG, "Enters createListeners catch \n");
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        LOGI(TAG, "Service onDestroy was called");
        if (connection != null)
            new Thread(connection::disconnect);

        connection = null;
        super.onDestroy();
    }

    public RiotRosterManager getRiotRosterManager() {
        return riotRosterManager;
    }

    public RiotChatManager getRiotChatManager() {
        return riotChatManager;
    }

    public RiotConnectionManager getRiotConnectionManager() {
        return connectionManager;
    }
}
