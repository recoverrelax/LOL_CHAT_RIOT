package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.ui.fragment.NavigationDrawerFragment;
import com.edgelabs.pt.mybaseapp.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public abstract class BaseActivity extends AppCompatActivity {

    @Optional
    @InjectView(R.id.app_bar)
    protected Toolbar toolbar;

    @Optional
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawer_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResources());
        ButterKnife.inject(this);

        /**
         * There must always be a toolbar, if it is not needed, it should be hidden but it
         * is still there. But just in case, we check if it's really there.
         */
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        if (drawer_layout != null) {
            NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment)
                    getSupportFragmentManager().findFragmentById(R.id.nav_drawer_fragment);

            navigationDrawerFragment.setup(R.id.nav_drawer_fragment, drawer_layout, toolbar, getSelfNavDrawerItem());
        }
    }

    public abstract @LayoutRes int getLayoutResources();

    /**
     * Returns the navigation drawer item that corresponds to this Activity. Subclasses
     * of BaseActivity override this to indicate what nav drawer item corresponds to them
     * Return -1 to mean that this Activity should not have a Nav Drawer.
     */
    protected abstract int getSelfNavDrawerItem();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public
    @ColorRes
    int getColor(@ColorRes int color) {
        return getResources().getColor(color);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
