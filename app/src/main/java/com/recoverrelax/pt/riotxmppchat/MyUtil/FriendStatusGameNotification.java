package com.recoverrelax.pt.riotxmppchat.MyUtil;

import android.app.Activity;
import android.support.design.widget.Snackbar;

import com.recoverrelax.pt.riotxmppchat.MainApplication;

public class FriendStatusGameNotification extends BaseNotification{

    private Activity activity;
    private String username;
    private String userXmppName;
    private static final String BUTTON_LABEL = "CHAT";

    private boolean hasStarted;

    public FriendStatusGameNotification(Activity activity, String username, String userXmppName, boolean hasStarted){
        this.activity = activity;
        this.username = username;
        this.userXmppName = userXmppName;
        this.hasStarted = hasStarted;
    }

    public void sendNotification(){
        if(MainApplication.getInstance().getRiotXmppService().getConnection().isConnected()){
            String message = hasStarted ? " has started a game" : " has left the game";
            if (MainApplication.getInstance().isApplicationClosed()) {
                sendSystemNotification(username, message);
            } else {
                sendSnackBarNotification(username + message, Snackbar.LENGTH_LONG, BUTTON_LABEL, username, userXmppName);
            }
        }
    }

    @Override
    public Activity getActivity() {
        return activity;
    }

}
