package com.recoverrelax.pt.riotxmppchat.NotificationCenter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;

import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.EventHandling.OnNewMessageEventEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.InAppLogIds;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.RiotXmppNewMessageActivity;
import com.squareup.otto.Bus;

import org.jivesoftware.smack.packet.Message;

import javax.inject.Inject;

import LolChatRiotDb.NotificationDb;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils.LOGI;
import static junit.framework.Assert.assertTrue;

public class MessageNotification extends NotificationHelper{


    private final DataStorage dataStorageInstance;
    private final RiotRosterManager riotRosterManager;

    private String userXmppAddress;
    private String username;
    private Message messageContent;
    private NotificationDb notificationDb;

    private static final int MESSAGE_NOTIFICATION_ID = 1111111;
    private static final int MESSAGE_NOTIFICATION_DRAWABLE = R.drawable.ic_action_question_answer_green;

    @Inject
    public MessageNotification(Bus bus, DataStorage dataStorageInstance, RiotRosterManager riotRosterManager, RiotXmppDBRepository xmppDBRepository, MessageSpeechNotification speechNotification){
        this.riotXmppDBRepository = xmppDBRepository;
        this.messageSpeechNotification = speechNotification;
        this.bus = bus;
        this.dataStorageInstance = dataStorageInstance;
        this.riotRosterManager = riotRosterManager;
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
                        bus.post(new OnNewMessageEventEvent(userXmppAddress));

                        /**
                         * {@link RiotXmppNewMessageActivity#OnMessageSnackBarReady(MessageNotification)}
                         */
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
        checkInit();
        boolean permission = getMessageForegroundPermission();
        String buttonLabel = "CHAT";
        String message = username + " said: " + messageContent.getBody();

                if(!permission || activity == null)
                    return;

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
