package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.edgelabs.pt.mybaseapp.R;
import com.github.mrengineer13.snackbar.SnackBar;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.drawer.ENavDrawer;
import com.recoverrelax.pt.riotxmppchat.MyUtil.storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotGlobals;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotServer;
import com.recoverrelax.pt.riotxmppchat.Riot.RiotXmppConnection;
import com.recoverrelax.pt.riotxmppchat.Riot.RiotXmppService;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static junit.framework.Assert.assertTrue;

public class LoginActivity extends BaseActivity implements RiotXmppConnection.ConnectionAuthenticationLoader {

    private final String TAG = "LoginActivity";

    @InjectView(R.id.username)
    EditText username;

    @InjectView(R.id.password)
    EditText password;

    @InjectView(R.id.serverSpinner)
    Spinner serverSpinner;

    @InjectView(R.id.connect)
    Button connectbutton;

    @InjectView(R.id.checkBox)
    CheckBox checkBox;

    private DataStorage mDataStorage;
    private SnackBar snackBar;

    private boolean usernameLengthControl = false;
    private boolean passwordLengthControl = false;
    private MainApplication mainApplication;
    private MaterialDialog materialDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainApplication = MainApplication.getInstance();
        mDataStorage = DataStorage.getInstance();

        checkBox.setChecked(mDataStorage.getSaveLoginCredentials());

        serverSpinner.setAdapter(new ArrayAdapter<>(LoginActivity.this, R.layout.spinner_layout, R.id.server_textview, RiotServer.getServerList()));

        if(mDataStorage.getSaveLoginCredentials()){
            username.setText(mDataStorage.getUsername());
            password.setText(mDataStorage.getPassword());
            serverSpinner.setSelection(RiotServer.getServerPositionByName(mDataStorage.getServer()));
        }else
            connectbutton.setEnabled(false);
    }

    @OnTextChanged(R.id.username)
    public void onUsernameTextChanged(CharSequence cs){
        usernameLengthControl = cs.toString().trim().length() > 3;
        connectbutton.setEnabled(usernameLengthControl && passwordLengthControl);
    }

    @OnTextChanged(R.id.password)
    public void onPasswordTextChanged(CharSequence cs){
        passwordLengthControl = cs.toString().trim().length() > 3;
        connectbutton.setEnabled(usernameLengthControl && passwordLengthControl);
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
//        RiotServer riotServerByName = RiotServer.getRiotServerByName((String) serverSpinner.getSelectedItem());

//        assertTrue("No server found with such name!", riotServerByName != null);
//        String serverHost = riotServerByName.getServerHost();

        int serverPort = RiotGlobals.RIOT_PORT;
        String serverDomain = RiotGlobals.RIOT_DOMAIN;

        materialDialog = new MaterialDialog.Builder(LoginActivity.this)
                .title(R.string.activity_login_progress_dialog_title)
                .content(R.string.activity_login_progress_dialog_message)
                .progress(true, 0)
                .show();

//        mainApplication.connectToRiotXmppServer(serverHost, serverPort, serverDomain, getUsername(), getPassword(), this);

        Intent service = new Intent(LoginActivity.this, RiotXmppService.class);
        service.putExtra(RiotXmppService.INTENT_SERVER_HOST_CONST, (String) serverSpinner.getSelectedItem());
        service.putExtra(RiotXmppService.INTENT_SERVER_USERNAME, getUsername());
        service.putExtra(RiotXmppService.INTENT_SERVER_PASSWORD, getPassword());
        RiotXmppService.callback = this;

        startService(service);
    }

    private void Login() {
        mainApplication.login();
    }

    @Override
    public void onConnect() {
             Login();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RiotXmppService.callback = null;
        materialDialog.dismiss();
    }

    @Override
    public void onError(int stringResourceId) {
        materialDialog.dismiss();
        snackBar = new SnackBar.Builder(this)
                .withMessageId(stringResourceId)
                .withTextColorId(R.color.primaryColor)
                .withDuration((short) 7000)
                .show();
    }

    @Override
    public void onLogin() {

        if(checkBox.isChecked())
            mDataStorage.setSaveLoginCredentials(true);
        else
            mDataStorage.setSaveLoginCredentials(false);

        mDataStorage.setUsername(getUsername());
        mDataStorage.setPassword(getPassword());
        mDataStorage.setServer(getServer());

        startMainActivity();
        materialDialog.dismiss();
        this.finish();
    }

    @Override
    public void startMainActivity(){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    public String getUsername(){
        return this.username.getText().toString();
    }

    public String getPassword(){
        return this.password.getText().toString();
    }

    public String getServer(){
        return (String) this.serverSpinner.getSelectedItem();
    }
}
