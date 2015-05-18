package com.recoverrelax.pt.riotxmppchat;

import android.app.Activity;
import android.app.Application;

import com.recoverrelax.pt.riotxmppchat.MyUtil.storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Riot.RiotXmppConnection;
import com.recoverrelax.pt.riotxmppchat.Riot.RiotXmppConnection.ConnectionAuthenticationLoader;

public class MainApplication extends Application {
    private static final String TAG = MainApplication.class.getSimpleName();

    private static MainApplication instance;
    private RiotXmppConnection xmppConnection;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        DataStorage.init(this);
    }

    public void connectToRiotXmppServer(String serverHost, int serverPort, String serverDomain,
                                         String username, String password, ConnectionAuthenticationLoader cb){
        xmppConnection = new RiotXmppConnection(serverHost, serverPort, serverDomain,
                username, password);

        xmppConnection.init((Activity)cb, cb);
        xmppConnection.connect();
    }

    public void login() {
        xmppConnection.login();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static MainApplication getInstance() {
        return instance;
    }

    public RiotXmppConnection getXmppConnection() {
        return xmppConnection;
    }
}
