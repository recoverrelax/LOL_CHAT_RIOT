package com.recoverrelax.pt.riotxmppchat.MyUtil;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;

import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;

public abstract class BaseNotification {

    private static final int ICON_ID = R.drawable.ic_teemo;
    private static final int NOTIFICATION_NEW_MESSAGE_ID = 123456789;

    protected void sendSnackBarNotification(String message, int length, String buttonLabel, String username, String userXmppName) {
        Snackbar
                .make(getActivity().getWindow().getDecorView().getRootView(), message, length)
                .setAction(buttonLabel, view -> {
                    if(getActivity() instanceof BaseActivity)
                        ((BaseActivity)getActivity()).goToMessageActivity(username, userXmppName);
                }).show();
//        includeAudioNotification();
    }
    public abstract Activity getActivity();

    protected void sendSystemNotification(String title, String message){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity())
                .setSmallIcon(ICON_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_NEW_MESSAGE_ID, mBuilder.build());
//        includeAudioNotification();
    }

    public void includeAudioNotification() {
        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, 0);

        if(audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {

            final MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.teemo_new_message);
            mp.setOnCompletionListener(mediaPlayer -> {
                mp.reset();
                mp.release();
            });
            mp.start();
        }else{
            Vibrator v = (Vibrator) this.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(1000);
        }
    }
}
