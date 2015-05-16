package com.recoverrelax.pt.riotxmppchat;

import android.app.Application;
import android.content.Context;

import com.recoverrelax.pt.riotxmppchat.MyUtil.storage.DataStorage;

public class MainApplication extends Application {
    private static final String TAG = MainApplication.class.getSimpleName();

    private static MainApplication instance;
    private Context context;

    public Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        DataStorage.init(this);
    }

    public static MainApplication getInstance() {
        return instance;
    }

}
