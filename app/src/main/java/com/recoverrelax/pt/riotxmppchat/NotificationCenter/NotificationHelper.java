package com.recoverrelax.pt.riotxmppchat.NotificationCenter;

import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils.LOGI;

public class NotificationHelper {

    private final Context applicationContext = MainApplication.getInstance().getApplicationContext();

    @Inject RiotXmppDBRepository riotXmppDBRepository;
    @Inject MessageSpeechNotification messageSpeechNotification;
    @Inject Bus bus;

    protected Observable<Boolean> sendSystemNotification(@NonNull String title, @NonNull String message, @DrawableRes int systemNotifId, int notificationId,
                                                         boolean hasPermissions){
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                if (!hasPermissions) {
                    subscriber.onNext(false);
                    subscriber.onCompleted();
                    LOGI("123", "!hasPermissions");
                }
                LOGI("123", "hasPermissions");
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(applicationContext)
                        .setSmallIcon(systemNotifId)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                NotificationManager mNotificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(notificationId, mBuilder.build());

                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected boolean isPausedOrClosed(){
        return MainApplication.getInstance().isApplicationPausedOrClosed();
    }

    protected void saveToLog(Integer logId, String logMessage, String targetXmppUser) {
        riotXmppDBRepository.insertOrReplaceInappLog(logId, logMessage, targetXmppUser)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    protected void hSendMessageSpeechNotification(String user, String message, boolean combinedPermission){

        if(!combinedPermission)
            return;
        messageSpeechNotification.sendMessageSpeechNotification(message, user);
    }

    protected void hSendStatusSpeechNotification(String message, boolean combinedPermission){

        if(!combinedPermission)
            return;
        messageSpeechNotification.sendStatusSpeechNotification(message);
    }

}
