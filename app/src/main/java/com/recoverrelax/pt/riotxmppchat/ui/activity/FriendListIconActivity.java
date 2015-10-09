package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendListFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import pt.reco.myutil.MyContext;

public class FriendListIconActivity extends MessageIconActivity {

    @Nullable
    @Bind(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    @Override
    public int getLayoutResources() {
        return R.layout.friends_list_activity;
    }

    @Override
    public int getNavigationViewPosition() {
        return R.id.navigation_item_1;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean hasNewMessageIcon() {
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
            appBarLayout.setBackgroundColor(MyContext.getColor(this, R.color.primaryColor120));
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

//    @Subscribe
//    @Override
//    public void sendSnackbarMessage(OnSnackBarNotificationEvent notif) {
//        super.sendSnackbarMessage(notif);
//    }
}
