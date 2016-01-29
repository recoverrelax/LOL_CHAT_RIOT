//package com.recoverrelax.pt.riotxmppchat.ui.activity;
//
//import android.os.Bundle;
//import android.support.design.widget.TabLayout;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentStatePagerAdapter;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//
//import com.recoverrelax.pt.riotxmppchat.MyUtil.AppContextUtils;
//import com.recoverrelax.pt.riotxmppchat.R;
//import com.recoverrelax.pt.riotxmppchat.ui.fragment.settings.SettingsGeneralFragment;
//import com.recoverrelax.pt.riotxmppchat.ui.fragment.settings.Settings_Alert;
//import com.recoverrelax.pt.riotxmppchat.ui.fragment.settings.Settings_Notification;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//import pt.reco.myutil.MyContext;
//
//public class SettingActivity extends MessageIconActivityKt {
//
//    @Bind(R.id.settings_pager)
//    ViewPager settings_pager;
//
//    @Bind(R.id.tabs)
//    TabLayout tabs;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        ButterKnife.bind(this);
//
//        PagerAdapter pagerAdapter = new SettingsPagerAdapter(getSupportFragmentManager());
//        settings_pager.setAdapter(pagerAdapter);
//        tabs.setupWithViewPager(settings_pager);
//
//        /**
//         * Configs
//         */
//    }
//
//    @Override
//    public CharSequence getToolbarTitle() {
//        return getResources().getString(R.string.settings_activity);
//    }
//
//    @Override
//    public Integer getToolbarColor() {
//        return MyContext.getColor(this, R.color.primaryColor);
//    }
//
//    @Override
//    public Integer getToolbarTitleColor() {
//        return null;
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        ButterKnife.unbind(this);
//    }
//
//    @Override
//    public int getLayoutResources() {
//        return R.layout.settings_activity;
//    }
//
//    @Override
//    public int getNavigationViewPosition() {
//        return R.id.navigation_item_4;
//    }
//
//    @Override
//    protected boolean hasNewMessageIcon() {
//        return false;
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        AppContextUtils.overridePendingTransitionBackAppDefault(this);
//    }
//
//    private class SettingsPagerAdapter extends FragmentStatePagerAdapter {
//
//        private final String[] TITLES = {"Notification", "General", "Alert"};
//
//        public SettingsPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            switch (position) {
//                case 0:
//                    return Settings_Notification.newInstance();
//                case 1:
//                    return SettingsGeneralFragment.newInstance();
//                default:
//                    return Settings_Alert.newInstance();
//            }
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return TITLES[position];
//        }
//
//        @Override
//        public int getCount() {
//            return TITLES.length;
//        }
//    }
//
////    @Subscribe
////    @Override
////    public void sendSnackbarMessage(OnSnackBarNotificationEvent notif) {
////        super.sendSnackbarMessage(notif);
////    }
//}
