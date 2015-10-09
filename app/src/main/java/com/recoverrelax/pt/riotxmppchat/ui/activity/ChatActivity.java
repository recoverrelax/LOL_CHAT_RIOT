package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.RelativeLayout;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.ChatFragment;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatActivity extends MessageIconActivity {

    public static final String INTENT_FRIEND_NAME = "intent_friend_name";
    public static final String INTENT_FRIEND_XMPPNAME = "intent_friend_xmppname";

    @Bind(R.id.parent_view_group)
    RelativeLayout parent_view_group;

    String friendUsername;
    String friendXmppName;

    @Override
    public int getLayoutResources() {
        return R.layout.personal_message_list_activity;
    }

    @Override
    public int getNavigationViewPosition() {
        return R.id.navigation_item_2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                friendUsername = extras.getString(INTENT_FRIEND_NAME);
                friendXmppName = extras.getString(INTENT_FRIEND_XMPPNAME);
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = ChatFragment.newInstance(friendUsername, friendXmppName);

            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
        setTitle(getResources().getString(R.string.chatting_with) + " " + friendUsername);
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
}
