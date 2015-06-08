package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.FriendLeftGameNotification;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.SnackBarNotification;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendListFragment;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FriendListActivity extends BaseActivity {

    @InjectView(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    @Override
    public int getLayoutResources() {
        return R.layout.activity_friend_list;
    }

    @Override
    public int getNavigationViewPosition() {
        return R.id.navigation_item_1;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        MainApplication.getInstance().setApplicationClosed(false);

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
    }

    @Override
    protected void onDestroy() {
        MainApplication.getInstance().setApplicationClosed(true);
        Log.i("ASAS", "onDestroy FriendListActivity");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.getInstance().getBusInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainApplication.getInstance().getBusInstance().unregister(this);
    }

    @Override
    public void onBackPressed() {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
    }

    @Subscribe
    public void OnFriendLeftGame(FriendLeftGameNotification notif){
        new SnackBarNotification(this, notif.getMessage(), "PM", notif.getFriendName(), notif.getFriendXmppAddress());
    }
}
