package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Nullable
    @Bind(R.id.app_bar)
    protected Toolbar toolbar;

    @Nullable
    @Bind(R.id.navigationView)
    NavigationView navigationView;

    @Nullable
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer_layout;

    @Nullable
    @Bind(R.id.toolbar_title)
    TextView toolbar_title;

    @Nullable
    @Bind(R.id.drawer_username)
    TextView drawer_username;

    private boolean userLearnedDrawer;
    private boolean fromSavedInstanceState;
    private DataStorage sDataStorage = DataStorage.getInstance();

    private Handler mHandler;
    private ActionBarDrawerToggle drawerToggle;
    // delay to launch nav drawer item, to allow close animation to play
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResources());
        ButterKnife.bind(this);
        MainApplication.getInstance().addStartedActivity();

        if(savedInstanceState != null){
            fromSavedInstanceState = true;
        }

        userLearnedDrawer = sDataStorage.userLearnedDrawer();

        if(getResources().getBoolean(R.bool.isTablet)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

        /**
         * There must always be a toolbar, if it is not needed, it should be hidden but it
         * is still there. But just in case, we check if it's really there.
         */
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar supportActionBar = getSupportActionBar();

            if(supportActionBar != null)
                supportActionBar.setDisplayShowTitleEnabled(false);
        }

        if(navigationView != null){
            setupDrawerContent();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainApplication.getInstance().addStoppedActivity();
    }

    private void setupDrawerContent() {
        mHandler = new Handler();

        if (drawer_username != null) {
            drawer_username.setText(getResources().getString(R.string.drawer_default_username_prefix) + " " +
                    DataStorage.getInstance().getUsername());
        }

        drawerToggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.drawer_opened, R.string.drawer_closed) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(!userLearnedDrawer){
                    sDataStorage.setUserLearnedDrawer();
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        if(!userLearnedDrawer && !fromSavedInstanceState){
            if (drawer_layout != null) {
                if (navigationView != null) {
                    drawer_layout.openDrawer(navigationView);
                }
            }
        }

        if (drawer_layout != null) {
            drawer_layout.setDrawerListener(drawerToggle);
        }

        if (drawer_layout != null) {
            drawer_layout.post(drawerToggle::syncState);
        }

        drawer_layout.setScrimColor(getResources().getColor(R.color.blackDrawerScrim));

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        setNavigationViewPosition(getNavigationViewPosition());
    }

    @Nullable
    @OnClick(R.id.logout)
    public void onDrawerLogout(View view){
        this.finish();

        if(!DataStorage.getInstance().getNotificationsAlwaysOn())
            MainApplication.getInstance().getRiotXmppService().stopSelf();
    }

    public abstract @LayoutRes int getLayoutResources();

    /**
     * @return Position or -1 for no-position
     */
    public abstract int getNavigationViewPosition();


    public void setNavigationViewPosition(int menuItemId){
        MenuItem item = null;
        if (navigationView != null) {
            item = navigationView.getMenu().findItem(menuItemId);
        }
        if(item != null && drawer_layout!=null)
            item.setChecked(true);
    }

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
        }else if (id == R.id.newMessage){
            goToMessageListActivity();
            return true;
        }else if(id == android.R.id.home){
            onBackPressed();
        }
                return super.onOptionsItemSelected(item);
        }

    @Override
    public void setTitle(CharSequence title) {
        if (toolbar_title != null) {
            toolbar_title.setText(title.toString());
        }
    }

    public
    @ColorRes
    int getColor(@ColorRes int color) {
        return getResources().getColor(color);
    }

    @Nullable
    public Toolbar getToolbar(){
        return toolbar;
    }

    public TextView getToolbarTitleTextView(){
        return this.toolbar_title;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
                Intent intent = null;
                int itemId = menuItem.getItemId();

                switch(itemId){
                    case R.id.navigation_item_0:
                        intent = new Intent(BaseActivity.this, DashBoardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        break;
                    case R.id.navigation_item_1:
                        intent = new Intent(BaseActivity.this, FriendListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        break;
                    case R.id.navigation_item_2:
                        intent = new Intent(BaseActivity.this, FriendMessageListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        break;
                    case R.id.navigation_item_3:
                        intent = new Intent(BaseActivity.this, SettingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        break;
                }

                final Intent finalIntent = intent;
                mHandler.postDelayed(() -> {
                    if (finalIntent != null) {
                        startActivity(finalIntent);
                        AppContextUtils.overridePendingTransitionBackAppDefault(BaseActivity.this);
                    }
                }, NAVDRAWER_LAUNCH_DELAY);
        if (drawer_layout != null) {
            drawer_layout.closeDrawer(Gravity.LEFT);
        }
        return true;
    }

    public void goToMessageActivity(String username, String userXmppName){
        AppContextUtils.startPersonalMessageActivity(this, username, userXmppName);
    }

    public void goToMessageListActivity(){
        if(getNavigationViewPosition() != -1)
            if (navigationView != null) {
                onNavigationItemSelected(navigationView.getMenu().findItem(R.id.navigation_item_2));
            }
        else {
            Intent intent = new Intent(BaseActivity.this, FriendMessageListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            AppContextUtils.overridePendingTransitionBackAppDefault(BaseActivity.this);
            this.finish();
        }
    }

    public void goToFriendListActivity(){
        if(getNavigationViewPosition() != -1)
            if (navigationView != null) {
                onNavigationItemSelected(navigationView.getMenu().findItem(R.id.navigation_item_1));
            }
        else {
            Intent intent = new Intent(BaseActivity.this, FriendListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            AppContextUtils.overridePendingTransitionBackAppDefault(BaseActivity.this);
            this.finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.getInstance().setBaseActivity(this);
        MainApplication.getInstance().addResumedActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainApplication.getInstance().setBaseActivity(null);
        MainApplication.getInstance().addPausedActivity();
    }
}
