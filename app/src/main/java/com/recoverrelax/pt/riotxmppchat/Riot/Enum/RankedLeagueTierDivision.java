package com.recoverrelax.pt.riotxmppchat.Riot.Enum;

import android.support.annotation.DrawableRes;

import com.recoverrelax.pt.riotxmppchat.R;

public enum RankedLeagueTierDivision {

    BRONZE_1("Bronze I", R.drawable.bronze_1),
    BRONZE_2("Bronze II", R.drawable.bronze_2),
    BRONZE_3("Bronze II", R.drawable.bronze_3),
    BRONZE_4("Bronze IV", R.drawable.bronze_4),
    BRONZE_5("Bronze V", R.drawable.bronze_5),
    SILVER_1("Silver I", R.drawable.silver_1),
    SILVER_2("Silver II", R.drawable.silver_2),
    SILVER_3("Silver III", R.drawable.silver_3),
    SILVER_4("Silver IV", R.drawable.silver_4),
    SILVER_5("Silver V", R.drawable.silver_5),
    GOLD_1("Gold I", R.drawable.gold_1),
    GOLD_2("Gold II", R.drawable.gold_2),
    GOLD_3("Gold III", R.drawable.gold_3),
    GOLD_4("Gold IV", R.drawable.gold_4),
    GOLD_5("Gold V", R.drawable.gold_5),
    PLATINUM_1("Platinum I", R.drawable.platinum_1),
    PLATINUM_2("Platinum II", R.drawable.platinum_2),
    PLATINUM_3("Platinum II", R.drawable.platinum_3),
    PLATINUM_4("Platinum IV", R.drawable.platinum_4),
    PLATINUM_5("Platinum V", R.drawable.platinum_5),
    DIAMOND_1("Diamond I", R.drawable.diamond_1),
    DIAMOND_2("Diamond II", R.drawable.diamond_2),
    DIAMOND_3("Diamond III", R.drawable.diamond_3),
    DIAMOND_4("Diamond IV", R.drawable.diamond_4),
    DIAMOND_5("Diamond V", R.drawable.diamond_5),
    MASTER("Master", R.drawable.master_1),
    CHALLENGER("Challenger", R.drawable.challenger_1),
    NON_DEFINED("", R.drawable.unknown);

    private String descriptiveName;
    private
    @DrawableRes int iconDrawable;

    RankedLeagueTierDivision(String descriptiveName, @DrawableRes int iconDrawable) {
        this.descriptiveName = descriptiveName;
        this.iconDrawable = iconDrawable;
    }

    public static RankedLeagueTierDivision getIconDrawableByLeagueAndTier(String rankedLeagueTier, String rankedLeagueDivision) {
        String compoundName;
        /**
         * Master and Challenger don't have Divisions!
         */
        if (rankedLeagueTier.toLowerCase().equals(RankedLeagueTierDivision.MASTER.getDescriptiveName().toLowerCase()) ||
                rankedLeagueTier.toLowerCase().equals(RankedLeagueTierDivision.CHALLENGER.getDescriptiveName().toLowerCase())) {
            compoundName = rankedLeagueTier.toLowerCase();
        } else {
            compoundName = rankedLeagueTier.toLowerCase() + " " + rankedLeagueDivision.toLowerCase();
        }

        for (RankedLeagueTierDivision rankedLdt : RankedLeagueTierDivision.values()) {
            if (rankedLdt.getDescriptiveName().toLowerCase().equals(compoundName)) {
                return rankedLdt;
            }
        }
        return RankedLeagueTierDivision.NON_DEFINED;
    }

    public String getDescriptiveName() {
        return descriptiveName;
    }

    public @DrawableRes int getIconDrawable() {
        return iconDrawable;
    }
}
