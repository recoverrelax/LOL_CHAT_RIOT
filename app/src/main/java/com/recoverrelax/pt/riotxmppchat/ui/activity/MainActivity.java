package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.recoverrelax.pt.riotxmppchat.MyUtil.drawer.ENavDrawer;
import com.edgelabs.pt.mybaseapp.R;
import com.github.mrengineer13.snackbar.SnackBar;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity implements SnackBar.OnMessageClickListener {

    @InjectView(R.id.parent_view_group)
    RelativeLayout parent_view_group;

    @Override
    public int getLayoutResources() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);

        new SnackBar.Builder(this.getApplicationContext(), parent_view_group)
                .withOnClickListener(this)
                .withMessage("This library is awesome!") // OR
                .withActionMessage("Close") // OR
                .withBackgroundColorId(R.color.primaryColor)
                .withTextColorId(R.color.accentColor)
                .withDuration((short) 10000)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return ENavDrawer.NAVDRAWER_ITEM_0.getNavDrawerId();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        boolean onCreateOptionsMenu = super.onCreateOptionsMenu(menu);

        menu.getItem(0).setVisible(true);
        menu.getItem(1).setVisible(true);
        return onCreateOptionsMenu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id== R.id.navigate){
            startActivity(new Intent(this, SubActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMessageClick(Parcelable parcelable) {

    }
}
