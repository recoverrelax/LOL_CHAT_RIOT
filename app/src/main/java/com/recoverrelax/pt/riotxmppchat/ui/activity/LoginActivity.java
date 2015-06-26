package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Login.OnConnectionOrLoginFailureEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Login.OnServiceBindedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Login.OnSuccessLoginEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotServer;
import com.squareup.otto.Subscribe;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

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

    @InjectView(R.id.login_bottom_layout)
    LinearLayout loginBottomLayout;

    @InjectView(R.id.login_base_layout)
    LinearLayout login_base_layout;

    @InjectView(R.id.login_main_layout)
    LinearLayout login_main_layout;

    private DataStorage mDataStorage;

    private boolean usernameLengthControl = false;
    private boolean passwordLengthControl = false;
    private MainApplication mainApplication;
    private MaterialDialog materialDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainApplication = MainApplication.getInstance();
        mDataStorage = DataStorage.getInstance();

        /**
         * Set initial Title Scalling to 0.7f
         */
        logo.setScaleY(0.7f);
        logo.setScaleX(0.7f);

        ObjectAnimator titleSlideUp = ObjectAnimator.ofFloat(logo, "translationY", 1000, -100, 0)
                                        .setDuration(3000);

        ObjectAnimator fadingBackground = ObjectAnimator.ofPropertyValuesHolder(login_main_layout.getBackground(),
                PropertyValuesHolder.ofInt("alpha", 0))
                .setDuration(4000);

        ObjectAnimator scalingTitleX = ObjectAnimator.ofFloat(logo, "scaleX", 0.7f, 0.8f, 0.9f, 1.0f);
        ObjectAnimator scalingTitleY = ObjectAnimator.ofFloat(logo, "scaleY", 0.7f, 0.8f, 0.9f, 1.0f);


        final ObjectAnimator credentialsFade = ObjectAnimator.ofFloat(loginBottomLayout, "alpha", 0.0f, 1.0f);
        credentialsFade.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                loginBottomLayout.setVisibility(View.VISIBLE);
            }

            @Override public void onAnimationEnd(Animator animator) { }
            @Override public void onAnimationCancel(Animator animator) { }
            @Override public void onAnimationRepeat(Animator animator) { }
        });

        final AnimatorSet animatorSetSecondPart = new AnimatorSet();
        animatorSetSecondPart.setDuration(1000);
        animatorSetSecondPart.playTogether(scalingTitleX, scalingTitleY, credentialsFade);

        titleSlideUp.start();
        fadingBackground.start();

        new Handler().postDelayed(animatorSetSecondPart::start,2000);

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

    @Override
    public int getNavigationViewPosition() {
        return -1;
    }


    @OnClick(R.id.connect)
    public void onConnectClick(View v) {
        materialDialog = new MaterialDialog.Builder(LoginActivity.this)
                .title(R.string.activity_login_progress_dialog_title)
                .content(R.string.activity_login_progress_dialog_message)
                .progress(true, 0)
                .cancelable(false)
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
//        snackBar = new SnackBar.Builder(this)
//                .withMessageId(R.string.activity_login_cannot_connect)
//                .withTextColorId(R.color.primaryColor)
//                .withDuration((short) 7000)
//                .show();
        Snackbar
                .make(getWindow().getDecorView().getRootView(), R.string.activity_login_cannot_connect, Snackbar.LENGTH_LONG).show();
    }

    @Subscribe
    public void onServiceBinded(OnServiceBindedEvent event) {
        Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        AppContextUtils.overridePendingTransitionBackAppDefault(this);
        materialDialog.dismiss();
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

        if(materialDialog != null)
            materialDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainApplication.getBusInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainApplication.getBusInstance().unregister(this);
    }
}
