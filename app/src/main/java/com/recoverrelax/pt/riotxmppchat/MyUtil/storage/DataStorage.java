package com.recoverrelax.pt.riotxmppchat.MyUtil.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.securepreferences.SecurePreferences;

public class DataStorage {
    //<editor-fold desc="Preference Property Names">
    private static final String PREF_KEY_FIRST_RUN = "first_run";
    private static final String PREF_KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    //</editor-fold>

    private static DataStorage sInstance;
    private final SecurePreferences mSettings;

    /**
     * <p>Initialize the {@link DataStorage} singleton</p>
     */
    public static synchronized void init(Context ctx){
        if (sInstance == null) {
            synchronized (DataStorage.class) {
                if (sInstance == null) {
                    sInstance = new DataStorage(ctx);
                }
            }
        }
    }

    private DataStorage(Context context) {
        mSettings = new SecurePreferences(context);
    }

    /**
     * Returns singleton class sInstance
     **/
    public static synchronized DataStorage getInstance() {
        if(sInstance ==null)
            throw new NullPointerException("You must initialized first");
        return sInstance;
    }

    public synchronized boolean isFirstRun(){
        return mSettings.getBoolean(PREF_KEY_FIRST_RUN,true);
    }

    public synchronized boolean userLearnedDrawer(){
        return mSettings.getBoolean(PREF_KEY_USER_LEARNED_DRAWER, false);
    }

    public synchronized boolean setUserLearnedDrawer(){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(PREF_KEY_USER_LEARNED_DRAWER, true);
        return editor.commit();
    }

    public synchronized boolean setRunned(){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(PREF_KEY_FIRST_RUN,false);
        return editor.commit();
    }
}
