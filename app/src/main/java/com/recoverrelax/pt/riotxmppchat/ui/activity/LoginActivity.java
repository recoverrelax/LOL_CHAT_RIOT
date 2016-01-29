//package com.recoverrelax.pt.riotxmppchat.ui.activity;
//
//import android.animation.Animator;
//import android.animation.AnimatorSet;
//import android.animation.ObjectAnimator;
//import android.animation.PropertyValuesHolder;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.design.widget.Snackbar;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.Spinner;
//
//import com.afollestad.materialdialogs.MaterialDialog;
//import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.OnConnectionFailurePublish;
//import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.OnLoginFailurePublish;
//import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.OnServiceBindedPublish;
//import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.OnSuccessLoginPublish;
//import com.recoverrelax.pt.riotxmppchat.MainApplication;
//import com.recoverrelax.pt.riotxmppchat.MyUtil.AppContextUtils;
//import com.recoverrelax.pt.riotxmppchat.R;
//import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotServer;
//import com.recoverrelax.pt.riotxmppchat.Storage.BusHandler;
//import com.squareup.otto.Subscribe;
//
//import javax.inject.Inject;
//
//import butterknife.Bind;
//import butterknife.OnClick;
//import butterknife.OnTextChanged;
//
//public class LoginActivity extends BaseActivity {
//
//    private final String TAG = "LoginActivity";
//
//    @Bind(R.id.username) EditText username;
//    @Bind(R.id.password) EditText password;
//    @Bind(R.id.serverSpinner) Spinner serverSpinner;
//    @Bind(R.id.connect) Button connectbutton;
//    @Bind(R.id.checkBox) CheckBox checkBox;
//    @Bind(R.id.lol_logo) ImageView logo;
//    @Bind(R.id.login_bottom_layout) LinearLayout loginBottomLayout;
//    @Bind(R.id.login_base_layout) LinearLayout login_base_layout;
//    @Bind(R.id.login_main_layout) LinearLayout login_main_layout;
//    @Inject MainApplication mainApplication;
//    @Inject BusHandler bus;
//
//    private boolean usernameLengthControl = false;
//    private boolean passwordLengthControl = false;
//    private MaterialDialog materialDialog;
//
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        MainApplication.getInstance().getAppComponent().inject(this);
//
//        doLoginAnimations();
//
//        checkBox.setChecked(mDataStorage.getSaveLoginCredentials());
//
//        serverSpinner.setAdapter(new ArrayAdapter<>(LoginActivity.this, R.layout.spinner_layout, R.id.server_textview, RiotServer.getServerList()));
//
//        if (mDataStorage.getSaveLoginCredentials()) {
//            username.setText(mDataStorage.getUsername());
//            password.setText(mDataStorage.getPassword());
//            serverSpinner.setSelection(RiotServer.getServerPositionByName(mDataStorage.getServer()));
//        } else
//            connectbutton.setEnabled(false);
//    }
//
//    @Override protected void onResume() {
//        super.onResume();
//        bus.register(this);
//    }
//
//    @Override protected void onPause() {
//        super.onPause();
//        bus.unregister(this);
//    }
//
//    @Override
//    public CharSequence getToolbarTitle() {
//        return null;
//    }
//
//    @Override
//    public Integer getToolbarColor() {
//        return null;
//    }
//
//    @Override
//    public Integer getToolbarTitleColor() {
//        return null;
//    }
//
//    private void doLoginAnimations() {
//        /**
//         * Set initial Title Scalling to 0.7f
//         */
//        logo.setScaleY(0.7f);
//        logo.setScaleX(0.7f);
//
//        /**
//         * EXPERIMENTAL!
//         */
//
//        loginBottomLayout.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        login_main_layout.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        logo.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//
//
//        ObjectAnimator titleSlideUp = ObjectAnimator.ofFloat(logo, "translationY", 1000, -100, 0)
//                .setDuration(3000);
//
//        ObjectAnimator fadingBackground = ObjectAnimator.ofPropertyValuesHolder(login_main_layout.getBackground(),
//                PropertyValuesHolder.ofInt("alpha", 0))
//                .setDuration(4000);
//
//        ObjectAnimator scalingTitleX = ObjectAnimator.ofFloat(logo, "scaleX", 0.7f, 0.8f, 0.9f, 1.0f);
//        ObjectAnimator scalingTitleY = ObjectAnimator.ofFloat(logo, "scaleY", 0.7f, 0.8f, 0.9f, 1.0f);
//
//
//        final ObjectAnimator credentialsFade = ObjectAnimator.ofFloat(loginBottomLayout, "alpha", 0.0f, 1.0f);
//        credentialsFade.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//                loginBottomLayout.setVisibility(View.VISIBLE);
//            }
//
//            @Override public void onAnimationEnd(Animator animator) {
//            }
//
//            @Override public void onAnimationCancel(Animator animator) {
//            }
//
//            @Override public void onAnimationRepeat(Animator animator) {
//            }
//        });
//
//        final AnimatorSet animatorSetSecondPart = new AnimatorSet();
//        animatorSetSecondPart.setDuration(1000);
//        animatorSetSecondPart.addListener(new Animator.AnimatorListener() {
//            @Override public void onAnimationStart(Animator animation) {
//            }
//
//            @Override public void onAnimationEnd(Animator animation) {
//                loginBottomLayout.setLayerType(View.LAYER_TYPE_NONE, null);
//                logo.setLayerType(View.LAYER_TYPE_NONE, null);
//                login_main_layout.setLayerType(View.LAYER_TYPE_NONE, null);
//            }
//
//            @Override public void onAnimationCancel(Animator animation) {
//            }
//
//            @Override public void onAnimationRepeat(Animator animation) {
//            }
//        });
//        animatorSetSecondPart.playTogether(scalingTitleX, scalingTitleY, credentialsFade);
//
//
//        titleSlideUp.start();
//        fadingBackground.start();
//
//        new Handler().postDelayed(animatorSetSecondPart::start, 2000);
//    }
//
//    @OnTextChanged(R.id.username)
//    public void onUsernameTextChanged(CharSequence cs) {
//        usernameLengthControl = cs.toString().trim().length() > 3;
//        connectbutton.setEnabled(usernameLengthControl && passwordLengthControl);
//    }
//
//    @OnTextChanged(R.id.password)
//    public void onPasswordTextChanged(CharSequence cs) {
//        passwordLengthControl = cs.toString().trim().length() > 3;
//        connectbutton.setEnabled(usernameLengthControl && passwordLengthControl);
//    }
//
//    @Override
//    public int getLayoutResources() {
//        return R.layout.login_activity;
//    }
//
//    @Override
//    public int getNavigationViewPosition() {
//        return -1;
//    }
//
////    @Override
////    public void sendSnackbarMessage(OnSnackBarNotificationEvent notif) {
////        // do nothing
////    }
//
//
//    @OnClick(R.id.connect)
//    public void onConnectClick(View v) {
//        materialDialog = new MaterialDialog.Builder(LoginActivity.this)
//                .title(R.string.activity_login_progress_dialog_title)
//                .content(R.string.activity_login_progress_dialog_message)
//                .progress(true, 0)
//                .cancelable(false)
//                .show();
//
//        mainApplication.startRiotXmppService((String) serverSpinner.getSelectedItem(), getUsername(), getPassword());
//    }
//
//    @Subscribe
//    public void onSuccessLogin(OnSuccessLoginPublish event) {
//
//        if (checkBox.isChecked())
//            mDataStorage.setSaveLoginCredentials(true);
//        else
//            mDataStorage.setSaveLoginCredentials(false);
//
//        mDataStorage.setUsername(getUsername());
//        mDataStorage.setPassword(getPassword());
//        mDataStorage.setServer(getServer());
//
//        mainApplication.bindService();
//    }
//
//    @Subscribe
//    public void onServiceBinded(OnServiceBindedPublish event) {
//        Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        AppContextUtils.overridePendingTransitionBackAppDefault(this);
//        materialDialog.dismiss();
//        this.finish();
//    }
//
//    @Subscribe
//    public void onConnectionFailure(OnConnectionFailurePublish event) {
//        materialDialog.dismiss();
//        Snackbar
//                .make(getWindow().getDecorView().getRootView(),
//                        R.string.activity_connection_failed, Snackbar.LENGTH_LONG).show();
//    }
//
//    @Subscribe
//    public void onLoginFailure(OnLoginFailurePublish event) {
//        materialDialog.dismiss();
//        Snackbar
//                .make(getWindow().getDecorView().getRootView(),
//                        R.string.activity_login_failed, Snackbar.LENGTH_LONG).show();
//    }
//
//
//    public String getUsername() {
//        return this.username.getText().toString();
//    }
//
//    public String getPassword() {
//        return this.password.getText().toString();
//    }
//
//    public String getServer() {
//        return (String) this.serverSpinner.getSelectedItem();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        if (materialDialog != null)
//            materialDialog.dismiss();
//    }
//
//    @Override public void onBackPressed() {
//        super.onBackPressed();
//
//        if (materialDialog != null && materialDialog.isShowing()) {
//            materialDialog.dismiss();
//            LoginActivity.this.finish();
//        }
//    }
//}
