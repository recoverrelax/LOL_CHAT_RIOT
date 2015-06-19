package com.recoverrelax.pt.riotxmppchat;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import com.crashlytics.android.Crashlytics;
import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Login.OnServiceBindedEvent;
import com.recoverrelax.pt.riotxmppchat.MyUtil.MessageSpeechNotification;
import com.recoverrelax.pt.riotxmppchat.MyUtil.storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.ui.activity.LoginActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import org.jivesoftware.smack.AbstractXMPPConnection;

import LolChatRiotDb.DaoMaster;
import LolChatRiotDb.DaoSession;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public class MainApplication extends Application {
    private static final String TAG = MainApplication.class.getSimpleName();

    private RiotXmppService mService;
    private boolean mBound = false;
    private Intent intentService;
    private DaoSession daoSession;
    private Bus bus;

    /**
     * This value is stored as a buffer because its accessed many times.
     * When the app start, this value is reseted (set to null)
     */
    private String connectedXmppUser;

    private static MainApplication instance;
    private int startedActivityCounter = 0;
    private int stoppedActivityCounter = 0;
    private int resumedActivityCOunter = 0;
    private int pausedActivityCounter = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        instance = this;
        DataStorage.init(this);
        MessageSpeechNotification.init(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Roboto-Light.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setupDatabase();

        bus = new Bus(ThreadEnforcer.ANY);
    }

    public void addStartedActivity() {
        startedActivityCounter++;
    }
    public void addStoppedActivity() {
        stoppedActivityCounter++;
    }

    public boolean isApplicationClosed(){
        return startedActivityCounter == stoppedActivityCounter && startedActivityCounter > 0;
    }

    public boolean hasNewMessages(){
        return RiotXmppDBRepository.hasUnreadedMessages(MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser());
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
            getBusInstance().post(new OnServiceBindedEvent());
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

    public AbstractXMPPConnection getConnection(){
        return mService.getConnection();
    }

    public void bindService() {
        if(!mBound)
            bindService(intentService, mConnection, BIND_AUTO_CREATE);
        else {
            /** callback goes to: {@link LoginActivity#onServiceBinded(OnServiceBindedEvent)}  **/
            getBusInstance().post(new OnServiceBindedEvent());
        }
    }

    public void unbindService() {
        if(mConnection != null) {
            if(mBound)
                unbindService(mConnection);
        }
    }

    public static MainApplication getInstance() {
        return instance;
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

    public Bus getBusInstance(){
        return this.bus;
    }
}
