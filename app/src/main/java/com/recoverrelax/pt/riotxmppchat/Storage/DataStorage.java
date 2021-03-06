package com.recoverrelax.pt.riotxmppchat.Storage;

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
    private static final String PREF_KEY_SHOW_OFFLINE_USERS = "showHideOffline";
    private static final String PREF_KEY_FRIEND_LIST_SORT_MODE = "sortMode";

    /**
     * NOTIFICATION SHARED PREFS
     */
    private static final String PREF_KEY_NOTIFICATION_ALWAYS_ON = "alwaysOn";
    private static final String PREF_KEY_NOTIFICATION_GLOBAL_FOREG_TEXT = "foreg_text";
    private static final String PREF_KEY_NOTIFICATION_GLOBAL_FOREG_SPEECH = "foreg_speech";

    private static final String PREF_KEY_NOTIFICATION_GLOBAL_BACKG_TEXT = "backg_text";
    private static final String PREF_KEY_NOTIFICATION_GLOBAL_BACKG_SPEECH = "backg_speech";

    //    private static DataStorage sInstance;
    private final SecurePreferences mSettings;

//    /**
//     * <p>Initialize the {@link DataStorage} singleton</p>
//     */
//    public static synchronized void init(Context ctx){
//        if (sInstance == null) {
//            synchronized (DataStorage.class) {
//                if (sInstance == null) {
//                    sInstance = new DataStorage(ctx);
//                }
//            }
//        }
//    }

    public DataStorage(Context context) {
        mSettings = new SecurePreferences(context);
    }

    public synchronized boolean isFirstRun() {
        return mSettings.getBoolean(PREF_KEY_FIRST_RUN, true);
    }

    public synchronized boolean userLearnedDrawer() {
        return mSettings.getBoolean(PREF_KEY_USER_LEARNED_DRAWER, false);
    }

    public synchronized boolean setUserLearnedDrawer() {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(PREF_KEY_USER_LEARNED_DRAWER, true);
        return editor.commit();
    }

    public synchronized boolean showOfflineUsers() {
        return mSettings.getBoolean(PREF_KEY_SHOW_OFFLINE_USERS, false);
    }

    public synchronized boolean showOfflineUsers(boolean state) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(PREF_KEY_SHOW_OFFLINE_USERS, state);
        return editor.commit();
    }

    public synchronized int getSortMode() {
        return mSettings.getInt(PREF_KEY_FRIEND_LIST_SORT_MODE, 1);
    }

    public synchronized boolean setSortMode(int sortMode) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(PREF_KEY_FRIEND_LIST_SORT_MODE, sortMode);
        return editor.commit();
    }

    public synchronized String getUsername() {
        return mSettings.getString(PREF_KEY_USERNAME, null);
    }

    public synchronized boolean setUsername(String username) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(PREF_KEY_USERNAME, username);
        return editor.commit();
    }

    public synchronized String getPassword() {
        return mSettings.getString(PREF_KEY_PASSWORD, null);
    }

    public synchronized boolean setPassword(String password) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(PREF_KEY_PASSWORD, password);
        return editor.commit();
    }

    public synchronized boolean getSaveLoginCredentials() {
        return mSettings.getBoolean(PREF_KEY_SAVE_LOGIN_CREDENTIALS, false);
    }

    public synchronized boolean setSaveLoginCredentials(boolean state) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(PREF_KEY_SAVE_LOGIN_CREDENTIALS, state);
        return editor.commit();
    }

    public synchronized String getServer() {
        return mSettings.getString(PREF_KEY_SERVER, RiotServer.EUW.getServerName());
    }

    public synchronized boolean setServer(String serverName) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(PREF_KEY_SERVER, serverName);
        return editor.commit();
    }

    public synchronized boolean setRunned() {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(PREF_KEY_FIRST_RUN, false);
        return editor.commit();
    }

    /**
     * NOTIFICATIONS SHARED PREFERENCES
     */


    public synchronized boolean getNotificationsAlwaysOn() {
        return mSettings.getBoolean(PREF_KEY_NOTIFICATION_ALWAYS_ON, true);
    }

    public synchronized boolean setNotificationsAlwaysOn(boolean state) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(PREF_KEY_NOTIFICATION_ALWAYS_ON, state);
        return editor.commit();
    }

    public synchronized boolean getGlobalNotifForegroundText() {
        return mSettings.getBoolean(PREF_KEY_NOTIFICATION_GLOBAL_FOREG_TEXT, true);
    }

    public synchronized boolean setGlobalNotifForegroundText(boolean state) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(PREF_KEY_NOTIFICATION_GLOBAL_FOREG_TEXT, state);
        return editor.commit();
    }

    public synchronized boolean getGlobalNotifForegroundSpeech() {
        return mSettings.getBoolean(PREF_KEY_NOTIFICATION_GLOBAL_FOREG_SPEECH, true);
    }

    public synchronized boolean setGlobalNotifForegroundSpeech(boolean state) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(PREF_KEY_NOTIFICATION_GLOBAL_FOREG_SPEECH, state);
        return editor.commit();
    }

    /**
     *
     */

    public synchronized boolean getGlobalNotifBackgroundText() {
        return mSettings.getBoolean(PREF_KEY_NOTIFICATION_GLOBAL_BACKG_TEXT, true);
    }

    public synchronized boolean setGlobalNotifBackgroundText(boolean state) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(PREF_KEY_NOTIFICATION_GLOBAL_BACKG_TEXT, state);
        return editor.commit();
    }

    public synchronized boolean getGlobalNotifBackgroundSpeech() {
        return mSettings.getBoolean(PREF_KEY_NOTIFICATION_GLOBAL_BACKG_SPEECH, true);
    }

    public synchronized boolean setGlobalNotifBackgroundSpeech(boolean state) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(PREF_KEY_NOTIFICATION_GLOBAL_BACKG_SPEECH, state);
        return editor.commit();
    }


}
