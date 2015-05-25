package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.edgelabs.pt.mybaseapp.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendMessageListFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.MainFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.PersonalMessageFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity implements FriendMessageListFragment.FriendMessageListFragActivityCallback {

    @InjectView(R.id.parent_view_group)
    RelativeLayout parent_view_group;

    public static final String INTENT_USER_XMPP_NAME = "intent_user_xmpp_name";

    @Override
    public int getLayoutResources() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        if(savedInstanceState == null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = MainFragment.newInstance();
//            fragment.setArguments(args);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        boolean onCreateOptionsMenu = super.onCreateOptionsMenu(menu);

        menu.getItem(0).setVisible(true);
        menu.getItem(1).setVisible(true);
        return onCreateOptionsMenu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id== R.id.navigate){
            startActivity(new Intent(this, SubActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void replaceFragment(String userXmppAddress) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = PersonalMessageFragment.newInstance();
        Bundle args =  new Bundle();
        args.putString(INTENT_USER_XMPP_NAME, userXmppAddress);

        fragment.setArguments(args);
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
