package com.recoverrelax.pt.riotxmppchat.MyUtil;

import android.app.Activity;
import android.support.design.widget.Snackbar;

import com.recoverrelax.pt.riotxmppchat.MainApplication;

public class NewMessageNotification extends BaseNotification {

    private Activity activity;
    private String username;
    private String userXmppName;
    private String message;
    private static final String BUTTON_LABEL = "CHAT";

    public NewMessageNotification(Activity activity, String username, String userXmppName, String message){
        this.activity = activity;
        this.username = username;
        this.userXmppName = userXmppName;
        this.message = message;

    }

    public void sendNotification(){
        if(MainApplication.getInstance().getRiotXmppService().getConnection().isConnected()){
            if (MainApplication.getInstance().isApplicationClosed()) {
                sendSystemNotification(username + ":", message);
            } else {
                sendSnackBarNotification(username + ": \n" + message, Snackbar.LENGTH_LONG, BUTTON_LABEL, username, userXmppName);
            }
        }
    }

    @Override
    public Activity getActivity() {
        return activity;
    }
}
