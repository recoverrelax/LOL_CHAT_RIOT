package com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.annotation.RawRes;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MainApplication;

import LolChatRiotDb.NotificationDb;

public class SoundNotification {

    private Context context;

    private @RawRes int soundID;
    private NotificationType notificationType;
    private NotificationDb notificationDb;

    public SoundNotification(Context context, @RawRes int soundId, NotificationType notificationType) {
        this.context = context;
        this.soundID = soundId;
        this.notificationType = notificationType;
        this.notificationDb = RiotXmppDBRepository.getNotification(MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser());

        switch (this.notificationType) {
            case ONLINE:
                if (this.notificationDb.getSoundNotificationOnline())
                    sendNotification();
                break;
            case OFFLINE:
                if (this.notificationDb.getSoundNotificationOffline())
                    sendNotification();
                break;
            default:
                break;
        }

    }

    public void sendNotification() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, 0);

        if(audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {

            final MediaPlayer mp = MediaPlayer.create(context, soundID);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mp.reset();
                    mp.release();
                }
            });
            mp.start();
        }else{
            Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(1000);
        }
    }

    public enum NotificationType {
        ONLINE,
        OFFLINE
    }
}
