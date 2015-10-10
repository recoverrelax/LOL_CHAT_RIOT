package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.LogFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LogActivity extends MessageIconActivity {

    @Nullable
    @Bind(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    @Override
    public int getLayoutResources() {
        return R.layout.log_activity;
    }

    @Override
    public int getNavigationViewPosition() {
        return R.id.navigation_item_1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        if(savedInstanceState == null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = LogFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }

        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }

        setTitle(getResources().getString(R.string.log_activity));
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
