package com.recoverrelax.pt.riotxmppchat.MyUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.ChatActivity;

public class AppContextUtils {

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

    public static void startChatActivity(Context context, String friendName, String friendXmppAddress){
        Intent intent = new Intent(context, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(ChatActivity.INTENT_FRIEND_NAME, friendName);
        intent.putExtra(ChatActivity.INTENT_FRIEND_XMPPNAME, friendXmppAddress);
        context.startActivity(intent);
        AppContextUtils.overridePendingTransitionBackAppDefault((Activity) context);
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

    public static void showSnackbar(Activity activity, @StringRes int messageString, int duration, View.OnClickListener listener){
        showSnackbar(activity, activity.getResources().getString(messageString), duration, listener);
    }

    public static void showSnackBarErrorFailedService(Activity activity, View.OnClickListener listener, int duration){
        AppContextUtils.showSnackbar(activity, R.string.service_currently_unavailable, duration, listener);
        new Handler().postDelayed(activity::finish, 4000);
    }

    public static void showSnackbar(Activity activity, String messageString, int duration, View.OnClickListener listener){
        Snackbar snackbar = Snackbar.make(activity.getWindow().getDecorView().getRootView(),
                messageString,
                duration);

        ViewGroup group = (ViewGroup) snackbar.getView();
        TextView snackbarTextView = (TextView) group.findViewById(android.support.design.R.id.snackbar_text);

        if(activity instanceof BaseActivity){
            BaseActivity baseActivity = (BaseActivity) activity;
            group.setBackgroundColor(baseActivity.getToobarColor());

            if(baseActivity.getToolbarColor() == baseActivity.getResources().getColor(R.color.white))
                snackbarTextView.setTextColor(Color.BLACK);
            else
                snackbarTextView.setTextColor(Color.WHITE);
        }else {
            group.setBackgroundColor(activity.getResources().getColor(R.color.primaryColor));
            snackbarTextView.setTextColor(Color.WHITE);
        }
        if(listener != null)
            snackbar.setAction("RETRY", listener);

        snackbar.show();
}


    public static void printStackTrace(Throwable e) {
        LogUtils.LOGE("TRW", e.getMessage(), e);
    }
}
