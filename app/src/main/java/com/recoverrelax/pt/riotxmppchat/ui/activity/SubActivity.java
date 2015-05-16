package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;

import com.recoverrelax.pt.riotxmppchat.MyUtil.drawer.ENavDrawer;
import com.edgelabs.pt.mybaseapp.R;

import butterknife.ButterKnife;

public class SubActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        setupToolbar();
    }

    public void enableScrollView(ScrollView scrollView){
        scrollView.setOnTouchListener(null);
    }

    private void setupToolbar() {
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null) {
            supportActionBar.setHomeButtonEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }

    }

    @Override
    public int getLayoutResources() {
        return R.layout.activity_sub;
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return ENavDrawer.NAVDRAWER_ITEM_1.getNavDrawerId();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean onCreateOptionsMenu = super.onCreateOptionsMenu(menu);

        menu.getItem(0).setVisible(true);
        return onCreateOptionsMenu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }
}
