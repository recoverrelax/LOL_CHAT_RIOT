package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Game;


import org.parceler.Parcel;

/**
 * This object contains raw stat information.
 */
@Parcel
public class RawStatsDto {

    int assists;

    /**
     * Number of enemy inhibitors killed.
     */
    int barracksKilled;

    int championsKilled;
    int combatPlayerScore;
    int consumablesPurchased;
    int damageDealtPlayer;
    int doubleKills;
    int firstBlood;
    int gold;
    int goldEarned;
    int goldSpent;

    int item0;
    int item01;
    int item02;
    int item03;
    int item04;
    int item05;
    int item06;

    int itemsPurchased;
    int killingSprees;
    int largestCriticalStrike;
    int largestKillingSpree;

    /**
     * Number of tier 3 items built.
     */
    int legendaryItemsCreated;

    int level;
    int magicDamageDealtPlayer;
    int magicDamageDealtToChampions;
    int magicDamageTaken;
    int minionsDenied;
    int minionsKilled;
    int neutralMinionsKilled;
    int neutralMinionsKilledEnemyJungle;
    int neutralMinionsKilledYourJungle;

    /**
     * Flag specifying if the summoner got the killing blow on the nexus.
     */
    boolean nexusKilled;

    int nodeCapture;
    int nodeCaptureAssist;
    int nodeNeutralize;
    int nodeNeutralizeAssist;
    int numDeaths;
    int numItemsBought;
    int objectivePlayerScore;
    int pentaKills;
    int physicalDamageDealtPlayer;
    int physicalDamageDealtToChampions;
    int physicalDamageTaken;

    /**
     * Player position (Legal values: TOP(1), MIDDLE(2), JUNGLE(3), BOT(4))
     */
    int playerPosition;

    /**
     * Player role (Legal values: DUO(1), SUPPORT(2), CARRY(3), SOLO(4))
     */
    int playerRole;

    int quadraKills;
    int sightWardsBought;

    /**
     * Number of times first champion spell was cast.
     */
    int spell1Cast;

    /**
     * Number of times second champion spell was cast.
     */
    int spell2Cast;

    /**
     * Number of times third champion spell was cast.
     */
    int spell3Cast;

    /**
     * Number of times fourth champion spell was cast.
     */
    int spell4Cast;

    int summonSpell1Cast;
    int summonSpell2Cast;
    int superMonsterKilled;
    int team;
    int teamObjective;
    int timePlayed;
    int totalDamageDealt;
    int totalDamageDealtToChampions;
    int totalDamageTaken;
    int totalHeal;
    int totalPlayerScore;
    int totalScoreRank;
    int totalTimeCrowdControlDealt;
    int totalUnitsHealed;
    int tripleKills;
    int trueDamageDealtPlayer;
    int trueDamageDealtToChampions;
    int trueDamageTaken;
    int turretsKilled;
    int unrealKills;
    int victoryPointTotal;
    int visionWardsBought;
    int wardKilled;
    int wardPlaced;

    boolean win; // Flag specifying whether or not this game was won.

    public int getAssists() {
        return assists;
    }

    public int getBarracksKilled() {
        return barracksKilled;
    }

    public int getChampionsKilled() {
        return championsKilled;
    }

    public int getCombatPlayerScore() {
        return combatPlayerScore;
    }

    public int getConsumablesPurchased() {
        return consumablesPurchased;
    }

    public int getDamageDealtPlayer() {
        return damageDealtPlayer;
    }

    public int getDoubleKills() {
        return doubleKills;
    }

    public int getFirstBlood() {
        return firstBlood;
    }

    public int getGold() {
        return gold;
    }

    public int getGoldEarned() {
        return goldEarned;
    }

    public int getGoldSpent() {
        return goldSpent;
    }

    public int getItem0() {
        return item0;
    }

    public int getItem01() {
        return item01;
    }

    public int getItem02() {
        return item02;
    }

    public int getItem03() {
        return item03;
    }

    public int getItem04() {
        return item04;
    }

    public int getItem05() {
        return item05;
    }

    public int getItem06() {
        return item06;
    }

    public int getItemsPurchased() {
        return itemsPurchased;
    }

    public int getKillingSprees() {
        return killingSprees;
    }

    public int getLargestCriticalStrike() {
        return largestCriticalStrike;
    }

