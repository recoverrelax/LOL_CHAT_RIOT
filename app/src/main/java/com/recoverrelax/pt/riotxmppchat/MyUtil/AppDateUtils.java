package com.recoverrelax.pt.riotxmppchat.MyUtil;

import android.text.format.DateUtils;

import java.util.Date;

public class AppDateUtils {

    private static final int DATE_MIN_TIME_FORMAT = 360;

    public static String getFormatedDate(Date date){
        return DateUtils.getRelativeTimeSpanString(date.getTime(), new Date().getTime(), DATE_MIN_TIME_FORMAT).toString();
    }
}
