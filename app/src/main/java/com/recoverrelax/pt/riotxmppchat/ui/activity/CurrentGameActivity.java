package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.CurrentGameFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CurrentGameActivity extends BaseActivity {

    @Nullable
    @Bind(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    public static final String FRIEND_XMPP_ADDRESS_INTENT = "friend_xmpp_address_intent";

    private static final String friendXmppAddressDefault = null;
    private String friendXmppAddress;

    @Override
    public int getLayoutResources() {

        return R.layout.activity_current_game;
    }

    @Override
    public int getNavigationViewPosition() {
        return R.id.navigation_item_3;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                friendXmppAddress = extras.getString(FRIEND_XMPP_ADDRESS_INTENT, friendXmppAddressDefault);
            }
        } else {
            friendXmppAddress = (String) savedInstanceState.getSerializable(FRIEND_XMPP_ADDRESS_INTENT);
        }

        if(savedInstanceState == null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = CurrentGameFragment.newInstance(friendXmppAddress);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }

        if(appBarLayout != null)
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.primaryColor));
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
}
