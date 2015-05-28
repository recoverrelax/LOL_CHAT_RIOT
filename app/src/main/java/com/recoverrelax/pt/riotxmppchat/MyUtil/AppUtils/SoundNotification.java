package com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils;

import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.RawRes;
import android.support.v4.app.NotificationCompat;

import com.edgelabs.pt.mybaseapp.R;

public class SoundNotification {

    private Context context;

    private @RawRes int soundID;

    private MediaPlayer mp;

    public SoundNotification(Context context, @RawRes int soundId){
        this.context = context;
        this.soundID = soundId;

        createNewSoundNotification();
    }

    private void createNewSoundNotification() {
        final MediaPlayer mp = MediaPlayer.create(context, soundID);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mp.release();
            }
        });
    }

    public void play(){
        mp.start();
    }
}
