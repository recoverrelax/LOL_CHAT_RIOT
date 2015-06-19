package com.recoverrelax.pt.riotxmppchat.MyUtil;

import android.content.Context;
import android.util.Log;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.FriendStatusGameNotificationEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewMessageReceivedEventEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.activity.FriendListActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.RiotXmppCommunicationActivity;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendMessageListFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.PersonalMessageFragment;

import org.jivesoftware.smack.packet.Message;

public class NotificationCenter extends NotificationCenterHelper{

    private Context context;

    public NotificationCenter(Context context){
        this.context = context;
    }

    public void sendMessageNotification(Message message, String userXmppAddress){
        String username = MainApplication.getInstance().getRiotXmppService().getRiotRosterManager().getRoster().getEntry(userXmppAddress).getName();
        String messageString = message.getBody();

        switch (getNotificationType()){
            case SYSTEM_NOTIFICATION:
                String title = username + ":";
                sendNewMessageSystemNotification(title, messageString);
                break;

            case SNACKBAR_NOTIFICATION:
                /**
                 * Deliver the new message to all the observers
                 *
                 * 1st: {@link FriendListActivity#OnNewMessageReceived(OnNewMessageReceivedEventEvent)}  }
                 * 2nd: {@link PersonalMessageFragment#OnNewMessageReceived(OnNewMessageReceivedEventEvent)}  }
                 * 3rd: {@link FriendMessageListFragment#OnNewMessageReceived(OnNewMessageReceivedEventEvent)}  }
                 */
                MainApplication.getInstance().getBusInstance()
                        .post(new OnNewMessageReceivedEventEvent(message, userXmppAddress, username));

                break;
        }

        MessageSpeechNotification.getInstance().sendMessageSpeechNotification(message.getBody(), username);
    }

    public void sendLeftGameNotification(String userXmppAddress){
        String username = MainApplication.getInstance().getRiotXmppService().getRiotRosterManager().getRoster().getEntry(userXmppAddress).getName();

        switch (getNotificationType()){
            case SYSTEM_NOTIFICATION:
                String title = "Has Left the Game";
                sendStatusSystemNotification(username, title);
                break;

            case SNACKBAR_NOTIFICATION:
                /**
                 * 1st: {@link RiotXmppCommunicationActivity#OnFriendStatusGameNotification(FriendStatusGameNotificationEvent)}
                 */
                Log.i("123", "Entered Notification Stopped Playing");
                MainApplication.getInstance().getBusInstance().post(new FriendStatusGameNotificationEvent(username, userXmppAddress));
                break;
        }
        String speechMessage = username + " has left a game";
        MessageSpeechNotification.getInstance().sendStatusSpeechNotification(speechMessage);
    }

    public enum NotificationType {
        SYSTEM_NOTIFICATION,
        SNACKBAR_NOTIFICATION;

        public boolean isSystemNotification(){
            return this.equals(NotificationType.SYSTEM_NOTIFICATION);
        }

        public boolean isSnackbarNotification(){
            return this.equals(NotificationType.SNACKBAR_NOTIFICATION);
        }
    }

    public boolean isApplicationClosed(){
        return MainApplication.getInstance().isApplicationClosed();
    }

    public NotificationType getNotificationType(){
        return isApplicationClosed() ? NotificationType.SYSTEM_NOTIFICATION : NotificationType.SNACKBAR_NOTIFICATION;
    }

    @Override
    public Context getContext(){
        return this.context;
    }

    @Override
    public int getNewMessageSystemNotificationIcon() {
        return R.drawable.ic_action_question_answer_green;
    }

    @Override
    public int getStatusSystemNotificationIcon() {
        return R.drawable.ic_action_question_answer_green;
    }
}
