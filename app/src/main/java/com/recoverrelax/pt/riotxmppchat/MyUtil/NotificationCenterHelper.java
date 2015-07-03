package com.recoverrelax.pt.riotxmppchat.MyUtil;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;
import static junit.framework.Assert.assertTrue;

public abstract class NotificationCenterHelper {

    private final Context applicationContext = MainApplication.getInstance().getApplicationContext();

    protected void hSendSystemNotification(@NonNull String title, @NonNull String message, @DrawableRes int systemNotifId, int notificationId,
                                           boolean hasPermissions){

        if(!hasPermissions) {
            return;
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(applicationContext)
                .setSmallIcon(systemNotifId)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager mNotificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    protected void hSendSnackbarNotification(@Nullable Activity activity, @Nullable String userXmppName, @NonNull String message,
                                                @Nullable String buttonLabel, boolean combinedPermission) {

        if(!combinedPermission || activity == null)
            return;

            Snackbar snackbar = Snackbar
                    .make(activity.getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_LONG);

            if (userXmppName != null && buttonLabel != null) {
                snackbar.setAction(buttonLabel, view -> {
                    String username = MainApplication.getInstance().getRiotXmppService().getRiotRosterManager().getRosterEntry2(userXmppName).getName();
                    if (activity instanceof BaseActivity)
                        ((BaseActivity) activity).goToMessageActivity(username, userXmppName);
                });
            }
            snackbar.show();
    }

    protected void hSendMessageSpeechNotification(String user, String message, boolean combinedPermission){

        if(!combinedPermission)
            return;
        MessageSpeechNotification.getInstance().sendMessageSpeechNotification(message, user);
    }

    protected void hSendStatusSpeechNotification(String message, boolean combinedPermission){

        if(!combinedPermission)
            return;
        MessageSpeechNotification.getInstance().sendStatusSpeechNotification(message);
    }
}