    public int getLargestKillingSpree() {
        return largestKillingSpree;
    }

    public int getLegendaryItemsCreated() {
        return legendaryItemsCreated;
    }

    public int getLevel() {
        return level;
    }

    public int getMagicDamageDealtPlayer() {
        return magicDamageDealtPlayer;
    }

    public int getMagicDamageDealtToChampions() {
        return magicDamageDealtToChampions;
    }

    public int getMagicDamageTaken() {
        return magicDamageTaken;
    }

    public int getMinionsDenied() {
        return minionsDenied;
    }

    public int getMinionsKilled() {
        return minionsKilled;
    }

    public int getNeutralMinionsKilled() {
        return neutralMinionsKilled;
    }

    public int getNeutralMinionsKilledEnemyJungle() {
        return neutralMinionsKilledEnemyJungle;
    }

    public int getNeutralMinionsKilledYourJungle() {
        return neutralMinionsKilledYourJungle;
    }

    public boolean isNexusKilled() {
        return nexusKilled;
    }

    public int getNodeCapture() {
        return nodeCapture;
    }

    public int getNodeCaptureAssist() {
        return nodeCaptureAssist;
    }

    public int getNodeNeutralize() {
        return nodeNeutralize;
    }

    public int getNodeNeutralizeAssist() {
        return nodeNeutralizeAssist;
    }

    public int getNumDeaths() {
        return numDeaths;
    }

    public int getNumItemsBought() {
        return numItemsBought;
    }

    public int getObjectivePlayerScore() {
        return objectivePlayerScore;
    }

    public int getPentaKills() {
        return pentaKills;
    }

    public int getPhysicalDamageDealtPlayer() {
        return physicalDamageDealtPlayer;
    }

    public int getPhysicalDamageDealtToChampions() {
        return physicalDamageDealtToChampions;
    }

    public int getPhysicalDamageTaken() {
        return physicalDamageTaken;
    }

    public int getPlayerPosition() {
        return playerPosition;
    }

    public int getPlayerRole() {
        return playerRole;
    }

    public int getQuadraKills() {
        return quadraKills;
    }

    public int getSightWardsBought() {
        return sightWardsBought;
    }

    public int getSpell1Cast() {
        return spell1Cast;
    }

    public int getSpell2Cast() {
        return spell2Cast;
    }

    public int getSpell3Cast() {
        return spell3Cast;
    }

    public int getSpell4Cast() {
        return spell4Cast;
    }

    public int getSummonSpell1Cast() {
        return summonSpell1Cast;
    }

    public int getSummonSpell2Cast() {
        return summonSpell2Cast;
    }

    public int getSuperMonsterKilled() {
        return superMonsterKilled;
    }

    public int getTeam() {
        return team;
    }

    public int getTeamObjective() {
        return teamObjective;
    }

    public int getTimePlayed() {
        return timePlayed;
    }

    public int getTotalDamageDealt() {
        return totalDamageDealt;
    }

    public int getTotalDamageDealtToChampions() {
        return totalDamageDealtToChampions;
    }

    public int getTotalDamageTaken() {
        return totalDamageTaken;
    }

    public int getTotalHeal() {
        return totalHeal;
    }

    public int getTotalPlayerScore() {
        return totalPlayerScore;
    }

    public int getTotalScoreRank() {
        return totalScoreRank;
    }

    public int getTotalTimeCrowdControlDealt() {
        return totalTimeCrowdControlDealt;
    }

    public int getTotalUnitsHealed() {
        return totalUnitsHealed;
    }

    public int getTripleKills() {
        return tripleKills;
    }

    public int getTrueDamageDealtPlayer() {
        return trueDamageDealtPlayer;
    }

    public int getTrueDamageDealtToChampions() {
        return trueDamageDealtToChampions;
    }

    public int getTrueDamageTaken() {
        return trueDamageTaken;
    }

    public int getTurretsKilled() {
        return turretsKilled;
    }

    public int getUnrealKills() {
        return unrealKills;
    }

    public int getVictoryPointTotal() {
        return victoryPointTotal;
    }

    public int getVisionWardsBought() {
        return visionWardsBought;
    }

    public int getWardKilled() {
        return wardKilled;
    }

    public int getWardPlaced() {
        return wardPlaced;
    }

    public boolean isWin() {
        return win;
    }
}
