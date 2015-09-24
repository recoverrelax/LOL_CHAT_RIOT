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
}
