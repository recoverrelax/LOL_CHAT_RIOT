package pt.reco.myutil;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
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
}
