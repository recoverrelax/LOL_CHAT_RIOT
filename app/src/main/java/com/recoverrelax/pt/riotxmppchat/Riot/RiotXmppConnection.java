package com.recoverrelax.pt.riotxmppchat.Riot;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.edgelabs.pt.mybaseapp.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotGlobals;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotServer;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.net.ssl.SSLSocketFactory;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

import static junit.framework.Assert.assertTrue;

public class RiotXmppConnection {

    private static final String TAG = "RiotXmppConnection";

    private String serverHost = RiotServer.EUW.serverHost;
    private int serverPort = RiotGlobals.Riot_Port;
    private String serverDomain = RiotGlobals.Riot_Domain;
    private XMPPTCPConnectionConfiguration connectionConfig;
    private AbstractXMPPConnection connection;

    private String username;
    private String password;

    private MaterialDialog materialDialog;
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

    public void init(Activity activity, ConnectionAuthenticationLoader callback) {
        this.activity = activity;
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

        materialDialog = new MaterialDialog.Builder(activity)
                .title(R.string.activity_login_progress_dialog_title)
                .content(R.string.activity_login_progress_dialog_message)
                .progress(true, 0)
                .show();

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                assertTrue("Must call init() First", callback != null);

                materialDialog = new MaterialDialog.Builder((AppCompatActivity) callback)
                        .title(R.string.activity_login_progress_dialog_title)
                        .content(R.string.activity_login_progress_dialog_message)
                        .progress(true, 0)
                        .show();
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
                    materialDialog.dismiss();
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
                        return false;
                    }
                    return connection.isConnected() && connection.isAuthenticated();
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    assertTrue("Must call init() First", callback != null);
                    materialDialog.dismiss();

                    if (aBoolean) {
                        callback.onLogin();
                    } else {
                        callback.onError(R.string.activity_login_cannot_connect);
                    }
                }
            }.execute();
        }
    }

    public ArrayList<Friend> getFriendsList(){
        Roster roster = Roster.getInstanceFor(connection);
        Collection<RosterEntry> entries = roster.getEntries();

        ArrayList<Friend> friendList = new ArrayList<>();

        for (RosterEntry entry : entries) {
            friendList.add(new Friend(entry.getName()));
        }
        return friendList;
    }


    public interface ConnectionAuthenticationLoader {
        void onConnect();

        void onError(int stringResourceId);

        void onLogin();
    }
}
