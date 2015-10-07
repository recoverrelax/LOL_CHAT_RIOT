package com.recoverrelax.pt.riotxmppchat.MyUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.activity.ChatActivity;

public class AppContextUtils {
    /**
     *
     *
     * @param view
     * @param state
     */
    public static void setBlinkAnimation(final View view, boolean state) {
        if(view == null)
            return;

        if (!state)
            view.clearAnimation();
        else {
            final AlphaAnimation fadeIn = new AlphaAnimation(1.0f, 0.0f);
            fadeIn.setDuration(700);

            fadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.startAnimation(fadeIn);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            view.startAnimation(fadeIn);
        }
    }

    public static void overridePendingTransitionAppDefault(Activity activity){
        activity.overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    public static void overridePendingTransitionBackAppDefault(Activity activity){
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void startPersonalMessageActivity(Context context, String friendName, String friendXmppAddress){
        Intent intent = new Intent(context, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(ChatActivity.INTENT_FRIEND_NAME, friendName);
        intent.putExtra(ChatActivity.INTENT_FRIEND_XMPPNAME, friendXmppAddress);
        context.startActivity(intent);
        AppContextUtils.overridePendingTransitionBackAppDefault((Activity) context);
    }

    public static void startPersonalMessageActivityBgColor(Context context, String friendName, String friendXmppAddress, int bgColor, ReturnCallback cb){
        Intent intent = new Intent(context, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(ChatActivity.INTENT_FRIEND_NAME, friendName);
        intent.putExtra(ChatActivity.INTENT_FRIEND_XMPPNAME, friendXmppAddress);
        intent.putExtra(ChatActivity.INTENT_BGCOLOR, bgColor);
        context.startActivity(intent);
        AppContextUtils.overridePendingTransitionBackAppDefault((Activity) context);

        if(cb!=null)
            cb.onReturnCallback();
    }

    public interface ReturnCallback{
        void onReturnCallback();
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
