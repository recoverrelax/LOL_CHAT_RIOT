package com.recoverrelax.pt.riotxmppchat.MyUtil;

import android.os.Handler;
import android.text.format.DateUtils;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;

import java.util.Date;

public class AppDateUtils {

    private static final int DATE_MIN_TIME_FORMAT = 360;
    private static final int UPDATE_DATE_EACH_MILIS = 30000;

    private static final int UPDATE_TEXTVIEW_REPEAT_TIME = 1000;

    public static String getFormatedDate(Date date){
        return DateUtils.getRelativeTimeSpanString(date.getTime(), new Date().getTime(), DATE_MIN_TIME_FORMAT).toString();
    }

    /**
     * First, it formats the Date like: 1 minute ago, 2 hours ago, Today, Yesterday, etc..
     * Then it creates an handler that will update the textview each {@link AppDateUtils#UPDATE_DATE_EACH_MILIS} seconds
     */
    public static void setTimeElapsedWithHandler(final TextView holder, final Date date) {
        Runnable runnable123 = new Runnable() {
            @Override
            public void run() {
                String formatedDate = getFormatedDate(date);
                holder.setText(formatedDate);
                new Handler().postDelayed(this, UPDATE_DATE_EACH_MILIS);
            }
        };
        runnable123.run();
    }

    public static void updateGameStatusPeriodically(final TextView textview, final Friend text2Update) {
        new Runnable() {
            @Override
            public void run() {
                textview.setText(text2Update.getGameStatusToPrint());
                new Handler().postDelayed(this, UPDATE_TEXTVIEW_REPEAT_TIME);
            }
        }.run();
    }
}