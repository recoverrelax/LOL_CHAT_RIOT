package com.recoverrelax.pt.riotxmppchat;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import com.crashlytics.android.Crashlytics;
import com.recoverrelax.pt.riotxmppchat.EventHandling.EventHandler;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.OnServiceBindedPublish;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.Storage.BusHandler;

import java.io.File;

import javax.inject.Inject;

import LolChatRiotDb.DaoMaster;
import LolChatRiotDb.DaoSession;
import io.fabric.sdk.android.Fabric;

public class MainApplication extends Application {
    private static final String TAG = MainApplication.class.getSimpleName();
    /**
     * This value is stored as a buffer because its accessed many times.
     * When the app start, this value is reseted (set to null)
     */

    private static MainApplication instance;
    /**
     * Dagger Components
     */

    private static AppComponent appComponent;
    /**
     * Temporary Control Variables
     */

    @Inject BusHandler bus;
    @Inject EventHandler handler;
    private RiotXmppService mService;
    private boolean mBound = false;
    private Intent intentService;
    private DaoSession daoSession;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder serviceBinder) {
            RiotXmppService.MyBinder binder = (RiotXmppService.MyBinder) serviceBinder;
            mService = binder.getService();
            mBound = true;

            /** callback goes to: {@link LoginActivity#onServiceBinded(OnServiceBindedPublish)}  **/
            bus.post(new OnServiceBindedPublish());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    public static MainApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        instance = this;

        setupDatabase();
        initAppComponents();
        appComponent.inject(this);
        File file = new File(AppMiscUtils.getAppSpecificFolder(this).getPath() + "/champion_skins/");

        if (!file.exists())
            file.mkdir();
    }

    private void initAppComponents() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "LolChatRiotDb", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public void startRiotXmppService(String server, String username, String password) {
        intentService = new Intent(this, RiotXmppService.class);
        intentService.putExtra(RiotXmppService.INTENT_SERVER_HOST_CONST, server);
        intentService.putExtra(RiotXmppService.INTENT_SERVER_USERNAME, username);
        intentService.putExtra(RiotXmppService.INTENT_SERVER_PASSWORD, password);

        startService(intentService);
    }

    public RiotXmppService getRiotXmppService() {
        return mService;
    }

    public void bindService() {
        LogUtils.LOGI(TAG, "Enters bindService");
        if (!mBound)
            bindService(intentService, mConnection, BIND_AUTO_CREATE);
        else {
            /** callback goes to: {@link LoginActivity#onServiceBinded(OnServiceBindedPublish)}  **/
            bus.post(new OnServiceBindedPublish());
        }
    }

    public void stopService() {
        unbindService();
        stopService(intentService);
    }

    public void unbindService() {
        if (mConnection != null) {
            if (mBound) {
                unbindService(mConnection);
                mBound = false;
            }
        }
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public boolean isApplicationPausedOrClosed() {
        return handler.isApplicationPaused();
    }
}
