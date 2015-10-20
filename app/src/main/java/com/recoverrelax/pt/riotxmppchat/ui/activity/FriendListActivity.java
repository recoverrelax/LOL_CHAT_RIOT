package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.mvp.friendlist.FriendListFragment;

import butterknife.ButterKnife;
import pt.reco.myutil.MyContext;

public class FriendListActivity extends MessageIconActivity {

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

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = FriendListFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public CharSequence getToolbarTitle() {
        return getResources().getString(R.string.friends_online);
    }

    @Override
    public Integer getToolbarColor() {
        return MyContext.getColor(this, R.color.primaryColor120);
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
}
