package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AndroidUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.drawer.ENavDrawer;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendMessageListFragment;


import butterknife.ButterKnife;

public class FriendMessageListActivity extends BaseActivity {

    @Override
    public int getLayoutResources() {
        return R.layout.activity_friend_message_list;
    }

    @Override
    public int getNavigationDrawerPosition() {
        return ENavDrawer.NAVDRAWER_ITEM_1.getNavDrawerId();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        MainApplication.getInstance().setApplicationClosed(false);

        if(savedInstanceState == null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = FriendMessageListFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AndroidUtils.overridePendingTransitionBackAppDefault(this);
    }
}
