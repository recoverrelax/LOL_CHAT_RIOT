package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.drawer.ENavDrawer;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendListFragment;

import butterknife.ButterKnife;

public class FriendListActivity extends BaseActivity {

    @Override
    public int getLayoutResources() {
        return R.layout.activity_friend_list;
    }

    @Override
    public int getNavigationDrawerPosition() {
        return ENavDrawer.NAVDRAWER_ITEM_0.getNavDrawerId();
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

        toolbar.getBackground().setAlpha(0);
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
}
