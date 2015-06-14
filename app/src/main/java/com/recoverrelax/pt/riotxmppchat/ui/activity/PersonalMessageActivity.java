package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.FriendStatusGameNotificationEvent;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppAndroidUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.PersonalMessageFragment;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;

public class PersonalMessageActivity extends RiotXmppCommunicationActivity {

    public static final String INTENT_FRIEND_NAME = "intent_friend_name";
    public static final String INTENT_FRIEND_XMPPNAME = "intent_friend_xmppname";

    String friendUsername;
    String friendXmppName;

    @Override
    public int getLayoutResources() {
        return R.layout.activity_personal_message_list;
    }

    @Override
    public int getNavigationViewPosition() {
        return R.id.navigation_item_2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                friendUsername = extras.getString(INTENT_FRIEND_NAME);
                friendXmppName = extras.getString(INTENT_FRIEND_XMPPNAME);
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = PersonalMessageFragment.newInstance();
            Bundle args = new Bundle();
            args.putString(INTENT_FRIEND_NAME, friendUsername);
            args.putString(INTENT_FRIEND_XMPPNAME, friendXmppName);
            fragment.setArguments(args);

            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
        setTitle(getResources().getString(R.string.chatting_with) + " " + friendUsername);
        toolbar.getBackground().setAlpha(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppAndroidUtils.overridePendingTransitionBackAppDefault(this);
    }

    @Subscribe
    public void OnFriendStatusGameNotification(FriendStatusGameNotificationEvent notif){
        super.OnFriendStatusGameNotification(notif);
    }

    @Override
    public boolean hasNewMessageIcon() {
        return false;
    }
}
