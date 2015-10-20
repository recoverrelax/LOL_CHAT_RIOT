package com.recoverrelax.pt.riotxmppchat.MyUtil;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.R;

import java.io.File;
import java.util.Random;

public class AppMiscUtils {

    public static boolean isPhoneSilenced() {
        AudioManager audioManager = (AudioManager) MainApplication.getInstance().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        return !(audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0 && audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL);
    }


    public @ColorRes static String getRandomMaterialColor(Context c) {
        Random randomFunction = new Random();
        String[] materialColorList = getMaterialColorList(c);
        return materialColorList[randomFunction.nextInt(materialColorList.length)];
    }

    public static String[] getMaterialColorList(Context context) {
        return context.getResources().getStringArray(R.array.materialColorsFiltered);
    }

    public static File getAppSpecificFolder(Context ctx) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // External directory
            File[] files = ContextCompat.getExternalFilesDirs(ctx, null);
            return files[0];
        } else {
            // Internal directory
            return ctx.getFilesDir();
        }
    }

    public static boolean hasMinimumVersion(int version) {
        return Build.VERSION.SDK_INT >= version;
    }
}