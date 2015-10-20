package com.recoverrelax.pt.riotxmppchat.MyUtil;

public class KamehameUtils {

    public static String transformGoldIntoKMode(int goldEarned) {
        if (goldEarned > 999) {
            return String.valueOf((goldEarned / 1000)) + "K";
        } else {
            return String.valueOf(goldEarned);
        }
    }

    public static String transformKillDeathAssistIntoKda(String kill, String death, String assists) {
        return kill + "/" + death + "/" + assists;
    }
}
