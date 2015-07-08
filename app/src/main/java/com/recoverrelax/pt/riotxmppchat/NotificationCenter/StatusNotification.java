package com.recoverrelax.pt.riotxmppchat.NotificationCenter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;

import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.EventHandling.OnNewFriendPlayingEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.InAppLogIds;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;

import LolChatRiotDb.NotificationDb;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils.LOGI;

public class StatusNotification extends NotificationHelper{
    private RiotXmppDBRepository riotXmppRepository;
    private DataStorage dataStorageInstance;
    private String userXmppAddress;
    private String username;
    private NotificationDb notificationDb;
    private Status status;

    private static final int STATUS_NOTIFICATION_ID  = 2222222;

    private static final int ONLINE_NOTIFICATION_DRAWABLE = R.drawable.ic_online;
    private static final int OFFLINE_NOTIFICATION_DRAWABLE = R.drawable.ic_offline;
    private static final int START_GAME_NOTIFICATION_DRAWABLE = R.drawable.ic_online;
    private static final int LEFT_GAME_NOTIFICATION_DRAWABLE = R.drawable.ic_offline;

    public StatusNotification(String userXmppAddress, String username2, Status status){
        riotXmppRepository = new RiotXmppDBRepository();
        dataStorageInstance = DataStorage.getInstance();
        this.userXmppAddress = userXmppAddress;
        this.status = status;
        this.username = username2;
    }

