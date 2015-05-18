package com.recoverrelax.pt.riotxmppchat.Riot;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotGlobals;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotServer;
import com.recoverrelax.pt.riotxmppchat.ui.activity.LoginActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.MainActivity;

public class RiotXmppService extends Service {

    private final IBinder mBinder = new MyBinder();
    private DataStorage dataStorage;
    private MainApplication mainApplication;

    public static final String INTENT_SERVER_HOST_CONST = "server";
    public static final String INTENT_SERVER_USERNAME = "username";
    public static final String INTENT_SERVER_PASSWORD = "password";


    /**
     * Server Info
     */
    private String serverDomain = RiotGlobals.Riot_Domain;
    private int serverPort = RiotGlobals.Riot_Port;
    public static RiotXmppConnection.ConnectionAuthenticationLoader callback = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dataStorage = DataStorage.getInstance();
        mainApplication = MainApplication.getInstance();

        RiotXmppConnection xmppConnection = mainApplication.getXmppConnection();

        if(intent != null){ // Started for the first time
            Log.i("Service", "Intent != null");
            Bundle extras = intent.getExtras();
            String intentServerHost = extras.getString(INTENT_SERVER_HOST_CONST);
            String intentUsername = extras.getString(INTENT_SERVER_USERNAME);
            String intentPassword = extras.getString(INTENT_SERVER_PASSWORD);

            String serverHost = (RiotServer.getRiotServerByName(intentServerHost)).getServerHost();

            if(intentServerHost != null && intentUsername != null && intentPassword != null){
                if(xmppConnection != null && xmppConnection.isConnected() && xmppConnection.isAutheticated()){ // If there is a connection, disconnect
                    if(dataStorage.getUsername().equals(intentUsername) && dataStorage.getServer().equals(intentServerHost)){
                        if(callback != null)
                            callback.startMainActivity();
                    }else{
                        xmppConnection.disconnect();
                        connectToRiotXmppServer(serverHost, serverPort, serverDomain, intentUsername, intentPassword);
                    }
                }else
                    connectToRiotXmppServer(serverHost, serverPort, serverDomain, intentUsername, intentPassword);
            }

        }else{ // IF RESTARTED
            Log.i("Service", "Intent == null");
            // If there are saved credentials we can use
            if(dataStorage.getUsername() != null && dataStorage.getPassword() != null && dataStorage.getServer() != null){

                if(xmppConnection != null && xmppConnection.isConnected()){ // If there is a connection, disconnect
                    xmppConnection.disconnect();
                }

                String serverHost = (RiotServer.getRiotServerByName((dataStorage.getServer())).getServerHost());
                connectToRiotXmppServer(serverHost, serverPort, serverDomain, dataStorage.getUsername(), dataStorage.getPassword());
            }
        }
        return Service.START_STICKY;
    }

    public void connectToRiotXmppServer(String serverHost, int serverPort, String serverDomain,
                                        String username, String password){

        mainApplication.connectToRiotXmppServer(serverHost, serverPort, serverDomain,
                username, password, callback);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        RiotXmppService getService() {
            return RiotXmppService.this;
        }
    }
}
