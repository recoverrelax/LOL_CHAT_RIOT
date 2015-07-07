package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewMessageEventEvent;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.NotificationCenter.MessageNotification;
import com.recoverrelax.pt.riotxmppchat.MyUtil.NotificationCenter.StatusNotification;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendListFragment;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public class FriendListActivity extends RiotXmppNewMessageActivity {

    @Nullable
    @Bind(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    @Override
    public int getLayoutResources() {

        return R.layout.activity_friend_list;
    }

    @Override
    public int getNavigationViewPosition() {
        return R.id.navigation_item_1;
    }

    @Subscribe
    public void OnNewMessageReceived(final OnNewMessageEventEvent messageReceived) {
        super.OnNewMessageReceived(messageReceived);
    }

    @Subscribe
    public void OnMessageSnackBarReady(MessageNotification event){
        super.OnMessageSnackBarReady(event);
    }

    @Subscribe
    public void OnStatusSnackBarReady(StatusNotification event){
        super.OnStatusSnackBarReady(event);
    }

    @Override
    public boolean hasNewMessageIcon() {
        return true;
    }

    @Override
    public boolean doesReceiveMessages() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        if(savedInstanceState == null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = FriendListFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
        setTitle(getResources().getString(R.string.friends_online));

//        appBarLayout.getBackground().setAlpha(120);
//        appBarLayout.setTranslationY(0);

        if(appBarLayout != null)
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.primaryColor120));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppContextUtils.overridePendingTransitionBackAppDefault(this);
    }
}
