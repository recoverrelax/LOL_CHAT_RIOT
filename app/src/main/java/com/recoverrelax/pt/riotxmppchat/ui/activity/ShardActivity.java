package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.ShardFragment;

import butterknife.ButterKnife;
import pt.reco.myutil.MyContext;

public class ShardActivity extends MessageIconActivity {

    @Override
    public int getLayoutResources() {
        return R.layout.shard_activity;
    }

    @Override
    public int getNavigationViewPosition() {
        return R.id.navigation_item_4;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        if(savedInstanceState == null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = ShardFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }

    }

    @Override
    public CharSequence getToolbarTitle() {
        return getResources().getString(R.string.title_activity_shard);
    }

    @Override
    public Integer getToolbarColor() {
        return MyContext.getColor(this, R.color.primaryColorDark);
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
