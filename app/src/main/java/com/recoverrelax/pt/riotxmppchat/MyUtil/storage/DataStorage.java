package com.recoverrelax.pt.riotxmppchat.MyUtil.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotServer;
import com.securepreferences.SecurePreferences;

public class DataStorage {
    //<editor-fold desc="Preference Property Names">
    private static final String PREF_KEY_FIRST_RUN = "first_run";
    private static final String PREF_KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    //</editor-fold>

    private static final String PREF_KEY_USERNAME = "username";
    private static final String PREF_KEY_PASSWORD = "password";
    private static final String PREF_KEY_SAVE_LOGIN_CREDENTIALS = "save_login_credentials";
    private static final String PREF_KEY_SERVER = "server";
    private static final String PREF_KEY_ALWAYS_ON = "alwaysOn";
    private static final String PREF_KEY_SHOW_OFFLINE_USERS = "showHideOffline";

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

    public synchronized boolean getAppAlwaysOn(){
        return mSettings.getBoolean(PREF_KEY_ALWAYS_ON, true);
    }

    public synchronized boolean setAppAlwaysOn(boolean state){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(PREF_KEY_ALWAYS_ON, state);
        return editor.commit();
    }

    public synchronized boolean showOfflineUsers(){
        return mSettings.getBoolean(PREF_KEY_SHOW_OFFLINE_USERS, true);
    }

    public synchronized boolean showOfflineUsers(boolean state){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(PREF_KEY_SHOW_OFFLINE_USERS, state);
        return editor.commit();
    }

    public synchronized String getUsername(){
        return mSettings.getString(PREF_KEY_USERNAME, null);
    }

    public synchronized boolean setUsername(String username){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(PREF_KEY_USERNAME, username);
        return editor.commit();
    }

    public synchronized String getPassword(){
        return mSettings.getString(PREF_KEY_PASSWORD, null);
    }

    public synchronized boolean setPassword(String password){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(PREF_KEY_PASSWORD, password);
        return editor.commit();
    }

    public synchronized boolean getSaveLoginCredentials(){
        return mSettings.getBoolean(PREF_KEY_SAVE_LOGIN_CREDENTIALS, false);
    }

    public synchronized boolean setSaveLoginCredentials(boolean state){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(PREF_KEY_SAVE_LOGIN_CREDENTIALS, state);
        return editor.commit();
    }

    public synchronized String getServer(){
        return mSettings.getString(PREF_KEY_SERVER, RiotServer.EUW.getServerName());
    }

    public synchronized boolean setServer(String serverName){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(PREF_KEY_SERVER, serverName);
        return editor.commit();
    }

    public synchronized boolean setRunned(){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(PREF_KEY_FIRST_RUN,false);
        return editor.commit();
    }
}
