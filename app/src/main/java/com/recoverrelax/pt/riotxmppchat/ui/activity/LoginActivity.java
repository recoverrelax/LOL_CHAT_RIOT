package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.edgelabs.pt.mybaseapp.R;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.recoverrelax.pt.riotxmppchat.MyUtil.drawer.ENavDrawer;
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

import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    private final String TAG = "LoginActivity";

    @InjectView(R.id.progressBarCircularIndeterminate)
    ProgressBarCircularIndeterminate progressBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgresBar(false);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.activity_login;
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return ENavDrawer.NAVDRAWER_NO_DRAWER.getNavDrawerId();
    }

    @OnClick(R.id.connect)
    public void onConnectClick(View v) {

        String serverHost = RiotServer.EUW.serverHost;
        int serverPort = RiotGlobals.Riot_Port;
        String serverDomain = RiotGlobals.Riot_Domain;

        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setSocketFactory(SSLSocketFactory.getDefault())
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setServiceName(serverDomain)
                .setHost(serverHost)
   
                .setPort(serverPort)
                .build();
//
        final AbstractXMPPConnection connection = new XMPPTCPConnection(config);

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                showProgresBar(true);
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    connection.connect();
                    connection.login();
                } catch (SmackException | IOException | XMPPException e) {
                    e.printStackTrace();
                }
                return connection.isConnected() && connection.isAuthenticated();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if(aBoolean)
                    onLogin();
            }
        }.execute();

//        ConnectionConfiguration connConf = new ConnectionConfiguration(serverHost, serverPort, serverDomain);
//        connConf.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
//        connConf.setSocketFactory(SSLSocketFactory.getDefault());
    }

    private void onLogin() {
        showProgresBar(false);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    public void showProgresBar(boolean state){
        progressBar.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
    }
}
