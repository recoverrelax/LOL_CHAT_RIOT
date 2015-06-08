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

public class SnackBarNotification {

    private Context context;

    private String message;
    private String username;
    private String userXmppName;
    private String buttonLabel;
    private NotificationDb notificationDb;
    private Snackbar snackBar;

    public SnackBarNotification(Context context, String message, String buttonLabel, String username, String userXmppName){
        this.context = context;
        this.message = message;
        this.username = username;
        this.userXmppName = userXmppName;
        this.buttonLabel = buttonLabel;
        this.notificationDb = RiotXmppDBRepository.getNotification(MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser());

        sendNotification();
    }


    private void createNewSnackBarNotification() {
        snackBar = Snackbar
                .make(((Activity) context).getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_LONG)
                .setAction(buttonLabel, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(context instanceof BaseActivity)
                            ((BaseActivity)context).goToMessageActivity(username, userXmppName);
                    }
                });
    }

    private void sendNotification(){

        if(notificationDb.getTextNotificationOnline()) {
            createNewSnackBarNotification();
            snackBar.show();
        }
    }
}