    public void sendStatusNotification(){
        riotXmppRepository.getNotificationByUser(userXmppAddress)
                .doOnNext(notification -> {
                    this.notificationDb = notification;

                    if(status.isStartedGame() || status.isLeftGame())
                        MainApplication.getInstance().getBusInstance().post(new OnNewFriendPlayingEvent());

                    int logId = getLogIdFromStatus(status);
                    String logMessage = getLogMessageFromStatus(status, username);
                    boolean speechPermission = getSpeechPermissionFromStatus(status);

                    saveToLog(logId, logMessage, userXmppAddress);
                    hSendStatusSpeechNotification(logMessage, speechPermission);

                })
                .flatMap(notification -> {
                    if (isPausedOrClosed()) {
                        String systemNotificationMessage = getMessageFromStatus(status);
                        int notificationDrawable = getNotificationDrawableFromStatus(status);
                        boolean statusPermission = getStatusPermissionFromStatus(status);

                        return sendSystemNotification(username, systemNotificationMessage, notificationDrawable, STATUS_NOTIFICATION_ID,
                                statusPermission);
                    } else {
                            sendSnackbarNotification(MainApplication.getInstance().getCurrentBaseActivity());
                        return Observable.just(true);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override public void onCompleted() { }
                    @Override public void onError(Throwable e) { }
                    @Override public void onNext(Boolean aBoolean) { }
                });
    }

    public void sendSnackbarNotification(@Nullable Activity activity) {

        boolean permission = getStatusPermissionFromStatus(status);

        if(!permission || activity == null)
            return;

        String buttonLabel = "CHAT";
        String message = username + " " + getMessageFromStatus(status);



                    Snackbar snackbar = Snackbar
                            .make(activity.getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_LONG);

                    if (userXmppAddress != null && username != null) {
                        snackbar.setAction(buttonLabel, view -> {
                            if (activity instanceof BaseActivity)
                                ((BaseActivity) activity).goToMessageActivity(username, userXmppAddress);
                        });
                    }
                    snackbar.show();
    }

    private int getLogIdFromStatus(Status status){
        switch (status){
            case ONLINE:
                return InAppLogIds.FRIEND_ONLINE.getOperationId();
            case OFFLINE:
                return InAppLogIds.FRIEND_OFFLINE.getOperationId();
            case STARTED_GAME:
                return InAppLogIds.FRIEND_STARTED_GAME.getOperationId();
            case LEFT_GAME:
                return InAppLogIds.FRIEND_ENDED_GAME.getOperationId();
            default:
                return InAppLogIds.FRIEND_OFFLINE.getOperationId();
        }
    }

    private String getLogMessageFromStatus(Status status, String username){
        return username + " " + getMessageFromStatus(status);
    }

    private String getMessageFromStatus(Status status){
        switch (status){
            case ONLINE:
                return "is now Online";
            case OFFLINE:
                return "has went Offline";
            case STARTED_GAME:
                return "has started a game";
            case LEFT_GAME:
                return "has left a game";
            default:
                return "has went Offline";
        }
    }

    private boolean getSpeechPermissionFromStatus(Status status){

        if(notificationDb == null)
            return false;

        switch (status){
            case ONLINE:
                return (isPausedOrClosed() ? dataStorageInstance.getGlobalNotifBackgroundSpeech() : dataStorageInstance.getGlobalNotifForegroundSpeech())
                        && notificationDb.getIsOnline()
                        && !AppMiscUtils.isPhoneSilenced();

            case OFFLINE:
                return (isPausedOrClosed() ? dataStorageInstance.getGlobalNotifBackgroundSpeech() : dataStorageInstance.getGlobalNotifForegroundSpeech())
                        && notificationDb.getIsOffline()
                        && !AppMiscUtils.isPhoneSilenced();

            case STARTED_GAME:
                return (isPausedOrClosed() ? dataStorageInstance.getGlobalNotifBackgroundSpeech() : dataStorageInstance.getGlobalNotifForegroundSpeech())
                        && notificationDb.getHasStartedGame()
                        && !AppMiscUtils.isPhoneSilenced();

            case LEFT_GAME:
                return (isPausedOrClosed() ? dataStorageInstance.getGlobalNotifBackgroundSpeech() : dataStorageInstance.getGlobalNotifForegroundSpeech())
                        && notificationDb.getHasLefGame()
                        && !AppMiscUtils.isPhoneSilenced();
            default:
                return (isPausedOrClosed() ? dataStorageInstance.getGlobalNotifBackgroundSpeech() : dataStorageInstance.getGlobalNotifForegroundSpeech())
                        && notificationDb.getIsOffline()
                        && !AppMiscUtils.isPhoneSilenced();
        }
    }

    private int getNotificationDrawableFromStatus(Status status){
        switch (status){
            case ONLINE:
                return ONLINE_NOTIFICATION_DRAWABLE;
            case OFFLINE:
                return OFFLINE_NOTIFICATION_DRAWABLE;
            case STARTED_GAME:
                return START_GAME_NOTIFICATION_DRAWABLE;
            case LEFT_GAME:
                return LEFT_GAME_NOTIFICATION_DRAWABLE;
            default:
                return ONLINE_NOTIFICATION_DRAWABLE;
        }
    }

    private boolean getStatusPermissionFromStatus(Status status){
        switch (status){
            case ONLINE:
            case OFFLINE:
                return (isPausedOrClosed() ? dataStorageInstance.getGlobalNotifBackgroundText() : dataStorageInstance.getGlobalNotifForegroundText()) &&
                        (status.isOnline()
                                ? notificationDb.getIsOnline()
                                : notificationDb.getIsOffline()
                        );
            case STARTED_GAME:
            case LEFT_GAME:
                return (isPausedOrClosed()) ? dataStorageInstance.getGlobalNotifBackgroundText() : dataStorageInstance.getGlobalNotifForegroundText() &&
                        (status.isStartedGame()
                                ? notificationDb.getHasStartedGame()
                                : notificationDb.getHasLefGame()
                        );
            default:
                return (isPausedOrClosed() ? dataStorageInstance.getGlobalNotifBackgroundText() : dataStorageInstance.getGlobalNotifForegroundText()) &&
                        (status.isOnline()
                                ? notificationDb.getIsOnline()
                                : notificationDb.getIsOffline()
                        );
        }
    }

    public enum Status {
        ONLINE,
        OFFLINE,
        STARTED_GAME,
        LEFT_GAME;

        public boolean isOnline(){
            return this.equals(Status.ONLINE);
        }
        public boolean isOffline(){
            return this.equals(Status.OFFLINE);
        }

        public boolean isStartedGame(){
            return this.equals(Status.STARTED_GAME);
        }

        public boolean isLeftGame(){
            return this.equals(Status.LEFT_GAME);
        }
    }
}
