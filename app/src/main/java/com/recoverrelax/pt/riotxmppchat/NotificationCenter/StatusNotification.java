package com.recoverrelax.pt.riotxmppchat.NotificationCenter;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.NewMessageReceivedPublish;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.OnNewFriendPlayingPublish;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.InAppLogIds;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;

import javax.inject.Inject;

import LolChatRiotDb.NotificationDb;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils.LOGI;
import static junit.framework.Assert.assertTrue;

public class StatusNotification extends NotificationHelper {

    private String userXmppAddress;
    private String username;
    private Status status;
    private NotificationDb notificationDb;

    private static final int STATUS_NOTIFICATION_ID = 2222222;

    private static final int ONLINE_NOTIFICATION_DRAWABLE = R.drawable.ic_online;
    private static final int OFFLINE_NOTIFICATION_DRAWABLE = R.drawable.ic_offline;
    private static final int START_GAME_NOTIFICATION_DRAWABLE = R.drawable.ic_online;
    private static final int LEFT_GAME_NOTIFICATION_DRAWABLE = R.drawable.ic_offline;

    @Inject
    DataStorage dataStorageInstance;

    @Inject
    public StatusNotification() {

    }

    public void init(String xmppAddress, String friendName, Status statusNotificationStatus) {
        this.userXmppAddress = xmppAddress;
        this.status = statusNotificationStatus;
        this.username = friendName;
    }

    private void checkInit() {
        assertTrue("must first call init", userXmppAddress != null && username != null && status != null);
    }

    public void sendStatusNotification() {
        LOGI("1212", "enter sendStatusNotification");
        checkInit();

        riotXmppDBRepository.getNotificationByUser(userXmppAddress)
                .doOnNext(notification -> {
                    this.notificationDb = notification;
                    LOGI("1212", "enter doOnNext");
                    if (status.isStartedGame() || status.isLeftGame())
                        bus.post(new OnNewFriendPlayingPublish());

                    int logId = getLogIdFromStatus(status);
                    String logMessage = getLogMessageFromStatus(status, username);
                    boolean speechPermission = getSpeechPermissionFromStatus(status);

                    saveToLog(logId, logMessage, userXmppAddress);
                    LOGI("1212", "hSendStatusSpeechNotification");
                    hSendStatusSpeechNotification(logMessage, speechPermission);

                })
                .flatMap(notification -> {
                    LOGI("1212", "enter flatMap");
                    if (isPausedOrClosed()) {
                        String systemNotificationMessage = getMessageFromStatus(status);
                        int notificationDrawable = getNotificationDrawableFromStatus(status);
                        boolean statusPermission = getStatusPermissionFromStatus(status);

                        return sendSystemNotification(username, systemNotificationMessage, notificationDrawable, STATUS_NOTIFICATION_ID,
                                statusPermission);
                    } else {

                        checkInit();
                        boolean permission = getStatusPermissionFromStatus(status);

                        if (permission) {
                            String buttonLabel = "CHAT";
                            String message = username + " " + getMessageFromStatus(status);
                            bus.post(new NewMessageReceivedPublish(userXmppAddress, username, message, buttonLabel));
                        }
                        return Observable.just(true);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }


    private int getLogIdFromStatus(Status status) {
        switch (status) {
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

    private String getLogMessageFromStatus(Status status, String username) {
        return username + " " + getMessageFromStatus(status);
    }

    private String getMessageFromStatus(Status status) {
        switch (status) {
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

    private boolean getSpeechPermissionFromStatus(Status status) {

        if (notificationDb == null)
            return false;

        switch (status) {
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

    private int getNotificationDrawableFromStatus(Status status) {
        switch (status) {
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

    private boolean getStatusPermissionFromStatus(Status status) {
        switch (status) {
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

        public boolean isOnline() {
            return this.equals(Status.ONLINE);
        }

        public boolean isOffline() {
            return this.equals(Status.OFFLINE);
        }

        public boolean isStartedGame() {
            return this.equals(Status.STARTED_GAME);
        }

        public boolean isLeftGame() {
            return this.equals(Status.LEFT_GAME);
        }
    }
}
