package com.recoverrelax.pt.riotxmppchat.MyUtil;

public class AppGlobals {

    public static class Message{
        public static final int DEFAULT_MESSAGES_RETURNED = 20;
    }

    public static class XMPP {
        public static final String RIOT_DOMAIN = "pvp.net";
        public static final int RIOT_PORT = 5223;
    }

    public static class RIOT_API {
        public static final String API_KEY = "3b04be4c-ff07-445e-8aa6-a5c99ea2aa64";
    }

    public static class DD_VERSION {
        public static final String DD_VERSION = "dd_version";

        public static final String CHAMPION_JSON_ID = "champion";
        public static final String PROFILEICON_JSON_ID = "profileicon";
        public static final String ITEM_JSON_ID = "item";
        public static final String MAP_JSON_ID = "map";
        public static final String MASTERY_JSON_ID = "mastery";
        public static final String LANGUAGE_JSON_ID = "language";

        public static final String SUMMONER_SPELL_JSON_ID_DD = "spell";
        public static final String SUMMONER_SPELL_JSON_ID_JSON = "summoner";

        public static final String RUNE_JSON_ID = "rune";

        public static final String SUMMONER_SPELL_EXTENSION = ".png";

        public static final String SKIN_BASE_URL = "http://ddragon.leagueoflegends.com/cdn/img/champion/splash/";
        public static final String SKIN_FILE_EXTENSION = ".jpg";


        public static final String PROFILEICON_EXTENSION = ".png";
        public static final String CHAMPION_EXTENSION = ".png";

        public static final String SUMMONER_SPELL_SQUARE =
                "http://ddragon.leagueoflegends.com/cdn/" + DD_VERSION + "/img/" + SUMMONER_SPELL_JSON_ID_DD + "/";

        public static final String CHAMPION_SQUARE =
                "http://ddragon.leagueoflegends.com/cdn/" + DD_VERSION + "/img/" + CHAMPION_JSON_ID + "/";

        public static final String ITEM_SQUARE =
                "http://ddragon.leagueoflegends.com/cdn/" + DD_VERSION + "/img/" + ITEM_JSON_ID + "/";

        public static final String PROFILE_SQUARE =
                "http://ddragon.leagueoflegends.com/cdn/" + DD_VERSION + "/img/" + PROFILEICON_JSON_ID + "/";






    }
}
