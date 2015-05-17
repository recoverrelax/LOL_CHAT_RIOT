package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.edgelabs.pt.mybaseapp.R;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.github.mrengineer13.snackbar.SnackBar;
import com.recoverrelax.pt.riotxmppchat.MyUtil.drawer.ENavDrawer;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotGlobals;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotServer;
import com.recoverrelax.pt.riotxmppchat.Riot.RiotXmppConnection;

import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements RiotXmppConnection.RiotXmppConnectionCallbacks{

    private final String TAG = "LoginActivity";

    private RiotXmppConnection xmppConnection;

    @InjectView(R.id.username)
    EditText username;

    @InjectView(R.id.password)
    EditText password;

    @InjectView(R.id.progressBarCircularIndeterminate)
    ProgressBarCircularIndeterminate progressBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgressBar(false);
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

        String username = this.username.getText().toString();
        String password = this.password.getText().toString();

        xmppConnection = new RiotXmppConnection(serverHost, serverPort, serverDomain,
                username, password);
        xmppConnection.init(this);
        xmppConnection.connect();

    }

    private void Login() {
        xmppConnection.login();
    }

    @Override
    public void showProgressBar(boolean state) {
        progressBar.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onConnect() {
             Login();
    }

    @Override
    public void onError(int stringResourceId) {
        new SnackBar.Builder(this)
                .withMessageId(stringResourceId)
                .withTextColorId(R.color.primaryColor)
                .withDuration((short) 1000)
                .show();
    }

    @Override
    public void onLogin() {
        showProgressBar(false);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        this.finish();
    }
}
