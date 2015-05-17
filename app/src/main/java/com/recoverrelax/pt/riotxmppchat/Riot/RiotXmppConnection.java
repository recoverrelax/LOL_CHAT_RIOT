package com.recoverrelax.pt.riotxmppchat.Riot;

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

import static junit.framework.Assert.assertTrue;

public class RiotXmppConnection {

    private static final String TAG = "RiotXmppConnection";

    private String serverHost = RiotServer.EUW.serverHost;
    private int serverPort = RiotGlobals.Riot_Port;
    private String serverDomain = RiotGlobals.Riot_Domain;
    private XMPPTCPConnectionConfiguration connectionConfig;
    private AbstractXMPPConnection connection;
    private RiotXmppConnectionCallbacks callback;

    private String username;
    private String password;

    public RiotXmppConnection(String serverHost, int serverPort, String serverDomain, String username, String password) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.serverDomain = serverDomain;

        this.username = username;
        this.password = password;
    }

    public void init(RiotXmppConnectionCallbacks callback) {
        this.callback = callback;
        prepareConnectionConfig(serverDomain, serverHost, serverPort);
        connection = new XMPPTCPConnection(connectionConfig);
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
                this.connectionConfig != null || this.connection != null);

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                assertTrue("Must call init() First", callback != null);
                callback.showProgressBar(true);
            }

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
                assertTrue("Must call init() First", callback != null);

                if (aBoolean) {
                    callback.onConnect();
                } else {
                    callback.onError(R.string.activity_login_cannot_connect);
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
                protected void onPreExecute() {
                    assertTrue("Must call init() First", callback != null);
                }

                @Override
                protected Boolean doInBackground(Void... voids) {
                    try {
                        connection.login();
                    } catch (SmackException | IOException | XMPPException e) {
                        e.printStackTrace();
                    }
                    return connection.isConnected() && connection.isAuthenticated();
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    assertTrue("Must call init() First", callback != null);

                    if (aBoolean) {
                        callback.onLogin();
                    } else {
                        callback.onError(R.string.activity_login_cannot_login);
                    }
                }
            }.execute();
        }
    }

    public interface RiotXmppConnectionCallbacks {
        void showProgressBar(boolean state);

        void onConnect();

        void onError(int stringResourceId);

        void onLogin();
    }


}
