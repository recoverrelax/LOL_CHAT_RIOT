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
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.MessageSpeechNotification;
import com.recoverrelax.pt.riotxmppchat.MyUtil.storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.LoginActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import org.jivesoftware.smack.AbstractXMPPConnection;

import java.io.File;

import LolChatRiotDb.DaoMaster;
import LolChatRiotDb.DaoSession;
import io.fabric.sdk.android.Fabric;
import rx.Observable;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public class MainApplication extends Application {
    private static final String TAG = MainApplication.class.getSimpleName();

    private RiotXmppService mService;
    private boolean mBound = false;
    private Intent intentService;
    private DaoSession daoSession;
    private Bus bus;

    private BaseActivity baseActivity;

    /**
     * This value is stored as a buffer because its accessed many times.
     * When the app start, this value is reseted (set to null)
     */

    private static MainApplication instance;
    private int resumedActivityCounter = 0;
    private int pausedActivityCounter = 0;
    private int startedActivityCounter = 0;
    private int stoppedActivityCounter = 0;

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

        File file = new File(AppMiscUtils.getAppSpecificFolder(this).getPath() + "/champion_skins/");

        if(!file.exists())
            file.mkdir();
    }

    public void addResumedActivity() {
        resumedActivityCounter = resumedActivityCounter+1;
        printActivityState();
    }
    public void addPausedActivity() {
        pausedActivityCounter = pausedActivityCounter+1;
        printActivityState();
    }

    public void addStartedActivity() {
        startedActivityCounter = startedActivityCounter+1;
        printActivityState();
    }
    public void addStoppedActivity() {
        stoppedActivityCounter = stoppedActivityCounter+1;
        printActivityState();
    }

    public void printActivityState(){
//        LOGI("1111", isApplicationPaused() ? "isPaused? yes" : "isPaused? no");
//        LOGI("1111", isApplicationClosed() ? "isClosed? yes" : "isClosed? no");
//        LOGI("1111", !isApplicationClosed() && !isApplicationPaused() ? "isOppened? yes" : "isOppened? no");
//        LOGI("1111", "\n\n");
    }

    public boolean isApplicationClosed(){
        return resumedActivityCounter == pausedActivityCounter && startedActivityCounter == stoppedActivityCounter;
    }

    public boolean isApplicationPaused(){
        return resumedActivityCounter == pausedActivityCounter && startedActivityCounter > stoppedActivityCounter;
    }

    public BaseActivity getCurrentOpenedActivity(){
        return this.baseActivity;
    }

    public BaseActivity setBaseActivity(BaseActivity act){
        return this.baseActivity = act;
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

    public Bus getBusInstance(){
        return this.bus;
    }

}
