package com.recoverrelax.pt.riotxmppchat.MyUtil;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

public class AppMiscUtils {

    public static boolean isPhoneSilenced(){
        AudioManager audioManager = (AudioManager) MainApplication.getInstance().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        return !(audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0 && audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL);
    }


    public @ColorRes static int getRandomMaterialColor(Random randomFunction){
        List materialColorList = getMaterialColorList();
        return (Integer)materialColorList.get(randomFunction.nextInt(materialColorList.size()));
    }

    public static List<Integer> getMaterialColorList(){
        List<Integer> colors = new ArrayList<>();

        colors.add(R.color.red_300);
        colors.add(R.color.red_400);

        colors.add(R.color.purple_500);
        colors.add(R.color.purple_600);
        colors.add(R.color.purple_700);
        colors.add(R.color.purple_800);
        colors.add(R.color.purple_A700);


        colors.add(R.color.deep_purple_300);
        colors.add(R.color.deep_purple_400);
        colors.add(R.color.deep_purple_500);
        colors.add(R.color.deep_purple_600);

        colors.add(R.color.deep_purple_A100);
        colors.add(R.color.deep_purple_A200);
        colors.add(R.color.deep_purple_A400);
        colors.add(R.color.deep_purple_A700);


        colors.add(R.color.indigo_300);
        colors.add(R.color.indigo_400);
        colors.add(R.color.indigo_500);
        colors.add(R.color.indigo_600);
        colors.add(R.color.indigo_A100);
        colors.add(R.color.indigo_A200);
        colors.add(R.color.indigo_A400);
        colors.add(R.color.indigo_A700);


        colors.add(R.color.blue_500);
        colors.add(R.color.blue_600);
        colors.add(R.color.blue_700);
        colors.add(R.color.blue_800);
        colors.add(R.color.blue_900);

        colors.add(R.color.blue_A200);
        colors.add(R.color.blue_A400);
        colors.add(R.color.blue_A700);

        colors.add(R.color.light_blue_300);
        colors.add(R.color.light_blue_400);
        colors.add(R.color.light_blue_500);
        colors.add(R.color.light_blue_600);
        colors.add(R.color.light_blue_700);
        colors.add(R.color.light_blue_800);
        colors.add(R.color.light_blue_900);

        colors.add(R.color.cyan_400);
        colors.add(R.color.cyan_500);
        colors.add(R.color.cyan_600);
        colors.add(R.color.cyan_700);
        colors.add(R.color.cyan_800);



        colors.add(R.color.teal_400);
        colors.add(R.color.teal_500);
        colors.add(R.color.teal_600);
        colors.add(R.color.teal_700);



        colors.add(R.color.light_green_500);
        colors.add(R.color.light_green_600);
        colors.add(R.color.light_green_700);
        colors.add(R.color.light_green_800);
        colors.add(R.color.light_green_900);
        colors.add(R.color.light_green_A200);
        colors.add(R.color.light_green_A400);
        colors.add(R.color.light_green_A700);


        colors.add(R.color.lime_500);
        colors.add(R.color.lime_600);
        colors.add(R.color.lime_700);
        colors.add(R.color.lime_800);
        colors.add(R.color.lime_900);
        colors.add(R.color.lime_A200);
        colors.add(R.color.lime_A400);
        colors.add(R.color.lime_A700);


        colors.add(R.color.grey_500);
        colors.add(R.color.grey_600);
        colors.add(R.color.grey_700);
        colors.add(R.color.grey_800);

        colors.add(R.color.blue_grey_300);
        colors.add(R.color.blue_grey_400);
        colors.add(R.color.blue_grey_500);
        colors.add(R.color.blue_grey_600);
        colors.add(R.color.blue_grey_700);
        colors.add(R.color.blue_grey_800);
        colors.add(R.color.blue_grey_900);
        
        return colors;
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