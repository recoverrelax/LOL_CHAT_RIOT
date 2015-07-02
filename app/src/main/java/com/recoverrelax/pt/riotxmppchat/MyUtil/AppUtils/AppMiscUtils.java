package com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Environment;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AppMiscUtils {

    public static boolean isPhoneSilenced(){
        AudioManager audioManager = (AudioManager) MainApplication.getInstance().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        return !(audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0 && audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL);
    }


    public @ColorRes static int getRamdomMaterialColor(Random randomFunction){
        List materialColorList = getMaterialColorList();
        return (Integer)materialColorList.get(randomFunction.nextInt(materialColorList.size()));
    }

    public static List<Integer> getXRamdomMaterialColorT(Random randomFunction, int nrColors, Context context, int alpha){
        List materialColorList = getMaterialColorList();
        List<Integer> colorList = new ArrayList<>();

        for(int i = 0; i < nrColors; i++){
            Integer colorId = (Integer) materialColorList.get(randomFunction.nextInt(materialColorList.size()));
            int color = context.getResources().getColor(colorId);
            int colorT = changeColorAlpha(color, alpha);
            colorList.add(colorT);
        }
        return colorList;
    }

    public static List<Integer> getXRamdomMaterialColor(Random randomFunction, int nrColors, Context context){
        List materialColorList = getMaterialColorList();
        List<Integer> colorList = new ArrayList<>();

        for(int i = 0; i < nrColors; i++){
            Integer colorId = (Integer) materialColorList.get(randomFunction.nextInt(materialColorList.size()));
            int color = context.getResources().getColor(colorId);

            colorList.add(color);
        }
        return colorList;
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

    public static List<Integer> getMaterialColorList2(){
        List<Integer> colors = new ArrayList<>();

        colors.add(R.color.red_50);
        colors.add(R.color.red_100);
        colors.add(R.color.red_200);
        colors.add(R.color.red_300);
        colors.add(R.color.red_400);
        colors.add(R.color.red_500);
        colors.add(R.color.red_600);
        colors.add(R.color.red_700);
        colors.add(R.color.red_800);
        colors.add(R.color.red_900);
        colors.add(R.color.red_A100);
        colors.add(R.color.red_A200);
        colors.add(R.color.red_A400);
        colors.add(R.color.red_A700);

        colors.add(R.color.pink_50);
        colors.add(R.color.pink_100);
        colors.add(R.color.pink_200);
        colors.add(R.color.pink_300);
        colors.add(R.color.pink_400);
        colors.add(R.color.pink_500);
        colors.add(R.color.pink_600);
        colors.add(R.color.pink_700);
        colors.add(R.color.pink_800);
        colors.add(R.color.pink_900);
        colors.add(R.color.pink_A100);
        colors.add(R.color.pink_A200);
        colors.add(R.color.pink_A400);
        colors.add(R.color.pink_A700);

        colors.add(R.color.purple_50);
        colors.add(R.color.purple_100);
        colors.add(R.color.purple_200);
        colors.add(R.color.purple_300);
        colors.add(R.color.purple_400);
        colors.add(R.color.purple_500);
        colors.add(R.color.purple_600);
        colors.add(R.color.purple_700);
        colors.add(R.color.purple_800);
        colors.add(R.color.purple_900);
        colors.add(R.color.purple_A100);
        colors.add(R.color.purple_A200);
        colors.add(R.color.purple_A400);
        colors.add(R.color.purple_A700);

        colors.add(R.color.deep_purple_50);
        colors.add(R.color.deep_purple_100);
        colors.add(R.color.deep_purple_200);
        colors.add(R.color.deep_purple_300);
        colors.add(R.color.deep_purple_400);
        colors.add(R.color.deep_purple_500);
        colors.add(R.color.deep_purple_600);
        colors.add(R.color.deep_purple_700);
        colors.add(R.color.deep_purple_800);
        colors.add(R.color.deep_purple_900);
        colors.add(R.color.deep_purple_A100);
        colors.add(R.color.deep_purple_A200);
        colors.add(R.color.deep_purple_A400);
        colors.add(R.color.deep_purple_A700);

        colors.add(R.color.indigo_50);
        colors.add(R.color.indigo_100);
        colors.add(R.color.indigo_200);
        colors.add(R.color.indigo_300);
        colors.add(R.color.indigo_400);
        colors.add(R.color.indigo_500);
        colors.add(R.color.indigo_600);
        colors.add(R.color.indigo_700);
        colors.add(R.color.indigo_800);
        colors.add(R.color.indigo_900);
        colors.add(R.color.indigo_A100);
        colors.add(R.color.indigo_A200);
        colors.add(R.color.indigo_A400);
        colors.add(R.color.indigo_A700);

        colors.add(R.color.blue_50);
        colors.add(R.color.blue_100);
        colors.add(R.color.blue_200);
        colors.add(R.color.blue_300);
        colors.add(R.color.blue_400);
        colors.add(R.color.blue_500);
        colors.add(R.color.blue_600);
        colors.add(R.color.blue_700);
        colors.add(R.color.blue_800);
        colors.add(R.color.blue_900);
        colors.add(R.color.blue_A100);
        colors.add(R.color.blue_A200);
        colors.add(R.color.blue_A400);
        colors.add(R.color.blue_A700);

        colors.add(R.color.light_blue_50);
        colors.add(R.color.light_blue_100);
        colors.add(R.color.light_blue_200);
        colors.add(R.color.light_blue_300);
        colors.add(R.color.light_blue_400);
        colors.add(R.color.light_blue_500);
        colors.add(R.color.light_blue_600);
        colors.add(R.color.light_blue_700);
        colors.add(R.color.light_blue_800);
        colors.add(R.color.light_blue_900);
        colors.add(R.color.light_blue_A100);
        colors.add(R.color.light_blue_A200);
        colors.add(R.color.light_blue_A400);
        colors.add(R.color.light_blue_A700);

        colors.add(R.color.cyan_50);
        colors.add(R.color.cyan_100);
        colors.add(R.color.cyan_200);
        colors.add(R.color.cyan_300);
        colors.add(R.color.cyan_400);
        colors.add(R.color.cyan_500);
        colors.add(R.color.cyan_600);
        colors.add(R.color.cyan_700);
        colors.add(R.color.cyan_800);
        colors.add(R.color.cyan_900);
        colors.add(R.color.cyan_A100);
        colors.add(R.color.cyan_A200);
        colors.add(R.color.cyan_A400);
        colors.add(R.color.cyan_A700);

        colors.add(R.color.teal_50);
        colors.add(R.color.teal_100);
        colors.add(R.color.teal_200);
        colors.add(R.color.teal_300);
        colors.add(R.color.teal_400);
        colors.add(R.color.teal_500);
        colors.add(R.color.teal_600);
        colors.add(R.color.teal_700);
        colors.add(R.color.teal_800);
        colors.add(R.color.teal_900);
        colors.add(R.color.teal_A100);
        colors.add(R.color.teal_A200);
        colors.add(R.color.teal_A400);
        colors.add(R.color.teal_A700);

        colors.add(R.color.green_50);
        colors.add(R.color.green_100);
        colors.add(R.color.green_200);
        colors.add(R.color.green_300);
        colors.add(R.color.green_400);
        colors.add(R.color.green_500);
        colors.add(R.color.green_600);
        colors.add(R.color.green_700);
        colors.add(R.color.green_800);
        colors.add(R.color.green_900);
        colors.add(R.color.green_A100);
        colors.add(R.color.green_A200);
        colors.add(R.color.green_A400);
        colors.add(R.color.green_A700);

        colors.add(R.color.light_green_50);
        colors.add(R.color.light_green_100);
        colors.add(R.color.light_green_200);
        colors.add(R.color.light_green_300);
        colors.add(R.color.light_green_400);
        colors.add(R.color.light_green_500);
        colors.add(R.color.light_green_600);
        colors.add(R.color.light_green_700);
        colors.add(R.color.light_green_800);
        colors.add(R.color.light_green_900);
        colors.add(R.color.light_green_A100);
        colors.add(R.color.light_green_A200);
        colors.add(R.color.light_green_A400);
        colors.add(R.color.light_green_A700);

        colors.add(R.color.lime_50);
        colors.add(R.color.lime_100);
        colors.add(R.color.lime_200);
        colors.add(R.color.lime_300);
        colors.add(R.color.lime_400);
        colors.add(R.color.lime_500);
        colors.add(R.color.lime_600);
        colors.add(R.color.lime_700);
        colors.add(R.color.lime_800);
        colors.add(R.color.lime_900);
        colors.add(R.color.lime_A100);
        colors.add(R.color.lime_A200);
        colors.add(R.color.lime_A400);
        colors.add(R.color.lime_A700);

        colors.add(R.color.yellow_50);
        colors.add(R.color.yellow_100);
        colors.add(R.color.yellow_200);
        colors.add(R.color.yellow_300);
        colors.add(R.color.yellow_400);
        colors.add(R.color.yellow_500);
        colors.add(R.color.yellow_600);
        colors.add(R.color.yellow_700);
        colors.add(R.color.yellow_800);
        colors.add(R.color.yellow_900);
        colors.add(R.color.yellow_A100);
        colors.add(R.color.yellow_A200);
        colors.add(R.color.yellow_A400);
        colors.add(R.color.yellow_A700);

        colors.add(R.color.amber_50);
        colors.add(R.color.amber_100);
        colors.add(R.color.amber_200);
        colors.add(R.color.amber_300);
        colors.add(R.color.amber_400);
        colors.add(R.color.amber_500);
        colors.add(R.color.amber_600);
        colors.add(R.color.amber_700);
        colors.add(R.color.amber_800);
        colors.add(R.color.amber_900);
        colors.add(R.color.amber_A100);
        colors.add(R.color.amber_A200);
        colors.add(R.color.amber_A400);
        colors.add(R.color.amber_A700);

        colors.add(R.color.orange_50);
        colors.add(R.color.orange_100);
        colors.add(R.color.orange_200);
        colors.add(R.color.orange_300);
        colors.add(R.color.orange_400);
        colors.add(R.color.orange_500);
        colors.add(R.color.orange_600);
        colors.add(R.color.orange_700);
        colors.add(R.color.orange_800);
        colors.add(R.color.orange_900);
        colors.add(R.color.orange_A100);
        colors.add(R.color.orange_A200);
        colors.add(R.color.orange_A400);
        colors.add(R.color.orange_A700);

        colors.add(R.color.deep_orange_50);
        colors.add(R.color.deep_orange_100);
        colors.add(R.color.deep_orange_200);
        colors.add(R.color.deep_orange_300);
        colors.add(R.color.deep_orange_400);
        colors.add(R.color.deep_orange_500);
        colors.add(R.color.deep_orange_600);
        colors.add(R.color.deep_orange_700);
        colors.add(R.color.deep_orange_800);
        colors.add(R.color.deep_orange_900);
        colors.add(R.color.deep_orange_A100);
        colors.add(R.color.deep_orange_A200);
        colors.add(R.color.deep_orange_A400);
        colors.add(R.color.deep_orange_A700);

        colors.add(R.color.brown_50);
        colors.add(R.color.brown_100);
        colors.add(R.color.brown_200);
        colors.add(R.color.brown_300);
        colors.add(R.color.brown_400);
        colors.add(R.color.brown_500);
        colors.add(R.color.brown_600);
        colors.add(R.color.brown_700);
        colors.add(R.color.brown_800);
        colors.add(R.color.brown_900);

        colors.add(R.color.grey_50);
        colors.add(R.color.grey_100);
        colors.add(R.color.grey_200);
        colors.add(R.color.grey_300);
        colors.add(R.color.grey_400);
        colors.add(R.color.grey_500);
        colors.add(R.color.grey_600);
        colors.add(R.color.grey_700);
        colors.add(R.color.grey_800);
        colors.add(R.color.grey_900);
        colors.add(R.color.grey_1000b);
        colors.add(R.color.grey_1000w);

        colors.add(R.color.blue_grey_50);
        colors.add(R.color.blue_grey_100);
        colors.add(R.color.blue_grey_200);
        colors.add(R.color.blue_grey_300);
        colors.add(R.color.blue_grey_400);
        colors.add(R.color.blue_grey_500);
        colors.add(R.color.blue_grey_600);
        colors.add(R.color.blue_grey_700);
        colors.add(R.color.blue_grey_800);
        colors.add(R.color.blue_grey_900);

        return colors;
    }

    public static int changeColorAlpha(int colorRes, int alpha){
        return Color.argb(alpha, Color.red(colorRes), Color.green(colorRes), Color.blue(colorRes));
    }

    /**
     * Get app specific folder's path
     * @return
     */
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
}