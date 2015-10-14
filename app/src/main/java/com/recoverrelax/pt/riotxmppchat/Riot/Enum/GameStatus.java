package com.recoverrelax.pt.riotxmppchat.Riot.Enum;

import android.support.annotation.StringDef;

public enum GameStatus {

//    @StringDef({OUT_OF_GAME, IN_QUEUE, SPECTATING,CHAMPION_SELECT, IN_TEAM_BUILDER, INGAME, HOSTING_PRACTICE_GAME})
//    public @interface GameStatusI {}
//
//    public static final String OUT_OF_GAME = "outOfGame";
//    public static final String IN_QUEUE = "inQueue";
//    public static final String SPECTATING = "spectating";
//    public static final String CHAMPION_SELECT = "championSelect";
//    public static final String IN_TEAM_BUILDER = "inTeamBuilder";
//    public static final String INGAME = "inGame";
//    public static final String HOSTING_PRACTICE_GAME = "hostingPracticeGame";
//
//    public static String getDescriptiveText(@GameStatusI String gameStatus){
//        switch (gameStatus) {
//            case OUT_OF_GAME:
//                return "Out of Game";
//            case IN_QUEUE:
//                return "In Queue";
//            case SPECTATING:
//                return "Spectating";
//            case CHAMPION_SELECT:
//                return "In Champion Select";
//            case IN_TEAM_BUILDER:
//                return "In Team Builder";
//            case INGAME:
//                return "In-Game";
//            case HOSTING_PRACTICE_GAME:
//                return "Hosting Practice Game";
//            default:
//                return "";
//        }
//    }


    OUT_OF_GAME("Out of Game", "outOfGame"),
    IN_QUEUE("In Queue", "inQueue"),
    SPECTATING("Spectating", "spectating"),
    CHAMPION_SELECT("In Champion Select",  "championSelect"),
    IN_TEAM_BUILDER("In Team Builder", "inTeamBuilder"),
    INGAME("In-Game", "inGame"),
    HOSTING_PRACTICE_GAME("Hosting Practice Game", "hostingPracticeGame");

    private String descriptiveText;
    private String xmppName;
    GameStatus(String descriptiveText, String xmppName){
        this.descriptiveText = descriptiveText;
        this.xmppName = xmppName;
    }

    public String getDescriptiveText() {
        return descriptiveText;
    }

    public String getXmppName() {
        return xmppName;
    }

    public static GameStatus getByXmppName(String xmppName){
        for(GameStatus game: GameStatus.values()){
            if(game.getXmppName().toLowerCase().equals(xmppName.toLowerCase())){
                return game;
            }
        }
        return GameStatus.OUT_OF_GAME;
    }

    public boolean isPlaying(){
        return this.equals(GameStatus.INGAME);
    }

    public boolean isInQueue(){
        return this.equals(GameStatus.IN_QUEUE);
    }

    public boolean isOutOfGame(){
        return this.equals(GameStatus.OUT_OF_GAME);
    }
}
