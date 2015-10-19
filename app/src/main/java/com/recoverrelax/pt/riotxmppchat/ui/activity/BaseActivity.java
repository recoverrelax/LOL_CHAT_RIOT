package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.OnDisconnectEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.OnReconnectEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.EventHandler;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppSnackbarUtils;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pt.reco.myutil.MyContext;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnReconnectEvent, OnDisconnectEvent {

    //http://ddragon.leagueoflegends.com/cdn/img/champion/splash/Kalista_2.jpg

    @Nullable
    @Bind(R.id.app_bar)
    protected Toolbar toolbar;

    @Nullable
    @Bind(R.id.appBarLayout)
    AppBarLayout appBarLayout;

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

    @Nullable
    @Bind(R.id.statusIcon)
    ImageView statusIcon;

    private boolean userLearnedDrawer;
    private boolean fromSavedInstanceState;
    private @ColorInt Integer toolbarColor;

    @Inject DataStorage mDataStorage;
    @Inject RiotXmppDBRepository riotRepository;
    @Inject EventHandler handler;
    @Inject
    RiotRosterManager rosterManager;

    @Inject
    Bus bus;

    private Snackbar connectionSnackbar;

    private Handler mHandler;
    // delay to launch nav drawer item, to allow close animation to play
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResources());
        ButterKnife.bind(this);
        MainApplication.getInstance().getAppComponent().inject(this);

        toolbarColor = getResources().getColor(R.color.primaryColor);

        if(savedInstanceState != null){
            fromSavedInstanceState = true;
        }

        userLearnedDrawer = mDataStorage.userLearnedDrawer();

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

            setTitle(getToolbarTitle());
            setToolbarBackgroundColor(getToolbarColor());
            setToolbarTitleColor(getToolbarTitleColor());

        }

        if(navigationView != null){
            setupDrawerContent();
        }else{
            ActionBar supportActionBar = getSupportActionBar();
            if(supportActionBar != null) {
                supportActionBar.setDisplayHomeAsUpEnabled(true);
                supportActionBar.setHomeButtonEnabled(true);
            }
        }
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        if (toolbar_title != null) {
            toolbar_title.setText(title == null ? "" : title.toString());
        }
    }

    public void setToolbarBackgroundColor(@ColorInt Integer toolbarColor) {
        if (toolbarColor == null) return;

        if (appBarLayout != null) {
            appBarLayout.setBackgroundColor(toolbarColor);
            this.toolbarColor = toolbarColor;
        } else if (toolbar != null) {
            toolbar.setBackgroundColor(toolbarColor);
            this.toolbarColor = toolbarColor;
        }
    }

    public @ColorInt int getToobarColor(){
        return this.toolbarColor;
    }

    public void setToolbarTitleColor(@ColorInt Integer toolbarTitleColor){
        if(toolbarTitleColor == null) return;

        if(toolbar_title != null)
            toolbar_title.setTextColor(toolbarTitleColor);
    }

    /**
     *
     * @return NULL FOR NO TITLE
     */
    public abstract @StringRes CharSequence getToolbarTitle();

    /**
     *
     * @return NULL FOR DEFAULT_COLOR
     */
    public abstract @ColorInt Integer getToolbarColor();

    /**
     *
     * @return NULL FOR DEFAULT COLOR
     */
    public abstract @ColorInt Integer getToolbarTitleColor();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void setupDrawerContent() {
        mHandler = new Handler();

        if (drawer_username != null) {
            drawer_username.setText(getResources().getString(R.string.drawer_default_username_prefix) + " " +
                    mDataStorage.getUsername());
        }

        if(statusIcon != null){
            Drawable drawable = statusIcon.getDrawable();
            drawable.mutate();

            drawable.setColorFilter(MyContext.getColor(this, MainApplication.getInstance().getRiotXmppService().getPresenceMode().getStatusColor2()), PorterDuff.Mode.SRC_IN);

            statusIcon.setImageDrawable(drawable);

            statusIcon.setOnClickListener(view -> {
                if(MainApplication.getInstance().getRiotXmppService().getConnection() != null) {
                    MainApplication.getInstance().getRiotXmppService().swapPresenceMode(false);

                    drawable.setColorFilter(
                            MyContext.getColor(this, MainApplication.getInstance().getRiotXmppService().getPresenceMode().getStatusColor2()),
                            PorterDuff.Mode.SRC_IN);

                    statusIcon.setImageDrawable(drawable);
                }
            });
        }

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.drawer_opened, R.string.drawer_closed) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!userLearnedDrawer) {
                    mDataStorage.setUserLearnedDrawer();
                }
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

        drawer_layout.setScrimColor(MyContext.getColor(this, R.color.blackDrawerScrim));

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        setNavigationViewPosition(getNavigationViewPosition());
    }

    @Nullable
    @OnClick(R.id.logout)
    public void onDrawerLogout(View view){
        this.finishAffinity();

        if(!mDataStorage.getNotificationsAlwaysOn())
            MainApplication.getInstance().stopService();
    }

    protected abstract @LayoutRes int getLayoutResources();

    /**
     * @return Position or -1 for no-position
     */
    protected abstract int getNavigationViewPosition();


    private void setNavigationViewPosition(int menuItemId){
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
        }else if(id == android.R.id.home){
            onBackPressed();
        }
                return super.onOptionsItemSelected(item);
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

//                    case R.id.navigation_item_3:
//
//                        AbstractXMPPConnection conn = MainApplication.getInstance().getRiotXmppService().getRiotConnectionManager().getConnection().toBlocking().single();
//                        String friendXmppAddress = AppXmppUtils.parseXmppAddress(conn.getUser());
//
//                        intent = new Intent(this, RecentGameActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        intent.putExtra(RecentGameActivity.FRIEND_XMPP_ADDRESS_INTENT, friendXmppAddress);
//                        intent.putExtra(RecentGameActivity.FRIEND_XMPP_USERNAME_INTENT, LiveGameActivity.FRIEND_XMPP_USERNAME_ME);
//
//                        break;


                    case R.id.navigation_item_4:
                        if(MainApplication.getInstance().isRealScoutEnabled) {
                            intent = new Intent(BaseActivity.this, ShardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        } else
                        AppSnackbarUtils.showSnackBar(this, R.string.feature_coming, AppSnackbarUtils.LENGTH_LONG);

                        break;

                    case R.id.navigation_item_5:
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
        AppContextUtils.startChatActivity(this, username, userXmppName);
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
        setNavigationViewPosition(getNavigationViewPosition());
        bus.register(this);
        handler.registerForReconnectEvent(this);
        handler.registerForDisconnectEvent(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
        handler.unregisterForReconnectEvent(this);
        handler.unregisterForDisconnectEvent(this);
    }

    @Override
    public void onReconnect() {
        if(connectionSnackbar != null)
            connectionSnackbar.dismiss();
    }

    @Override
    public void onDisconnect() {
        connectionSnackbar = AppSnackbarUtils.showSnackBar(
                this,
                R.string.activity_connection_lost,
                AppSnackbarUtils.LENGTH_INDEFINITE
        );
    }

    @Nullable
    public View getToolbar() {
        return this.toolbar;
    }
}
