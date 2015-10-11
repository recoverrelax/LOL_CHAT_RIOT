package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.recoverrelax.pt.riotxmppchat.MyUtil.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.LogFragment;

import butterknife.ButterKnife;
import pt.reco.myutil.MyContext;

public class LogActivity extends MessageIconActivity {

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
    }

    @Override
    public CharSequence getToolbarTitle() {
        return getResources().getString(R.string.log_activity);
    }

    @Override
    public Integer getToolbarColor() {
        return MyContext.getColor(this, R.color.white);
    }

    @Override
    public Integer getToolbarTitleColor() {
        return MyContext.getColor(this, R.color.black);
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
