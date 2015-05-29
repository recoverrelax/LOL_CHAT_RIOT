package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.edgelabs.pt.mybaseapp.R;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.drawer.ENavDrawer;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendMessageListFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendListFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.PersonalMessageFragment;

import butterknife.ButterKnife;

public class FriendListActivity extends BaseActivity implements FriendMessageListFragment.FriendMessageListFragActivityCallback {

    public static final String INTENT_FRIEND_NAME = "intent_friend_name";
    public static final String INTENT_FRIEND_XMPPNAME = "intent_friend_xmppname";

    @Override
    public int getLayoutResources() {
        return R.layout.activity_friend_list;
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
                        navigationDrawerFragment.setCurrentSelectedItem(positionByFrag);
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
    public void replaceFragment(String friendUsername, String friendXmppName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = PersonalMessageFragment.newInstance();
        Bundle args =  new Bundle();
        args.putString(INTENT_FRIEND_NAME, friendUsername);
        args.putSerializable(INTENT_FRIEND_XMPPNAME, friendXmppName);

        fragment.setArguments(args);
        fragmentManager.beginTransaction()
//                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
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