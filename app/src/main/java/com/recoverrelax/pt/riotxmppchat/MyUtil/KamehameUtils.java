package com.recoverrelax.pt.riotxmppchat.MyUtil;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

public class KamehameUtils {

    public static Snackbar showSnackbar(Activity activity, @StringRes int stringRes, boolean showAction, int length, String actionString, View.OnClickListener listener){
        Snackbar snackbar = Snackbar.make(activity.getWindow().getDecorView().getRootView(),
                stringRes,
                length);
        if(showAction && listener != null)
            snackbar.setAction(actionString, listener);

        snackbar.show();
        return snackbar;
    }

    public static String transformGoldIntoKMode(int goldEarned){
        if(goldEarned > 999){
            return String.valueOf((goldEarned/1000)) + "K";
        }else{
            return String.valueOf(goldEarned);
        }
    }

    public static String transformKillDeathAssistIntoKda(int kill, int death, int assists) {
        return String.valueOf(kill) + "/" + String.valueOf(death) + "/" + String.valueOf(assists);
    }
}
