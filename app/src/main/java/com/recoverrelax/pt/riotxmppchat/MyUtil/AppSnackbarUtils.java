package com.recoverrelax.pt.riotxmppchat.MyUtil;

import android.app.Activity;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import pt.reco.myutil.MyContext;

public final class AppSnackbarUtils {

    public static final int LENGTH_SHORT = Snackbar.LENGTH_SHORT;
    public static final int LENGTH_INDEFINITE = Snackbar.LENGTH_INDEFINITE;
    public static final int LENGTH_LONG = Snackbar.LENGTH_LONG;
    private static final int DEFAULT_BACKGROUND_COLOR = R.color.colorDark;
    private static final int DEFAULT_TEXT_COLOR = R.color.white;
    private static final int DEFAULT_ACTION_TEXT_COLOR = R.color.accentColor;

    @SnackbarDuration
    public static Snackbar showSnackBar(
            @NonNull Activity activity,
            String messageStringRes,
            @SnackbarDuration int duration,
            Integer backgroundColor,
            Integer textColor,
            String actionText,
            Integer actionTextColor,
            View.OnClickListener listener) {

        //noinspection ConstantConditions
        if (activity == null)
            return null;

        Snackbar snackbar = Snackbar.make(
                activity.getWindow().getDecorView().getRootView(),
                messageStringRes,
                duration
        );

        ViewGroup group = (ViewGroup) snackbar.getView();
        TextView snackbarTextView = (TextView) group.findViewById(android.support.design.R.id.snackbar_text);

        // Setting background color
        group.setBackgroundColor(MyContext.getColor(activity, backgroundColor == null ? DEFAULT_BACKGROUND_COLOR : backgroundColor));

        // setting text color
        snackbarTextView.setTextColor(MyContext.getColor(activity, textColor == null ? DEFAULT_TEXT_COLOR : textColor));

        // set click listener
        if (listener != null && actionText != null) {
            snackbar.setAction(actionText, listener);
            snackbar.setActionTextColor(MyContext.getColor(activity, actionTextColor == null ? DEFAULT_ACTION_TEXT_COLOR : actionTextColor));
        }

        snackbar.show();
        return snackbar;
    }

    @SnackbarDuration
    public static Snackbar showSnackBar(
            @NonNull Activity activity,
            @StringRes int messageStringRes,
            @SnackbarDuration int duration,
            Integer backgroundColor,
            Integer textColor,
            String actionText,
            Integer actionTextColor,
            View.OnClickListener listener) {

        return showSnackBar(activity, activity.getResources().getString(messageStringRes), duration, backgroundColor, textColor, actionText, actionTextColor, listener);

    }

    @SnackbarDuration
    public static Snackbar showSnackBar(
            @NonNull Activity activity,
            @StringRes int messageStringRes,
            @SnackbarDuration int duration,
            Integer backgroundColor,
            Integer textColor,
            @StringRes Integer actionText,
            Integer actionTextColor,
            View.OnClickListener listener) {

        return showSnackBar(activity, activity.getResources().getString(messageStringRes), duration, backgroundColor, textColor, activity.getResources().getString(actionText), actionTextColor, listener);

    }

    @SnackbarDuration
    public static Snackbar showSnackBar(@NonNull Activity activity, @StringRes int stringRes, @SnackbarDuration int length) {
        return showSnackBar(activity, activity.getResources().getString(stringRes), length, null, null, null, null, null);
    }

    @SnackbarDuration
    public static Snackbar showSnackBar(@NonNull Activity activity, String textString, @SnackbarDuration int length) {
        return showSnackBar(activity, textString, length, null, null, null, null, null);
    }

    @SnackbarDuration
    public static Snackbar showSnackBar(@NonNull Activity activity, @StringRes int stringRes, @SnackbarDuration int length, @StringRes int actionText, View.OnClickListener listener) {
        return showSnackBar(activity, stringRes, length, null, null, actionText, null, listener);
    }

    public static Snackbar showSnackBar(Activity activity, String message, @SnackbarDuration int length, String buttonLabel, View.OnClickListener listener) {
        return showSnackBar(activity, message, length, null, null, buttonLabel, null, listener);
    }

    @IntDef({LENGTH_SHORT, LENGTH_LONG, LENGTH_INDEFINITE})
    @Retention(RetentionPolicy.SOURCE)
    private @interface SnackbarDuration {
    }
}
