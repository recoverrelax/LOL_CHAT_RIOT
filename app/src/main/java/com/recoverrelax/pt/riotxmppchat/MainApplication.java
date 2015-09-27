package com.recoverrelax.pt.riotxmppchat;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import com.crashlytics.android.Crashlytics;
import com.recoverrelax.pt.riotxmppchat.EventHandling.OnServiceBindedEvent;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.LoginActivity;
import com.squareup.otto.Bus;

import java.io.File;
import java.lang.ref.WeakReference;

import javax.inject.Inject;

import LolChatRiotDb.DaoMaster;
import LolChatRiotDb.DaoSession;
import io.fabric.sdk.android.Fabric;

public class MainApplication extends Application {
    private static final String TAG = MainApplication.class.getSimpleName();

    private RiotXmppService mService;
    private boolean mBound = false;
    private Intent intentService;
    private DaoSession daoSession;

    @Inject Bus bus;

    /**
     * This value is stored as a buffer because its accessed many times.
     * When the app start, this value is reseted (set to null)
     */

    private static MainApplication instance;
    private WeakReference<BaseActivity> currentBaseActivity = null;

    /**
     * Dagger Components
     */

    private static AppComponent appComponent;




    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        instance = this;

        setupDatabase();
        initAppComponents();
        appComponent.inject(this);
        File file = new File(AppMiscUtils.getAppSpecificFolder(this).getPath() + "/champion_skins/");

        if(!file.exists())
            file.mkdir();


    }

    private void initAppComponents() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent(){
        return appComponent;
    }

    public void setCurrentBaseActivity(BaseActivity baseActivity){
        this.currentBaseActivity = new WeakReference<>(baseActivity);
    }

    public BaseActivity getCurrentBaseActivity(){
        return this.currentBaseActivity.get();
    }

    public boolean isApplicationPausedOrClosed(){
        return getCurrentBaseActivity() == null;
    }

    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "LolChatRiotDb", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder serviceBinder) {
            RiotXmppService.MyBinder binder = (RiotXmppService.MyBinder) serviceBinder;
            mService = binder.getService();
            mBound = true;

            /** callback goes to: {@link LoginActivity#onServiceBinded(OnServiceBindedEvent)}  **/
            bus.post(new OnServiceBindedEvent());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };



    public void startRiotXmppService(String selectedItem, String username, String password){
        intentService = new Intent(this, RiotXmppService.class);
        intentService.putExtra(RiotXmppService.INTENT_SERVER_HOST_CONST, selectedItem);
        intentService.putExtra(RiotXmppService.INTENT_SERVER_USERNAME, username);
        intentService.putExtra(RiotXmppService.INTENT_SERVER_PASSWORD, password);

        startService(intentService);
    }

    public RiotXmppService getRiotXmppService() {
        return mService;
    }

    public void bindService() {
        if(!mBound)
            bindService(intentService, mConnection, BIND_AUTO_CREATE);
        else {
            /** callback goes to: {@link LoginActivity#onServiceBinded(OnServiceBindedEvent)}  **/
            bus.post(new OnServiceBindedEvent());

        }
    }

    public void stopService(){
        unbindService();
        stopService(intentService);
    }


    public void unbindService() {
        if(mConnection != null) {
            if(mBound) {
                unbindService(mConnection);
                mBound = false;
            }
        }
    }



    public static MainApplication getInstance() {
        return instance;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
