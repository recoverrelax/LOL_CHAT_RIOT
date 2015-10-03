package com.recoverrelax.pt.riotxmppchat.NotificationCenter;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.NewMessageReceivedPublish;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.InAppLogIds;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;

import org.jivesoftware.smack.packet.Message;

import javax.inject.Inject;

import LolChatRiotDb.NotificationDb;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static junit.framework.Assert.assertTrue;

public class MessageNotification extends NotificationHelper{


    private String userXmppAddress;
    private String username;
    private Message messageContent;
    private NotificationDb notificationDb;

    private static final int MESSAGE_NOTIFICATION_ID = 1111111;
    private static final int MESSAGE_NOTIFICATION_DRAWABLE = R.drawable.ic_action_question_answer_green;

    @Inject DataStorage dataStorageInstance;
    @Inject RiotRosterManager riotRosterManager;

    @Inject
    public MessageNotification(){
//        this.riotXmppDBRepository = xmppDBRepository;
//        this.messageSpeechNotification = speechNotification;
//        this.bus = bus;
//        this.dataStorageInstance = dataStorageInstance;
//        this.riotRosterManager = riotRosterManager;
    }

    public void init(String userXmppAddress, Message messageContent){
        this.userXmppAddress = userXmppAddress;
        this.messageContent = messageContent;
    }

    public void checkInit(){
        assertTrue("first call init", userXmppAddress != null && messageContent != null);
    }

    public void sendMessageNotification(){
        checkInit();
        String message = messageContent.getBody();

        riotXmppDBRepository.getNotificationByUser(userXmppAddress)
                .doOnNext(notification ->
                                this.notificationDb = notification
                )
                .flatMap(notification -> riotRosterManager.getFriendNameFromXmppAddress(userXmppAddress))
                .doOnNext(targetUserName -> {
                    saveToLog(InAppLogIds.FRIEND_PM.getOperationId(), targetUserName + " says: " + message, userXmppAddress);
                    hSendMessageSpeechNotification(targetUserName, message, getMessageSpeechPermission());
                })
                .flatMap(targetUserName -> {
                    this.username = targetUserName;
                    if (isPausedOrClosed()) {
                        return sendSystemNotification(targetUserName + " says:", message, MESSAGE_NOTIFICATION_DRAWABLE, MESSAGE_NOTIFICATION_ID,
                                getMessageBackgroundPermission());
                    } else {
                        checkInit();
                        boolean permission = getMessageForegroundPermission();
                        String buttonLabel = "CHAT";
                        String messageFinal = username + " said: " + messageContent.getBody();

                        if (permission)
                        bus.post(new NewMessageReceivedPublish(userXmppAddress, username, messageFinal, buttonLabel));
                        return Observable.just(true);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                    }
                });
    }

    private boolean getMessageSpeechPermission() {
        return notificationDb != null &&
                (isPausedOrClosed()
                        ? dataStorageInstance.getGlobalNotifBackgroundSpeech()
                        : dataStorageInstance.getGlobalNotifForegroundSpeech()
                )
                && notificationDb.getHasSentMePm() && !AppMiscUtils.isPhoneSilenced();
    }

    private boolean getMessageForegroundPermission() {
        return  notificationDb != null &&
                !isPausedOrClosed() && dataStorageInstance.getGlobalNotifForegroundText() && notificationDb.getHasSentMePm();
    }
    private boolean getMessageBackgroundPermission() {
        return  notificationDb != null &&
                isPausedOrClosed() &&
                dataStorageInstance.getGlobalNotifBackgroundText() &&
                notificationDb.getHasSentMePm();
    }
}
