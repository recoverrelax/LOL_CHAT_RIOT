package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.DashBoardFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DashBoardActivity extends BaseActivity {

    @Nullable
    @Bind(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    @Override
    public int getLayoutResources() {
        return R.layout.activity_dashboard;
    }

    @Override
    public int getNavigationViewPosition() {
        return R.id.navigation_item_0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        if(savedInstanceState == null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = DashBoardFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
        setTitle(getResources().getString(R.string.dashboard));
        appBarLayout.setBackgroundColor(getResources().getColor(R.color.primaryColor));
    }

    @Override
    public void onBackPressed() {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
    }
}
