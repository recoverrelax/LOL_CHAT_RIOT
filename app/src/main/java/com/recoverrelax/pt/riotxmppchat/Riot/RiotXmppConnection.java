package com.recoverrelax.pt.riotxmppchat.Riot;

import android.app.Activity;
import android.os.AsyncTask;

import com.edgelabs.pt.mybaseapp.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotGlobals;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotServer;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

import javax.net.ssl.SSLSocketFactory;

import rx.Subscription;

import static junit.framework.Assert.assertTrue;

public class RiotXmppConnection {

    private static final String TAG = "RiotXmppConnection";

    private String serverHost = RiotServer.EUW.serverHost;
    private int serverPort = RiotGlobals.RIOT_PORT;
    private String serverDomain = RiotGlobals.RIOT_DOMAIN;
    private XMPPTCPConnectionConfiguration connectionConfig;
    private AbstractXMPPConnection connection;

    private String username;
    private String password;

    private ConnectionAuthenticationLoader callback;
    private Activity activity;
    private Subscription mSubscription;

    public RiotXmppConnection(String serverHost, int serverPort, String serverDomain, String username, String password) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.serverDomain = serverDomain;

        this.username = username;
        this.password = password;
    }

    public void init() {
        prepareConnectionConfig(serverDomain, serverHost, serverPort);
        connection = new XMPPTCPConnection(connectionConfig);
    }

    public void init(Activity activity, ConnectionAuthenticationLoader callback) {
        this.activity = activity;
        this.callback = callback;
        init();
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

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    connection.connect();
                } catch (SmackException | IOException | XMPPException e) {
                    e.printStackTrace();
                }
                return connection.isConnected();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

                if (aBoolean) {
                    if(callback != null)
                        callback.onConnect();
                    else
                        login();
                } else {
                    if(callback != null)
                        callback.onError(R.string.activity_login_cannot_connect);
                    /**
                     * TODO: restart the service here in xxx seconds
                     */
                }

            }
        }.execute();
    }

    public void login() {

        assertTrue("To start a connection to the server, you must first call init() method!",
                this.connectionConfig != null || this.connection != null);

        if (connection.isConnected()) {

            new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected void onPreExecute() {}

                @Override
                protected Boolean doInBackground(Void... voids) {
                    try {
                        connection.login();
                    } catch (SmackException | IOException | XMPPException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return connection.isConnected() && connection.isAuthenticated();
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {

                    if (aBoolean) {
                        if(callback != null)
                            callback.onLogin();
                    } else {
                        if(callback != null)
                            callback.onError(R.string.activity_login_cannot_connect);
                        /**
                         * TODO: restart service in xxx seconds
                         */
                    }
                }
            }.execute();
        }
    }



    public boolean isConnected(){
        return connection != null && connection.isConnected();
    }

    public boolean isAutheticated(){
        return isConnected() && connection.isAuthenticated();
    }

    public void disconnect(){
        connection.disconnect();
    }

    public AbstractXMPPConnection getConnection() {
        return connection;
    }

    public interface ConnectionAuthenticationLoader {
        void onConnect();

        void onError(int stringResourceId);

        void onLogin();
        void startMainActivity();
    }
}
