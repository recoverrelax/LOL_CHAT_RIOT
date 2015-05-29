package com.recoverrelax.pt.riotxmppchat;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import com.edgelabs.pt.mybaseapp.R;
import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.RiotXmppConnectionImpl;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RiotXmppDataLoaderCallback;

import org.jivesoftware.smack.AbstractXMPPConnection;

import LolChatRiotDb.DaoMaster;
import LolChatRiotDb.DaoSession;
import LolChatRiotDb.NotificationDb;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MainApplication extends Application {
    private static final String TAG = MainApplication.class.getSimpleName();

    private RiotXmppService mService;
    private boolean mBound = false;
    private Intent intentService;
    private ActivityServerCallback activityServerCallback;
    private DaoSession daoSession;

    /**
     * This value is stored as a buffer because its accessed many times.
     * When the app start, this value is reseted (set to null)
     */
    private String connectedXmppUser;

    private static MainApplication instance;
    private boolean isApplicationClosed = true;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        DataStorage.init(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Roboto-Light.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setupDatabase();
    }

    public void settings_init(String connectedXmppUser) {
        if(!RiotXmppDBRepository.defaultSettingsNotificationsSetted(connectedXmppUser)){
            NotificationDb notif = new NotificationDb(null, connectedXmppUser, false, false, false, false);
            RiotXmppDBRepository.insertNotification(notif);
            LogUtils.LOGI(TAG, "Setting NotificationDefaults for the first time");
        }else{
            LogUtils.LOGI(TAG, "NotificationDefaults already setted");
        }
    }

    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "LolChatRiotDb", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    /** Defines callbacks for intentService binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder serviceBinder) {
            RiotXmppService.MyBinder binder = (RiotXmppService.MyBinder) serviceBinder;
            mService = binder.getService();
            mBound = true;

            if(activityServerCallback != null)
                activityServerCallback.onServiceBinded();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    public boolean isApplicationClosed() {
        return isApplicationClosed;
    }

    public void setApplicationClosed(boolean isApplicationClosed) {
        this.isApplicationClosed = isApplicationClosed;
    }

    public void startRiotXmppService(String selectedItem, String username, String password, RiotXmppDataLoaderCallback<RiotXmppConnectionImpl.RiotXmppOperations> loginActilivyCallback){
        intentService = new Intent(this, RiotXmppService.class);
        intentService.putExtra(RiotXmppService.INTENT_SERVER_HOST_CONST, selectedItem);
        intentService.putExtra(RiotXmppService.INTENT_SERVER_USERNAME, username);
        intentService.putExtra(RiotXmppService.INTENT_SERVER_PASSWORD, password);

        RiotXmppService.loginActilivyCallback = loginActilivyCallback;
        startService(intentService);
    }

    public RiotXmppService getRiotXmppService() {
        return mService;
    }

    public AbstractXMPPConnection getConnection(){
        return mService.getConnection();
    }

    public void bindService(ActivityServerCallback callback) {
        this.activityServerCallback = callback;

        if(!mBound)
            bindService(intentService, mConnection, BIND_AUTO_CREATE);
        else {
            if(activityServerCallback != null)
                activityServerCallback.onServiceBinded();
        }
    }

    public void unbindService() {
        if(mConnection != null) {
            unbindService(mConnection);
            this.activityServerCallback = null;
        }
    }

    @Override
    public void onTerminate() {
        if (mBound) {
            unbindService();
            mBound = false;
        }
        super.onTerminate();
    }

    public static MainApplication getInstance() {
        return instance;
    }

    public interface ActivityServerCallback{
        void onServiceBinded();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public String getConnectedXmppUser() {
        return connectedXmppUser;
    }

    public void setConnectedXmppUser(String connectedXmppUser) {
        this.connectedXmppUser = connectedXmppUser;
    }
}
