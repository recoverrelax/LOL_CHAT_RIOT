package com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils;

import android.content.Context;
import android.media.AudioManager;

public class AppMiscUtils {

    public static boolean isAppSilenced(Context context){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return !(audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0 && audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL);
    }
}
