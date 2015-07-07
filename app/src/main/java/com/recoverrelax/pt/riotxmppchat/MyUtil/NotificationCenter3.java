package com.recoverrelax.pt.riotxmppchat.MyUtil;

import android.support.v4.util.Pair;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewLogEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewMessageEventEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.InAppLogIds;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.DashBoardActivity;

import org.jivesoftware.smack.packet.Message;

import LolChatRiotDb.NotificationDb;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public class NotificationCenter3 extends NotificationCenterHelper2{

    private static final int MESSAGE_NOTIFICATION_ID = 1111111;
    private static final int STATUS_NOTIFICATION_ID  = 2222222;


    private static final int MESSAGE_NOTIFICATION_DRAWABLE = R.drawable.ic_action_question_answer_green;
    private static final int ONLINE_NOTIFICATION_DRAWABLE = R.drawable.ic_online;
    private static final int OFFLINE_NOTIFICATION_DRAWABLE = R.drawable.ic_offline;
    private static final int START_GAME_NOTIFICATION_DRAWABLE = R.drawable.ic_online;
    private static final int LEFT_GAME_NOTIFICATION_DRAWABLE = R.drawable.ic_offline;

    private DataStorage dataStorageInstance;

    private String targetXmppUser;
    private RiotXmppDBRepository riotXmppRepository;

    public NotificationCenter3(String targetXmppUser){
        dataStorageInstance = DataStorage.getInstance();

        this.targetXmppUser = targetXmppUser;
        riotXmppRepository = new RiotXmppDBRepository();
    }

    public void sendOnlineOfflineNotification(String userXmppAddress, OnlineOffline status){

//        saveToLog(status.isOnline() ? InAppLogIds.FRIEND_ONLINE.getOperationId() : InAppLogIds.FRIEND_OFFLINE.getOperationId(),
//                status.isOnline() ? targetUserName + " has went online" : targetUserName + " has went offline");
//
//        String message = status.isOnline() ? "is now online" : "has went offline";
//
//        hSendSystemNotification(targetUserName, "..." + message,
//                status.isOffline() ? OFFLINE_NOTIFICATION_DRAWABLE : ONLINE_NOTIFICATION_DRAWABLE,
//                STATUS_NOTIFICATION_ID,
//                getOnlineOfflineBackgroundTextPermission(notificationDb, status));
//
//        BaseActivity currentOpenedActivity = MainApplication.getInstance().getCurrentOpenedActivity();
//        if(!(currentOpenedActivity instanceof DashBoardActivity))
//        hSendSnackbarNotification(MainApplication.getInstance().getCurrentOpenedActivity(),
//                targetXmppUser,
//                targetUserName,
//                targetUserName + " " + message,
//                "CHAT",
//                getOnlineOfflineForegroundTextPermission(notificationDb, status));
//
//        hSendStatusSpeechNotification(targetUserName + " " + message, getOnlineOfflineSpeechPermission(notificationDb, status));
    }

    public void sendStartedEndedGameNotification(NotificationDb notificationDb, PlayingIddle playingOrIdle){

//        saveToLog(playingOrIdle.startedGame() ? InAppLogIds.FRIEND_STARTED_GAME.getOperationId() : InAppLogIds.FRIEND_ENDED_GAME.getOperationId(),
//                playingOrIdle.startedGame() ? targetUserName + " has left a game" : targetUserName + " has started a game");
//
//        String message = playingOrIdle.startedGame() ? "has started a game" : "has left a game";
//
//        hSendSystemNotification(targetUserName, "..." + message,
//                playingOrIdle.startedGame() ? START_GAME_NOTIFICATION_DRAWABLE : LEFT_GAME_NOTIFICATION_DRAWABLE,
//                STATUS_NOTIFICATION_ID,
//                getStartedLeftGameBackgroundTextPermission(notificationDb, playingOrIdle));
//
//        BaseActivity currentOpenedActivity = MainApplication.getInstance().getCurrentOpenedActivity();
//        if(!(currentOpenedActivity instanceof DashBoardActivity))
//        hSendSnackbarNotification(MainApplication.getInstance().getCurrentOpenedActivity(),
//                targetXmppUser,
//                targetUserName,
//                targetUserName + " " + message,
//                "CHAT",
//                getStartedLeftGameForegroundTextPermission(notificationDb, playingOrIdle));
//
//        hSendStatusSpeechNotification(targetUserName + " " + message, getStartedLeftGameSpeechPermission(notificationDb, playingOrIdle));
    }

    private boolean getOnlineOfflineBackgroundTextPermission(NotificationDb notificationDb, OnlineOffline status) {
        boolean b = MainApplication.getInstance().isApplicationClosed() && dataStorageInstance.getGlobalNotifBackgroundText() &&
                (status.isOnline()
                        ? notificationDb.getIsOnline()
                        : notificationDb.getIsOffline()
                );
        LOGI("1212", b ? "true" : "false");
        return b;
    }

    private boolean getOnlineOfflineForegroundTextPermission(NotificationDb notificationDb, OnlineOffline status) {
        return !isPausedOrClosed() && dataStorageInstance.getGlobalNotifForegroundText() &&
                (status.isOnline()
                        ? notificationDb.getIsOnline()
                        : notificationDb.getIsOffline()
                );
    }

    private boolean getStartedLeftGameBackgroundTextPermission(NotificationDb notificationDb, PlayingIddle playingOrIdle) {
        return isPausedOrClosed() && dataStorageInstance.getGlobalNotifBackgroundText() &&
                (playingOrIdle.startedGame()
                        ? notificationDb.getHasStartedGame()
                        : notificationDb.getHasLefGame()
                );
    }


    private boolean getStartedLeftGameForegroundTextPermission(NotificationDb notificationDb, PlayingIddle playingOrIdle) {
        return !isPausedOrClosed() && dataStorageInstance.getGlobalNotifForegroundText() &&
                (playingOrIdle.startedGame()
                        ? notificationDb.getHasStartedGame()
                        : notificationDb.getHasLefGame()
                );
    }
    private boolean getMessageSpeechPermission(NotificationDb notificationDb) {
        return (isPausedOrClosed() ? dataStorageInstance.getGlobalNotifBackgroundSpeech() : dataStorageInstance.getGlobalNotifForegroundSpeech())
                && notificationDb.getHasSentMePm() && !AppMiscUtils.isPhoneSilenced();
    }
    private boolean getMessageForegroundPermission(NotificationDb notificationDb) {
        return !isPausedOrClosed() && dataStorageInstance.getGlobalNotifForegroundText() && notificationDb.getHasSentMePm();
    }
    private boolean getMessageBackgroundPermission(NotificationDb notificationDb) {
        return isPausedOrClosed() && dataStorageInstance.getGlobalNotifBackgroundText() && notificationDb.getHasSentMePm();
    }
    private boolean getOnlineOfflineSpeechPermission(NotificationDb notificationDb, OnlineOffline status) {
        return (isPausedOrClosed() ? dataStorageInstance.getGlobalNotifBackgroundSpeech() : dataStorageInstance.getGlobalNotifForegroundSpeech())
                && ( status.isOnline() ? notificationDb.getIsOnline() : notificationDb.getIsOffline() )
                && !AppMiscUtils.isPhoneSilenced();
    }

    private boolean getStartedLeftGameSpeechPermission(NotificationDb notificationDb, PlayingIddle playingOrIdle) {
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

    private void saveToLog(Integer logId, String logMessage, String targetXmppUser) {
        new RiotXmppDBRepository().insertOrReplaceInappLog(logId, logMessage, targetXmppUser)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        MainApplication.getInstance().getBusInstance().post(new OnNewLogEvent());
                    }
                });

    }
}
