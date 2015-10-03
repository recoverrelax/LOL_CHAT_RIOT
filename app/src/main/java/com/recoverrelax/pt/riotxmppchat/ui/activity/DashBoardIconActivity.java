package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.RelativeLayout;

import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.DashBoardFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DashBoardIconActivity extends MessageIconActivity {

    @Nullable
    @Bind(R.id.parent_view_group)
    RelativeLayout parent_view_group;

    @Override
    public int getLayoutResources() {
        return R.layout.dashboard_activity;
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
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onBackPressed() {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
    }

    @Override
    protected boolean hasNewMessageIcon() {
        return true;
    }
}
