package com.recoverrelax.pt.riotxmppchat.MyUtil;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;

import static junit.framework.Assert.assertTrue;

public abstract class NotificationCenterHelper {

    private static final int NEW_MESSAGE_NOTIFICATION_ID = 544565;

    protected void sendNewMessageSystemNotification(String title, String message){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext())
                .setSmallIcon(getNewMessageSystemNotificationIcon())
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NEW_MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }

    protected void sendStatusSystemNotification(String title, String message){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext())
                .setSmallIcon(getNewMessageSystemNotificationIcon())
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NEW_MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }

    public static void sendNewMessageSnackbarNotification(Activity activity, String message, String username, String userXmppName, String buttonLabel) {
        String finalMessage = username + " said: " + message;

        Snackbar
                .make(activity.getWindow().getDecorView().getRootView(), finalMessage, Snackbar.LENGTH_LONG)
                .setAction(buttonLabel, view -> {
                    if (activity instanceof BaseActivity)
                        ((BaseActivity) activity).goToMessageActivity(username, userXmppName);
                }).show();
    }

    public static void sendGameSnackbarNotificationNoAction(Activity activity, String message) {
        Snackbar
                .make(activity.getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_LONG)
                .show();
    }

    public static void sendNewMessageSnackbarNotificationNoAction(Activity activity, String message, String username) {
        String finalMessage = username + " said: " + message;

        Snackbar
                .make(activity.getWindow().getDecorView().getRootView(), finalMessage, Snackbar.LENGTH_LONG)
                .show();
    }

    public abstract Context getContext();

    public Activity getActivity(){
        assertTrue("In order to send a SnackBar notification, you need to pass in Activity context", getContext() instanceof Activity);
        return (Activity) getContext();
    }

    public abstract @DrawableRes int getNewMessageSystemNotificationIcon();
    public abstract @DrawableRes int getStatusSystemNotificationIcon();
}
