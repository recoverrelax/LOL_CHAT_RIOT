package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewMessageReceivedEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppAndroidUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.MessageNotification;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.settings.Settings_Alert;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.settings.Settings_General;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.settings.Settings_Notification;
import com.squareup.otto.Subscribe;

import org.jivesoftware.smack.packet.Message;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SettingActivity extends BaseActivity {

    @InjectView(R.id.settings_pager)
    ViewPager settings_pager;

    @InjectView(R.id.tabs)
    TabLayout tabs;

    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        setTitle("Settings");
        if(toolbar != null)
            toolbar.setBackgroundColor(getResources().getColor(R.color.primaryColorDarkT2));

        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }

        pagerAdapter = new SettingsPagerAdapter(getSupportFragmentManager());
        settings_pager.setAdapter(pagerAdapter);
        tabs.setupWithViewPager(settings_pager);

        /**
         * Configs
         */
    }

    @Override
    public int getLayoutResources() {
        return R.layout.activity_settings;
    }

    @Override
    public int getNavigationViewPosition() {
        return -1;
    }

    private class SettingsPagerAdapter extends FragmentStatePagerAdapter {

        private final String [] TITLES = {"Notification", "General", "Alert"};

        public SettingsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return Settings_Notification.newInstance();
                case 1:
                    return Settings_General.newInstance();
                default:
                    return Settings_Alert.newInstance();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppAndroidUtils.overridePendingTransitionBackAppDefault(this);
    }

    @Subscribe
    public void OnNewMessageReceived(final OnNewMessageReceivedEvent messageReceived) {
        final Message message = messageReceived.getMessage();
        final String userXmppAddress = messageReceived.getMessageFrom();

        final String username = MainApplication.getInstance().getRiotXmppService().getRoster().getEntry(userXmppAddress).getName();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MessageNotification(SettingActivity.this, message.getBody(), username, userXmppAddress, MessageNotification.NotificationType.SNACKBAR);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainApplication.getInstance().getBusInstance().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.getInstance().getBusInstance().register(this);
    }
}
