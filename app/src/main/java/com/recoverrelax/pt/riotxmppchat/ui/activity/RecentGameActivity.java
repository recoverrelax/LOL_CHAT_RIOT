package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.RecentGameFragment;

import butterknife.ButterKnife;
import pt.reco.myutil.MyContext;

public class RecentGameActivity extends MessageIconActivity {

    public static final String FRIEND_XMPP_ADDRESS_INTENT = "friend_xmpp_address_intent";
    public static final String FRIEND_XMPP_USERNAME_INTENT = "friend_xmpp_username";

    private static final String friendXmppAddressDefault = null;
    private static final String friendXmppUsernameDefault = null;

    private String friendXmppAddress;
    private String friendXmppUsername;

    @Override
    public int getLayoutResources() {
        return R.layout.recent_game_activity;
    }

    @Override
    public int getNavigationViewPosition() {
        return 0;
//        return R.id.navigation_item_3;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                friendXmppAddress = extras.getString(FRIEND_XMPP_ADDRESS_INTENT, friendXmppAddressDefault);
                friendXmppUsername = extras.getString(FRIEND_XMPP_USERNAME_INTENT, friendXmppUsernameDefault);
            }
        } else {
            friendXmppAddress = (String) savedInstanceState.getSerializable(FRIEND_XMPP_ADDRESS_INTENT);
            friendXmppUsername = (String) savedInstanceState.getSerializable(FRIEND_XMPP_USERNAME_INTENT);
        }

        if(savedInstanceState == null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = RecentGameFragment.newInstance(friendXmppAddress, friendXmppUsername);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public CharSequence getToolbarTitle() {
        return null;
    }

    @Override
    public Integer getToolbarColor() {
        return MyContext.getColor(this, R.color.primaryColor);
    }

    @Override
    public Integer getToolbarTitleColor() {
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppContextUtils.overridePendingTransitionBackAppDefault(this);
    }

    @Override
    protected boolean hasNewMessageIcon() {
        return true;
    }

//    @Subscribe
//    @Override
//    public void sendSnackbarMessage(OnSnackBarNotificationEvent notif) {
//        super.sendSnackbarMessage(notif);
//    }
}
