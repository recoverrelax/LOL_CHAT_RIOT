//package com.recoverrelax.pt.riotxmppchat.ui.activity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//
//import com.recoverrelax.pt.riotxmppchat.MainApplication;
//import com.recoverrelax.pt.riotxmppchat.R;
//import com.recoverrelax.pt.riotxmppchat.ui.mvp.dashboard.DashBoardFragment;
//
//import butterknife.ButterKnife;
//import pt.reco.myutil.MyContext;
//
//public class DashBoardActivity extends MessageIconActivity {
//
//    @Override
//    public int getLayoutResources() {
//        return R.layout.dashboard_activity;
//    }
//
//    @Override
//    public int getNavigationViewPosition() {
//        return R.id.navigation_item_0;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        ButterKnife.bind(this);
//        MainApplication.getInstance().getAppComponent().inject(this);
//
//        if (savedInstanceState == null) {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            Fragment fragment = DashBoardFragment.newInstance();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.container, fragment)
//                    .commit();
//        }
//    }
//
//    @Override
//    public CharSequence getToolbarTitle() {
//        return getResources().getString(R.string.dashboard);
//    }
//
//    @Override
//    public Integer getToolbarColor() {
//        return MyContext.getColor(this, R.color.black);
//    }
//
//    @Override
//    public Integer getToolbarTitleColor() {
//        return null;
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        ButterKnife.unbind(this);
//    }
//
//    @Override
//    public void onBackPressed() {
//        Intent i = new Intent(Intent.ACTION_MAIN);
//        i.addCategory(Intent.CATEGORY_HOME);
//        startActivity(i);
//    }
//
//    @Override
//    protected boolean hasNewMessageIcon() {
//        return true;
//    }
//}
