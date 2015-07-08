package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.RelativeLayout;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.PersonalMessageFragment;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PersonalMessageActivity extends BaseActivity {

    public static final String INTENT_FRIEND_NAME = "intent_friend_name";
    public static final String INTENT_FRIEND_XMPPNAME = "intent_friend_xmppname";
    public static final String INTENT_BGCOLOR = "intent_bgcolor";

    @Bind(R.id.parent_view_group)
    RelativeLayout parent_view_group;

    String friendUsername;
    String friendXmppName;
    int bgColor;

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
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                friendUsername = extras.getString(INTENT_FRIEND_NAME);
                friendXmppName = extras.getString(INTENT_FRIEND_XMPPNAME);
                bgColor = extras.getInt(INTENT_BGCOLOR,
                        AppMiscUtils.getXRamdomMaterialColorT(new Random(), 1, this, 120) // return a list of colors
                                        .get(0));
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = PersonalMessageFragment.newInstance();
            Bundle args = new Bundle();
            args.putString(INTENT_FRIEND_NAME, friendUsername);
            args.putString(INTENT_FRIEND_XMPPNAME, friendXmppName);
            args.putInt(INTENT_BGCOLOR, bgColor);
            fragment.setArguments(args);

            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
        setTitle(getResources().getString(R.string.chatting_with) + " " + friendUsername);
        toolbar.setBackgroundColor(bgColor);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppContextUtils.overridePendingTransitionBackAppDefault(this);
    }
}
