package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.FriendLeftGameNotification;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppAndroidUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.SnackBarNotification;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendListFragment;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class FriendListActivity extends BaseActivity {

    @Optional
    @InjectView(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    private ImageView imageviewMessage;

    @Override
    public int getLayoutResources() {

        return R.layout.activity_friend_list;
    }

    @Override
    public int getNavigationViewPosition() {
        return R.id.navigation_item_1;
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

//        appBarLayout.getBackground().setAlpha(120);
//        appBarLayout.setTranslationY(0);
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

    @Subscribe
    public void OnFriendLeftGame(FriendLeftGameNotification notif){
        new SnackBarNotification(this, notif.getMessage(), "PM", notif.getFriendName(), notif.getFriendXmppAddress());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean b = super.onCreateOptionsMenu(menu);

        boolean hasUnreaded = MainApplication.getInstance().hasNewMessages();
        MenuItem messageItem = menu.findItem(R.id.newMessage);

        if (hasUnreaded) {

            messageItem.setVisible(true);

            Drawable messageIcon = messageItem.getIcon();
            messageIcon.mutate();
            messageItem.setActionView(R.layout.new_message_view);

            View actionView = messageItem.getActionView();

            imageviewMessage = ButterKnife.findById(actionView, R.id.newMessage);
            Drawable drawable = imageviewMessage.getDrawable();
            drawable.mutate();
            drawable.setColorFilter(getResources().getColor(R.color.newMessageColor), PorterDuff.Mode.SRC_IN);

            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToMessageListActivity();
                }
            });

            AppAndroidUtils.setBlinkAnimation(imageviewMessage, true);
        } else {
            messageItem.setVisible(false);
            messageItem.setActionView(null);
            if(imageviewMessage != null)
                AppAndroidUtils.setBlinkAnimation(imageviewMessage, false);
        }

        return b;
    }
}
