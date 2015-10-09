package pt.reco.myutil;

public class MyDate {

    private static final int DATE_MIN_TIME_FORMAT = 360;

    public static String getFormatedDate(java.util.Date date){
        return android.text.format.DateUtils.getRelativeTimeSpanString(date.getTime(), new java.util.Date().getTime(), DATE_MIN_TIME_FORMAT).toString();
    }

}
