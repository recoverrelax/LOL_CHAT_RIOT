package com.recoverrelax.pt.riotxmppchat.MyUtil;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.R;
import LolChatRiotDb.NotificationDb;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public class NotificationCenter extends NotificationCenterHelper{

    private static final int MESSAGE_NOTIFICATION_ID = 1111111;
    private static final int STATUS_NOTIFICATION_ID  = 2222222;


    private static final int MESSAGE_NOTIFICATION_DRAWABLE = R.drawable.ic_action_question_answer_green;
    private static final int ONLINE_NOTIFICATION_DRAWABLE = R.drawable.ic_online;
    private static final int OFFLINE_NOTIFICATION_DRAWABLE = R.drawable.ic_offline;

    private boolean isAppClosedOrBg = true;
    private DataStorage dataStorageInstance;
    private NotificationDb notificationDb;

    private String connectedXmppUser;
    private String targetXmppUser;
    private String targetUserName;

    public NotificationCenter(String targetXmppUser){
        this.isAppClosedOrBg = MainApplication.getInstance().isApplicationClosed();
        dataStorageInstance = DataStorage.getInstance();

        this.connectedXmppUser = MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser();
        this.targetXmppUser = targetXmppUser;
        this.notificationDb = RiotXmppDBRepository.getNotificationByUser(connectedXmppUser, targetXmppUser);
        this.targetUserName = MainApplication.getInstance().getRiotXmppService().getRiotRosterManager().getRosterEntry(this.targetXmppUser).getName();
    }

    public void sendMessageNotification(String message){

        hSendSystemNotification(targetUserName + " says:", message, MESSAGE_NOTIFICATION_DRAWABLE, MESSAGE_NOTIFICATION_ID,
                getMessageBackgroundPermission());

        hSendSnackbarNotification(MainApplication.getInstance().getCurrentOpenedActivity(),
                targetXmppUser,
                targetUserName + "says: \n" + message,
                "CHAT",
                getMessageForegroundPermission());

        hSendMessageSpeechNotification(targetUserName, message,
                getMessageSpeechPermission());
    }

    public void sendOnlineOfflineNotification(OnlineOffline status){

        String message = status.isOnline() ? "is now online" : "has went offline";

        hSendSystemNotification(targetUserName, "..." + message,
                status.isOffline() ? OFFLINE_NOTIFICATION_DRAWABLE : ONLINE_NOTIFICATION_DRAWABLE,
                STATUS_NOTIFICATION_ID,
                getOnlineOfflineBackgroundTextPermission(status));

        hSendSnackbarNotification(MainApplication.getInstance().getCurrentOpenedActivity(),
                targetXmppUser,
                targetUserName + " " + message,
                "CHAT",
                getOnlineOfflineForegroundTextPermission(status));

        hSendStatusSpeechNotification(targetUserName + " " + message, getOnlineOfflineSpeechPermission(status));
    }



    private boolean getOnlineOfflineBackgroundTextPermission(OnlineOffline status) {
        boolean b = isAppClosedOrBg && dataStorageInstance.getGlobalNotifBackgroundText() &&
                (status.isOnline()
                        ? notificationDb.getIsOnline()
                        : notificationDb.getIsOffline()
                );
        LOGI("1212", b ? "true" : "false");
        return b;
    }
    private boolean getOnlineOfflineForegroundTextPermission(OnlineOffline status) {
        return !isAppClosedOrBg && dataStorageInstance.getGlobalNotifForegroundText() &&
                (status.isOnline()
                ? notificationDb.getIsOnline()
                : notificationDb.getIsOffline()
                );
    }
    private boolean getMessageSpeechPermission() {
        return (isAppClosedOrBg ? dataStorageInstance.getGlobalNotifBackgroundSpeech() : dataStorageInstance.getGlobalNotifForegroundSpeech())
                && notificationDb.getHasSentMePm() && !AppMiscUtils.isPhoneSilenced();
    }
    private boolean getMessageForegroundPermission() {
        return !isAppClosedOrBg && dataStorageInstance.getGlobalNotifForegroundText() && notificationDb.getHasSentMePm();
    }
    private boolean getMessageBackgroundPermission() {
        return isAppClosedOrBg && dataStorageInstance.getGlobalNotifBackgroundText() && notificationDb.getHasSentMePm();
    }
    private boolean getOnlineOfflineSpeechPermission(OnlineOffline status) {
        return (isAppClosedOrBg ? dataStorageInstance.getGlobalNotifBackgroundSpeech() : dataStorageInstance.getGlobalNotifForegroundSpeech())
                && ( status.isOnline() ? notificationDb.getIsOnline() : notificationDb.getIsOffline() )
                && !AppMiscUtils.isPhoneSilenced();
    }

    public enum OnlineOffline{
        ONLINE,
        OFFLINE;

        public boolean isOnline(){
            return this.equals(OnlineOffline.ONLINE);
        }

        public boolean isOffline(){
            return !isOnline();
        }
    }
}
