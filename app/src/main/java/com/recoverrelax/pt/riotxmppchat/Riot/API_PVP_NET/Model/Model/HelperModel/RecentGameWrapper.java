package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel;

import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.GameConstants.PlayerPosition;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.TeamCode;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import pt.reco.myutil.MyDate;

public class RecentGameWrapper {

    private String myChampionUrl;
    private int myTeamId;

    private String summonerSpellUrl1;
    private String summonerSpellUrl2;

    private String kill;
    private String dead;
    private String assists;

    private List<String> skinList;

    private boolean isWin;

    private int gold;
    private String cs;

    private List<String> itemList;

    private HashMap<Integer, List<TeamInfo>> teamUrlMap = new HashMap<>();

    private String gameType;
    private String playerPosition;
    private String gameWhen;

    public RecentGameWrapper() {
    }

    public String getMyChampionUrl() {
        return myChampionUrl;
    }

    public void setMyChampionUrl(String myChampionUrl) {
        this.myChampionUrl = myChampionUrl;
    }

    public String getSummonerSpellUrl1() {
        return summonerSpellUrl1;
    }

    public void setSummonerSpellUrl1(String summonerSpellUrl1) {
        this.summonerSpellUrl1 = summonerSpellUrl1;
    }

    public String getSummonerSpellUrl2() {
        return summonerSpellUrl2;
    }

    public void setSummonerSpellUrl2(String summonerSpellUrl2) {
        this.summonerSpellUrl2 = summonerSpellUrl2;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public String getCs() {
        return cs;
    }

    public void setCs(String cs) {
        this.cs = cs;
    }

    public List<String> getItemList() {
        return itemList;
    }

    public void setItemList(List<String> itemList) {
        this.itemList = itemList;
    }

    public void addPlayer(long playerId, String playerImage, int teamId) {
        if (teamUrlMap.containsKey(teamId)) {
            teamUrlMap.get(teamId).add(new TeamInfo(playerId, playerImage));
        } else {
            List<TeamInfo> teamInfo = new ArrayList<>();
            teamInfo.add(new TeamInfo(playerId, playerImage));

            teamUrlMap.put(teamId, teamInfo);
        }
    }

    public List<TeamInfo> getTeam100() {
        if (teamUrlMap.containsKey(TeamCode.TEAM1.id))
            return teamUrlMap.get(TeamCode.TEAM1.id);
        else
            return null;
    }

    public List<TeamInfo> getTeam200() {
        if (teamUrlMap.containsKey(TeamCode.TEAM2.id))
            return teamUrlMap.get(TeamCode.TEAM2.id);
        else
            return null;
    }

    public HashMap<Integer, List<TeamInfo>> getTeamUrlMap() {
        return teamUrlMap;
    }

    public int getMyTeamId() {
        return myTeamId;
    }

    public void setMyTeamId(int myTeamId) {
        this.myTeamId = myTeamId;
    }

    public String getKill() {
        return kill;
    }

    public void setKill(String kill) {
        this.kill = kill;
    }

    public String getDead() {
        return dead;
    }

    public void setDead(String dead) {
        this.dead = dead;
    }

    public String getAssists() {
        return assists;
    }

    public void setAssists(String assists) {
        this.assists = assists;
    }

    public List<String> getSkinList() {
        return skinList;
    }

    public void setSkinList(List<String> skinList) {
        this.skinList = new ArrayList<>();
        this.skinList.addAll(skinList);
    }

    public String getRamdomSkin(Random r) {
        return this.skinList.get(r.nextInt(this.skinList.size()));
    }

    public boolean isWin() {
        return isWin;
    }

    public void setIsWin(boolean isWin) {
        this.isWin = isWin;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getGameWhen() {
        return gameWhen;
    }


    public void setGameWhen(long createdDate) {
        Date date = new Date(createdDate);
        this.gameWhen = MyDate.getFormatedDate(date);
    }

    public String getPlayerPosition() {
        return playerPosition;
    }

    public void setPlayerPosition(int playerPosition) {
        this.playerPosition = PlayerPosition.getPositionName(playerPosition).toUpperCase();
    }
}
