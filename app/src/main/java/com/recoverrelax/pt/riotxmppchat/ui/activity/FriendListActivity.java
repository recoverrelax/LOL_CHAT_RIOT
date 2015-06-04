package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnReplaceMainFragmentEvent;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.drawer.ENavDrawer;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendListFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.PersonalMessageFragment;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;

import static com.recoverrelax.pt.riotxmppchat.ui.activity.PersonalMessageActivity.*;
import static com.recoverrelax.pt.riotxmppchat.ui.activity.PersonalMessageActivity.INTENT_FRIEND_NAME;

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

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if(navigationDrawerFragment != null) {
                    Fragment frag = getSupportFragmentManager().findFragmentById(R.id.container);
                        int positionByFrag = ENavDrawer.getPositionByFrag(frag);
//                        navigationDrawerFragment.setCurrentSelectedItem(positionByFrag);
                }
            }
        });
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

    @Subscribe
    public void replaceFragment(OnReplaceMainFragmentEvent event) {

        String friendUsername = event.getFriendUsername();
        String friendXmppName = event.getFriendXmppName();

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = PersonalMessageFragment.newInstance();
        Bundle args =  new Bundle();
        args.putString(INTENT_FRIEND_NAME, friendUsername);
        args.putSerializable(INTENT_FRIEND_XMPPNAME, friendXmppName);

        fragment.setArguments(args);
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.zoom_enter, R.anim.zoom_exit, R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if(isMainFragmentAttached()){
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
        }else{
            super.onBackPressed();
        }
    }

    public boolean isMainFragmentAttached(){
        Fragment fragmentById = getSupportFragmentManager().findFragmentById(R.id.container);
        return fragmentById instanceof FriendListFragment;
    }
}
