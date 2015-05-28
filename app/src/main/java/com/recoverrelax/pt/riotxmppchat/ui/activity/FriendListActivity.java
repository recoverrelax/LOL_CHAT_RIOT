package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import com.edgelabs.pt.mybaseapp.R;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.drawer.ENavDrawer;
import com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
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
                    if (frag instanceof FriendListFragment) {
                        // do something with f
                        int positionByFrag = ENavDrawer.getPositionByFrag(frag);
                        navigationDrawerFragment.setCurrentSelectedItem(positionByFrag);
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        MainApplication.getInstance().setApplicationClosed(true);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        boolean onCreateOptionsMenu = super.onCreateOptionsMenu(menu);

        menu.findItem(R.id.action_settings).setVisible(true);
        menu.findItem(R.id.navigate).setVisible(false).setEnabled(false);
        menu.findItem(R.id.newMessage).setVisible(false);
        return onCreateOptionsMenu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.newMessage){
            navigationDrawerFragment.onDrawerItemSelected(ENavDrawer.NAVDRAWER_ITEM_1.getNavDrawerId());
            return true;
        }else
            return super.onOptionsItemSelected(item);
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
