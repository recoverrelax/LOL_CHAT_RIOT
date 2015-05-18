package com.recoverrelax.pt.riotxmppchat;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import com.edgelabs.pt.mybaseapp.R;
import com.recoverrelax.pt.riotxmppchat.MyUtil.storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Riot.RiotXmppConnection;
import com.recoverrelax.pt.riotxmppchat.Riot.RiotXmppConnection.ConnectionAuthenticationLoader;
import com.recoverrelax.pt.riotxmppchat.Riot.RiotXmppService;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MainApplication extends Application {
    private static final String TAG = MainApplication.class.getSimpleName();

    private static MainApplication instance;
    private RiotXmppConnection xmppConnection;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        DataStorage.init(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Roboto-Light.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }

    public void connectToRiotXmppServer(String serverHost, int serverPort, String serverDomain,
                                         String username, String password, ConnectionAuthenticationLoader cb){
        xmppConnection = new RiotXmppConnection(serverHost, serverPort, serverDomain,
                username, password);

        xmppConnection.init((Activity) cb, cb);
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
