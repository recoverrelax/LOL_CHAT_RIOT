package com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.activity.PersonalMessageActivity;

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

    public static void startPersonalMessageActivity(Context context, String friendName, String friendXmppAddress){
        Intent intent = new Intent(context, PersonalMessageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(PersonalMessageActivity.INTENT_FRIEND_NAME, friendName);
        intent.putExtra(PersonalMessageActivity.INTENT_FRIEND_XMPPNAME, friendXmppAddress);
        context.startActivity(intent);
        AppContextUtils.overridePendingTransitionBackAppDefault((Activity) context);
    }
}
