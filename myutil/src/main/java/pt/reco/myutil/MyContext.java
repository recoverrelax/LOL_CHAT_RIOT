package pt.reco.myutil;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

public class MyContext {

    @ColorInt
    public static int getColor(Context context, int color){
       return ContextCompat.getColor(context, color);
    }

    @ColorInt
    public static int getColor(Fragment frag, int color){
        return ContextCompat.getColor(frag.getActivity(), color);
    }

    public static Drawable getDrawable(Context context, int drawableId){
        return ContextCompat.getDrawable(context, drawableId);
    }

    public static Drawable getDrawable(Fragment frag, int drawableId){
        return ContextCompat.getDrawable(frag.getActivity(), drawableId);
    }

    public static void showSnackbar(Activity activity, @StringRes int messageString, int duration){
        Snackbar.make(activity.getWindow().getDecorView().getRootView(),
                messageString,
                duration).show();
    }

    public static void showSnackbar(Activity activity, String messageString, int duration){
        Snackbar.make(activity.getWindow().getDecorView().getRootView(),
                messageString,
                duration).show();
    }

    public static void showSnackbar(Fragment frag, @StringRes int messageString, int duration){
        showSnackbar(frag.getActivity(), messageString, duration);
    }



    public static void showSnackbar(Fragment frag, String messageString, int duration){
        showSnackbar(frag.getActivity(), messageString, duration);
    }
}
