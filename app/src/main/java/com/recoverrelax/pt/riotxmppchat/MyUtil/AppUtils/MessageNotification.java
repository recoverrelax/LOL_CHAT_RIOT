package com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.edgelabs.pt.mybaseapp.R;
import com.github.mrengineer13.snackbar.SnackBar;
import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.ui.activity.FriendListActivity;

import LolChatRiotDb.NotificationDb;

public class MessageNotification implements SnackBar.OnMessageClickListener {

    private static final int ICON_ID = R.drawable.ic_teemo;
    private static final String EMPTY_STRING = "";
    private static final int NOTIFICATION_NEW_MESSAGE_ID = 123456789;

    private static final int NOTIFICATION_TITLE = R.string.message_notification_title;
    private Context context;

    private String message;
    private String username;
    private String userXmppNAme;
    private NotificationCompat.Builder mBuilder;
    private NotificationType notifType;
    private SnackBar.Builder builder;
    private NotificationDb notificationDb;

    public MessageNotification(Context context, String message, String username, String userXmppName, NotificationType notifType){
        this.context = context;
        this.message = message;
        this.username = username;
        this.notifType = notifType;
        this.userXmppNAme = userXmppName;
        this.notificationDb = RiotXmppDBRepository.getNotification(MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser());

        sendNotification();
    }

    private void createNewSystemNotification() {
            mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(ICON_ID)
                            .setContentTitle(username + ":")
                            .setContentText(message);
    }

    private void createNewSnackBarNotification() {
            builder = new SnackBar.Builder((Activity) context)
                    .withMessage(username + "\n" + message)
                    .withTextColorId(R.color.white)
                    .withOnClickListener(this)
                    .withActionMessage("GO")
                    .withBackgroundColorId(R.color.primaryColor)
                    .withDuration((short) 3000);
    }

    private void sendSystemNotification(){

        if(notificationDb.getTextNotificationOffline()) {
            createNewSystemNotification();
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(NOTIFICATION_NEW_MESSAGE_ID, mBuilder.build());
        }
    }

    private void sendSnackBarNotification(){

        if(notificationDb.getTextNotificationOnline()) {
            createNewSnackBarNotification();
            builder.show();
        }
    }

    public void sendNotification(){
        if(notifType.equals(NotificationType.SNACKBAR))
            sendSnackBarNotification();
        else if(notifType.equals(NotificationType.SYSTEM_NOTIFICATION))
            sendSystemNotification();
    }

    @Override
    public void onMessageClick(Parcelable parcelable) {
        if(context instanceof FriendListActivity){
            ((FriendListActivity) context).replaceFragment(username, userXmppNAme);
        }
    }

    public enum NotificationType {
        SNACKBAR,
        SYSTEM_NOTIFICATION
    }
}