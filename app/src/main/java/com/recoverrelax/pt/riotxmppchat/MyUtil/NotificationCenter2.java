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

import org.jivesoftware.smack.packet.Message;

import LolChatRiotDb.NotificationDb;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public class NotificationCenter2 extends NotificationCenterHelper2{

    private static final int MESSAGE_NOTIFICATION_ID = 1111111;
    private static final int STATUS_NOTIFICATION_ID  = 2222222;


    private static final int MESSAGE_NOTIFICATION_DRAWABLE = R.drawable.ic_action_question_answer_green;
    private static final int ONLINE_NOTIFICATION_DRAWABLE = R.drawable.ic_online;
    private static final int OFFLINE_NOTIFICATION_DRAWABLE = R.drawable.ic_offline;
    private static final int START_GAME_NOTIFICATION_DRAWABLE = R.drawable.ic_online;
    private static final int LEFT_GAME_NOTIFICATION_DRAWABLE = R.drawable.ic_offline;

    private DataStorage dataStorageInstance;
//
//    private String targetXmppUser;
//    private String targetUserName;
    private RiotXmppDBRepository riotXmppRepository;
//    private NotificationDb notificationDb;

    public NotificationCenter2(){
        dataStorageInstance = DataStorage.getInstance();
        riotXmppRepository = new RiotXmppDBRepository();
    }

    public void sendMessageNotification(String userXmppAddress, Message messageContent){

        String message = messageContent.getBody();
        final boolean[] hasSoundPermission = {true};

            riotXmppRepository.getNotificationByUser(userXmppAddress)
                    .map(notificationDb -> {
                        hasSoundPermission[0] = getMessageSpeechPermission(notificationDb);
                        if(isPausedOrClosed())
                            return getMessageBackgroundPermission(notificationDb);
                        else
                            return getMessageForegroundPermission(notificationDb);
                    })
            .flatMap(hasPermissions -> MainApplication.getInstance().getRiotXmppService().getRiotRosterManager().getFriendNameFromXmppAddress(userXmppAddress)
                            .map(targetUserName -> new Pair<>(hasPermissions, targetUserName))
            )
            .doOnNext(booleanStringPair -> {
                String targetUserName = booleanStringPair.second;
                saveToLog(InAppLogIds.FRIEND_PM.getOperationId(), targetUserName + " says: " + message, userXmppAddress);
                hSendMessageSpeechNotification(targetUserName, message, hasSoundPermission[0]);
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Pair<Boolean, String>>() {
                        @Override public void onCompleted() { }
                        @Override public void onError(Throwable e) { }

                        @Override
                        public void onNext(Pair<Boolean, String> booleanStringPair) {
                            String targetUserName = booleanStringPair.second;
                            boolean hasPermission = booleanStringPair.first;

                            if(isPausedOrClosed()){
                                LOGI("123", "isPausedOrClosed");
                                hSendSystemNotification(targetUserName + " says:", message, MESSAGE_NOTIFICATION_DRAWABLE, MESSAGE_NOTIFICATION_ID,
                                        hasPermission)
                                        .subscribe(new Subscriber<Boolean>() {
                                            @Override public void onCompleted() { } @Override public void onError(Throwable e) { }
                                            @Override
                                            public void onNext(Boolean aBoolean) {}
                                        });
                            }else{
//                                LOGI("123", "isPausedOrClosed2");
//                                MainApplication.getInstance().getBusInstance().post(new OnNewMessageEventEvent(userXmppAddress,
//                                        targetUserName, targetUserName + "says: \n" + message, "CHAT", hasPermission));
                            }
                        }
                    });

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



    public boolean isPausedOrClosed(){
        return MainApplication.getInstance().isApplicationPaused()
                || MainApplication.getInstance().isApplicationClosed();
    }



    private void saveToLog(Integer logId, String logMessage, String targetXmppUser) {
         new RiotXmppDBRepository().insertOrReplaceInappLog(logId, logMessage, targetXmppUser)
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribeOn(Schedulers.io())
                 .subscribe(new Subscriber<Long>() {
                     @Override public void onCompleted() { } @Override public void onError(Throwable e) { }

                     @Override
                     public void onNext(Long aLong) {
                         MainApplication.getInstance().getBusInstance().post(new OnNewLogEvent());
                     }
                 });

    }
}
