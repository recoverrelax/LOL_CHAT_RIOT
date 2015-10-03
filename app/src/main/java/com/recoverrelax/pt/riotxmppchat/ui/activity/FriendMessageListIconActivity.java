package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendMessageListFragment;

import butterknife.ButterKnife;

public class FriendMessageListIconActivity extends MessageIconActivity {

    @Override
    public int getLayoutResources() {
        return R.layout.friend_message_list_activity;
    }

    @Override
    public int getNavigationViewPosition() {
        return R.id.navigation_item_2;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

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
        return false;
    }

//    @Subscribe
//    @Override
//    public void sendSnackbarMessage(OnSnackBarNotificationEvent notif) {
//        super.sendSnackbarMessage(notif);
//    }
}
