package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Login.OnConnectionOrLoginFailureEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Login.OnServiceBindedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Login.OnSuccessLoginEvent;
import com.recoverrelax.pt.riotxmppchat.R;
import com.github.mrengineer13.snackbar.SnackBar;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotServer;
import com.squareup.otto.Subscribe;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class LoginActivity extends BaseActivity {

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

    @InjectView(R.id.lol_logo)
    ImageView logo;

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
        mainApplication.getBusInstance().register(this);

        logo.setTranslationY(-1.0f);

        ScaleAnimation scaleAnimation = new ScaleAnimation(0.1f, 1f, 0.1f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        logo.setAnimation(scaleAnimation);
        scaleAnimation.setDuration(2000);
        scaleAnimation.start();

        MainApplication.getInstance().setConnectedXmppUser(null);

        checkBox.setChecked(mDataStorage.getSaveLoginCredentials());

        serverSpinner.setAdapter(new ArrayAdapter<>(LoginActivity.this, R.layout.spinner_layout, R.id.server_textview, RiotServer.getServerList()));

        if (mDataStorage.getSaveLoginCredentials()) {
            username.setText(mDataStorage.getUsername());
            password.setText(mDataStorage.getPassword());
            serverSpinner.setSelection(RiotServer.getServerPositionByName(mDataStorage.getServer()));
        } else
            connectbutton.setEnabled(false);
    }



    @OnTextChanged(R.id.username)
    public void onUsernameTextChanged(CharSequence cs) {
        usernameLengthControl = cs.toString().trim().length() > 3;
        connectbutton.setEnabled(usernameLengthControl && passwordLengthControl);
    }

    @OnTextChanged(R.id.password)
    public void onPasswordTextChanged(CharSequence cs) {
        passwordLengthControl = cs.toString().trim().length() > 3;
        connectbutton.setEnabled(usernameLengthControl && passwordLengthControl);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.activity_login;
    }

    @OnClick(R.id.connect)
    public void onConnectClick(View v) {
        materialDialog = new MaterialDialog.Builder(LoginActivity.this)
                .title(R.string.activity_login_progress_dialog_title)
                .content(R.string.activity_login_progress_dialog_message)
                .progress(true, 0)
                .show();

        mainApplication.startRiotXmppService((String) serverSpinner.getSelectedItem(), getUsername(), getPassword());
    }

    @Subscribe
    public void onSuccessLogin(OnSuccessLoginEvent event) {

        if (checkBox.isChecked())
            mDataStorage.setSaveLoginCredentials(true);
        else
            mDataStorage.setSaveLoginCredentials(false);

        mDataStorage.setUsername(getUsername());
        mDataStorage.setPassword(getPassword());
        mDataStorage.setServer(getServer());

        mainApplication.bindService();
    }

    @Subscribe
    public void onFailure(OnConnectionOrLoginFailureEvent event) {
        materialDialog.dismiss();
        snackBar = new SnackBar.Builder(this)
                .withMessageId(R.string.activity_login_cannot_connect)
                .withTextColorId(R.color.primaryColor)
                .withDuration((short) 7000)
                .show();
    }

    @Subscribe
    public void onServiceBinded(OnServiceBindedEvent event) {
        Intent intent = new Intent(LoginActivity.this, FriendListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        materialDialog.dismiss();
        MainApplication.getInstance().initSettings();
        this.finish();
    }

    public String getUsername() {
        return this.username.getText().toString();
    }

    public String getPassword() {
        return this.password.getText().toString();
    }

    public String getServer() {
        return (String) this.serverSpinner.getSelectedItem();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainApplication.getBusInstance().unregister(this);

        if(materialDialog != null)
            materialDialog.dismiss();
    }
}
