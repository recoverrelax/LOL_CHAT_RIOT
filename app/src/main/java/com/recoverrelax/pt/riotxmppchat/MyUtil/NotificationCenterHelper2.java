package com.recoverrelax.pt.riotxmppchat.MyUtil;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewMessageEventEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public abstract class NotificationCenterHelper2 {

    private final Context applicationContext = MainApplication.getInstance().getApplicationContext();

    protected Observable<Boolean> hSendSystemNotification(@NonNull String title, @NonNull String message, @DrawableRes int systemNotifId, int notificationId,
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

//    public Observable<Snackbar> hSendSnackbarNotification(@Nullable Activity activity, OnNewMessageEventEvent event) {
//        boolean combinedPermission = event.isHasPermissions();
//        String userXmppName = event.getUserXmppAddress();
//        String username = event.getUsername();
//        String buttonLabel = event.getButtonLabel();
//        String message = event.getMessage();
//
//        return Observable.create(new Observable.OnSubscribe<Snackbar>() {
//            @Override
//            public void call(Subscriber<? super Snackbar> subscriber) {
//                if(!combinedPermission || activity == null){
//                    subscriber.onNext(null);
//                    subscriber.onCompleted();
//                }else {
//
//                    Snackbar snackbar = Snackbar
//                            .make(activity.getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_LONG);
//
//                    if (userXmppName != null && buttonLabel != null) {
//                        snackbar.setAction(buttonLabel, view -> {
//                            if (activity instanceof BaseActivity)
//                                ((BaseActivity) activity).goToMessageActivity(username, userXmppName);
//                        });
//                    }
//                    snackbar.show();
//
//                    subscriber.onNext(snackbar);
//                    subscriber.onCompleted();
//                }
//            }
//        });
//    }

    protected void hSendMessageSpeechNotification(String user, String message, boolean combinedPermission){

        if(!combinedPermission)
            return;
        MessageSpeechNotification.getInstance().sendMessageSpeechNotification(message, user);
    }

    protected void hSendStatusSpeechNotification(String message, boolean combinedPermission){

        if(!combinedPermission)
            return;
        MessageSpeechNotification.getInstance().sendStatusSpeechNotification(message);
    }
}
