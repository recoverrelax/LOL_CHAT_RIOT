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
    private static final int START_GAME_NOTIFICATION_DRAWABLE = R.drawable.ic_online;
    private static final int LEFT_GAME_NOTIFICATION_DRAWABLE = R.drawable.ic_offline;

    private DataStorage dataStorageInstance;
    private NotificationDb notificationDb;

    private String connectedXmppUser;
    private String targetXmppUser;
    private String targetUserName;

    public NotificationCenter(String targetXmppUser){
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

    public void sendStartedEndedGameNotification(PlayingIddle playingOrIdle){

        String message = playingOrIdle.startedGame() ? "has started a game" : "has left a game";

        hSendSystemNotification(targetUserName, "..." + message,
                playingOrIdle.startedGame() ? START_GAME_NOTIFICATION_DRAWABLE : LEFT_GAME_NOTIFICATION_DRAWABLE,
                STATUS_NOTIFICATION_ID,
                getStartedLeftGameBackgroundTextPermission(playingOrIdle));

        hSendSnackbarNotification(MainApplication.getInstance().getCurrentOpenedActivity(),
                targetXmppUser,
                targetUserName + " " + message,
                "CHAT",
                getStartedLeftGameForegroundTextPermission(playingOrIdle));

        hSendStatusSpeechNotification(targetUserName + " " + message, getStartedLeftGameSpeechPermission(playingOrIdle));
    }



    private boolean getOnlineOfflineBackgroundTextPermission(OnlineOffline status) {
        boolean b = MainApplication.getInstance().isApplicationClosed() && dataStorageInstance.getGlobalNotifBackgroundText() &&
                (status.isOnline()
                        ? notificationDb.getIsOnline()
                        : notificationDb.getIsOffline()
                );
        LOGI("1212", b ? "true" : "false");
        return b;
    }

    private boolean getStartedLeftGameBackgroundTextPermission(PlayingIddle playingOrIdle) {
        return isPausedOrClosed() && dataStorageInstance.getGlobalNotifBackgroundText() &&
                (playingOrIdle.startedGame()
                        ? notificationDb.getHasStartedGame()
                        : notificationDb.getHasLefGame()
                );
    }
    private boolean getOnlineOfflineForegroundTextPermission(OnlineOffline status) {
        return !isPausedOrClosed() && dataStorageInstance.getGlobalNotifForegroundText() &&
                (status.isOnline()
                ? notificationDb.getIsOnline()
                : notificationDb.getIsOffline()
                );
    }

    private boolean getStartedLeftGameForegroundTextPermission(PlayingIddle playingOrIdle) {
        return !isPausedOrClosed() && dataStorageInstance.getGlobalNotifForegroundText() &&
                (playingOrIdle.startedGame()
                        ? notificationDb.getHasStartedGame()
                        : notificationDb.getHasLefGame()
                );
    }
    private boolean getMessageSpeechPermission() {
        return (isPausedOrClosed() ? dataStorageInstance.getGlobalNotifBackgroundSpeech() : dataStorageInstance.getGlobalNotifForegroundSpeech())
                && notificationDb.getHasSentMePm() && !AppMiscUtils.isPhoneSilenced();
    }
    private boolean getMessageForegroundPermission() {
        return !isPausedOrClosed() && dataStorageInstance.getGlobalNotifForegroundText() && notificationDb.getHasSentMePm();
    }
    private boolean getMessageBackgroundPermission() {
        return isPausedOrClosed() && dataStorageInstance.getGlobalNotifBackgroundText() && notificationDb.getHasSentMePm();
    }
    private boolean getOnlineOfflineSpeechPermission(OnlineOffline status) {
        return (isPausedOrClosed() ? dataStorageInstance.getGlobalNotifBackgroundSpeech() : dataStorageInstance.getGlobalNotifForegroundSpeech())
                && ( status.isOnline() ? notificationDb.getIsOnline() : notificationDb.getIsOffline() )
                && !AppMiscUtils.isPhoneSilenced();
    }

    private boolean getStartedLeftGameSpeechPermission(PlayingIddle playingOrIdle) {
        return (isPausedOrClosed() ? dataStorageInstance.getGlobalNotifBackgroundSpeech() : dataStorageInstance.getGlobalNotifForegroundSpeech())
                && ( playingOrIdle.startedGame() ? notificationDb.getHasStartedGame() : notificationDb.getHasLefGame() )
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

    public boolean isPausedOrClosed(){
        return MainApplication.getInstance().isApplicationClosed()
                || MainApplication.getInstance().isApplicationClosed();
    }

    public enum PlayingIddle{
        STARTED_GAME,
        ENDED_GAME;

        public boolean startedGame(){
            return this.equals(PlayingIddle.STARTED_GAME);
        }

        public boolean endedGame(){
            return this.equals(PlayingIddle.ENDED_GAME);
        }
    }
}
