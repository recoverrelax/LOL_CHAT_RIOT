package com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class AndroidUtils {
    public static void setBlinkAnimation(final View view, boolean state) {
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
}
