package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppAndroidUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendMessageListFragment;

import butterknife.ButterKnife;

public class FriendMessageListActivity extends BaseActivity {

    @Override
    public int getLayoutResources() {
        return R.layout.activity_friend_message_list;
    }

    @Override
    public int getNavigationViewPosition() {
        return R.id.navigation_item_2;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        if(savedInstanceState == null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = FriendMessageListFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
        toolbar.getBackground().setAlpha(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppAndroidUtils.overridePendingTransitionBackAppDefault(this);
    }
}
