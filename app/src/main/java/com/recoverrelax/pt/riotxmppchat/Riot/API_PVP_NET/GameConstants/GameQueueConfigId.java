package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.GameConstants;

public enum GameQueueConfigId {

    CUSTOM(0, "Custom games"),
    NORMAL_3x3(8, "Normal 3v3 games"),
    NORMAL_5x5_BLIND(2, "Normal 5v5 Blind Pick games"),
    NORMAL_5x5_DRAFT(14, "Normal 5v5 Draft Pick games"),
    RANKED_SOLO_5x5(4, "Ranked Solo 5v5 games"),
    RANKED_PREMADE_5x5(6, "Ranked Premade 5v5 games"),
    RANKED_PREMADE_3x3(9, "Ranked Premade 3v3 games"),
    RANKED_TEAM_3x3(41, "Ranked Team 3v3 games"),
    RANKED_TEAM_5x5(42, "Ranked Team 5v5 games"),
    ODIN_5x5_BLIND(16, "Dominion 5v5 Blind Pick games"),
    ODIN_5x5_DRAFT(17, "Dominion 5v5 Draft Pick games"),
    BOT_5x5(7, "Historical Summoner's Rift Coop vs AI games"),
    BOT_ODIN_5x5(25, "Dominion Coop vs AI games"),
    BOT_5x5_INTRO(31, "Summoner's Rift Coop vs AI Intro Bot games"),
    BOT_5x5_BEGINNER(32, "Summoner's Rift Coop vs AI Beginner Bot games"),
    BOT_5x5_INTERMEDIATE(33, "Historical Summoner's Rift Coop vs AI Intermediate Bot games"),
    BOT_TT_3x3(52, "Twisted Treeline Coop vs AI games"),
    GROUP_FINDER_5x5(61, "Team Builder games"),
    ARAM_5x5(65, "ARAM games"),
    ONEFORALL_5x5(70, "One for All games"),
    FIRSTBLOOD_1x1(72, "Snowdown Showdown 1v1 games"),
    FIRSTBLOOD_2x2(73, "Snowdown Showdown 2v2 games"),
    SR_6x6(75, "Summoner's Rift 6x6 Hexakill games"),
    URF_5x5(76, "Ultra Rapid Fire games"),
    BOT_URF_5x5(83, "Ultra Rapid Fire games played against AI games"),
    NIGHTMARE_BOT_5x5_RANK1(91, "Doom Bots Rank 1 games"),
    NIGHTMARE_BOT_5x5_RANK2(92, "Doom Bots Rank 2games"),
    NIGHTMARE_BOT_5x5_RANK5(93, "Doom Bots Rank 5games"),
    ASCENSION_5x5(96, "Ascension games"),
    HEXAKILL(98, "Twisted Treeline 6x6 Hexakill games"),
    BILGEWATER_ARAM_5x5(100, "Butcher's Bridge games"),
    KING_PORO_5x5(300, "King Poro games"),
    COUNTER_PICK(310, "Nemesis games"),
    BILGEWATER_5x5(313, "Black Market Brawlers game");

    private long queueType;
    private String name;

    GameQueueConfigId(long queueType, String name) {
        this.queueType = queueType;
        this.name = name;
    }

    public static GameQueueConfigId getByQueueType(long queueType){
        for(GameQueueConfigId q: GameQueueConfigId.values()){
            if(q.getQueueType() == queueType)
                return q;
        }
        return null;
    }

    public long getQueueType() {
        return queueType;
    }

    public String getName() {
        return name;
    }
}
