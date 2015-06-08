package com.recoverrelax.pt.riotxmppchat.MyUtil;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.view.View;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;

import LolChatRiotDb.NotificationDb;

public class SystemNotification {

    private static final int ICON_ID = R.drawable.ic_teemo;
    private static final int NOTIFICATION_NEW_MESSAGE_ID = 123456789;

    private Context context;

    private String message;
    private String title;
    private NotificationCompat.Builder mBuilder;
    private NotificationDb notificationDb;

    public SystemNotification(Context context, String message, String title){
        this.context = context;
        this.message = message;
        this.title = title;
        this.notificationDb = RiotXmppDBRepository.getNotification(MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser());

        sendNotification();
    }

    private void createNewSystemNotification() {
            mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(ICON_ID)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setPriority(NotificationCompat.PRIORITY_HIGH);
    }

    private void sendNotification(){

        if(notificationDb.getTextNotificationOffline()) {
            createNewSystemNotification();
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(NOTIFICATION_NEW_MESSAGE_ID, mBuilder.build());
        }
    }
}
