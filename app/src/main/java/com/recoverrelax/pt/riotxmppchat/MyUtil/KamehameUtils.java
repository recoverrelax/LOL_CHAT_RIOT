package com.recoverrelax.pt.riotxmppchat.MyUtil;

import android.support.v7.widget.RecyclerView;

public class KamehameUtils {

    public static String transformGoldIntoKMode(int goldEarned){
        if(goldEarned > 999){
            return String.valueOf((goldEarned/1000)) + "K";
        }else{
            return String.valueOf(goldEarned);
        }
    }

    public static String transformKillDeathAssistIntoKda(String kill, String death, String assists) {
        return kill + "/" + death + "/" + assists;
    }

    public static void setRecyclerViewOnScrollListener(RecyclerView recyclerView){
//        recyclerView.setOnScrollListener();
        recyclerView.setOnScrollChangeListener(null);
    }
}
