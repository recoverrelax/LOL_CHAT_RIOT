package com.recoverrelax.pt.riotxmppchat.Riot.Enum;

public enum GameStatus {
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
