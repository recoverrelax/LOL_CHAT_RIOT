package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.GameConstants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PlayerPosition {

    public static final int TOP = 1;
    public static final int MIDDLE = 2;
    public static final int JUNGLE = 3;
    public static final int BOT = 4;

    public static String getPositionName(int pp) {
        switch (pp) {
            case TOP:
                return "Top";
            case MIDDLE:
                return "Mid";
            case JUNGLE:
                return "Jungle";
            case BOT:
                return "Bot";
            default:
                return "";
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TOP, MIDDLE, JUNGLE, BOT})
    public @interface PlayerPositionI {
    }
}
