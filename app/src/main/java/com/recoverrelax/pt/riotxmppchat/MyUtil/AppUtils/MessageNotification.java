package com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.edgelabs.pt.mybaseapp.R;

public class MessageNotification {

    private static final int ICON_ID = R.drawable.ic_teemo;
    private static final String EMPTY_STRING = "";
    private static final int NOTIFICATION_NEW_MESSAGE_ID = 123456789;

    private static final int NOTIFICATION_TITLE = R.string.message_notification_title;
    private Context context;

    private String message;
    private String messageFrom;
    private NotificationCompat.Builder mBuilder;

    public MessageNotification(Context context, String message, String messageFrom){
        this.context = context;
        this.message = message;
        this.messageFrom = messageFrom;

        createNewNotification();
    }

    private void createNewNotification() {
        mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(ICON_ID)
                        .setContentTitle(messageFrom + ":")
                        .setContentText(message);
    }

    public void sendNotification(){
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_NEW_MESSAGE_ID, mBuilder.build());
    }
}
