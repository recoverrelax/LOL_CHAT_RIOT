package com.recoverrelax.pt.riotxmppchat.Riot.Model;

import android.support.annotation.NonNull;

import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.GameStatus;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.PresenceMode;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RankedLeagueTierDivision;

import org.jivesoftware.smack.packet.Presence;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.Comparator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Friend implements Comparable<Friend>{
    private String name;
    private String userXmppAddress;
    private Presence userRosterPresence;
    Element rootElement;

    /**
     * CONSTANTS
     */
    public static final String PROFILE_ICON = "profileIcon";
    public static final String LEVEL = "level";
    public static final String RANKED_WINS = "rankedWins";
    public static final String RANKED_LEAGUE_TIER = "rankedLeagueTier";
    public static final String RANKED_LEAGUE_DIVISION = "rankedLeagueDivision";
    public static final String STATUS_MSG = "statusMsg";
    public static final String CHAMPION_MASTERY_SCORE = "championMasteryScore";
    public static final String CHAMPION_NAME = "skinname";
    public static final String TIME_STAMP = "timeStamp";
    public static final String GAME_STATUS = "gameStatus";
    public static final String GAME_STATUS_NO_VIEW = "-1";
    public static final String PERSONAL_MESSAGE_NO_VIEW = "-1";

    public static final String NO_DATA = "-";

    public Friend(String name, String userXmppAddress, Presence userRosterPresence) {
        this.name = name;
        this.userXmppAddress = userXmppAddress;
        this.userRosterPresence = userRosterPresence;

        rootElement = buildCustomAttrFromStatusMessage();
    }

    /**
     * @return Element or NULL
     */
    private Element buildCustomAttrFromStatusMessage() {

        if (userRosterPresence.getStatus() == null)
            return null;
        else {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                return null;
            }
            Document document;
            try {
                document = builder.parse(new InputSource(new StringReader(userRosterPresence.getStatus())));
            } catch (SAXException | IOException e) {
                e.printStackTrace();
                return null;
            }
            return document.getDocumentElement();
        }
    }

    /**
     * @return The extracted String or EMPTY_STRING ("")
     */
    public static String getStringFromXmlTag(String tagName, Element rootElement) {
        if (rootElement == null)
            return NO_DATA;
        else {
            NodeList list = rootElement.getElementsByTagName(tagName);
            if (list != null && list.getLength() > 0) {
                NodeList subList = list.item(0).getChildNodes();

                if (subList != null && subList.getLength() > 0) {
                    return subList.item(0).getNodeValue();
                }
            }
            return NO_DATA;
        }
    }

    public String getProfileIconId() {
        return getStringFromXmlTag(PROFILE_ICON, rootElement);
    }

    public String getLevel() {
        return getStringFromXmlTag(LEVEL, rootElement);
    }

    public String getWins() {
        return getStringFromXmlTag(RANKED_WINS, rootElement);
    }

    /**
     * Mode only available for online users
     *
     * @return
     */

    public PresenceMode getFriendMode() {
        if (!this.userRosterPresence.isAvailable())
            return PresenceMode.UNAVAILABLE;
        else
            return PresenceMode.getPresenceModeFromSmack(this.userRosterPresence.getMode());
    }

    public boolean isOnline() {
        return this.getUserRosterPresence() != null && this.getUserRosterPresence().isAvailable();
    }

    public boolean isOffline() {
        return this.getUserRosterPresence() != null && !this.getUserRosterPresence().isAvailable();
    }

    public Presence getUserRosterPresence() {
        return userRosterPresence;
    }

    /**
     * @return A name assigned to the user (e.g. "Joe").
     */
    public String getName() {
        return name;
    }

    /**
     * @return XMPP address (e.g. jsmith@example.com).
     */
    public String getUserXmppAddress() {
        return userXmppAddress;
    }

    public String getRankedLeagueTier() {
        return getStringFromXmlTag(RANKED_LEAGUE_TIER, rootElement);
    }

    public String getRankedLeagueDivision() {
        return getStringFromXmlTag(RANKED_LEAGUE_DIVISION, rootElement);
    }

    public RankedLeagueTierDivision getLeagueDivisionAndTier() {
        return RankedLeagueTierDivision.getIconDrawableByLeagueAndTier(getRankedLeagueTier(), getRankedLeagueDivision());
    }

    public int getProfileIconResId() {
        return RankedLeagueTierDivision.getIconDrawableByLeagueAndTier(getRankedLeagueTier(), getRankedLeagueDivision()).getIconDrawable();
    }

    public String getStatusMsg() {
        String stringFromXmlTag = getStringFromXmlTag(STATUS_MSG, rootElement);
        if (stringFromXmlTag.equals(NO_DATA)) {
            return PERSONAL_MESSAGE_NO_VIEW;
        } else {
            return stringFromXmlTag;
        }
    }

    public String getChampionMasteryScore() {
        return getStringFromXmlTag(CHAMPION_MASTERY_SCORE, rootElement);
    }

    public GameStatus getGameStatus() {
        String gameStatusXmpp = getStringFromXmlTag(GAME_STATUS, rootElement);
        return GameStatus.getByXmppName(gameStatusXmpp);
    }

    public boolean isPlaying(){
        GameStatus gameStatus = getGameStatus();
        return gameStatus != null && gameStatus.isPlaying();
    }

    public boolean isInQueue(){
        GameStatus gameStatus = getGameStatus();
        return gameStatus != null && gameStatus.isInQueue();
    }

    /**
     * @return The Game Status or "-1" to tell the view shud be View.GONE
     */
    public String getGameStatusToPrint() {
        GameStatus gameStatus = getGameStatus();

        switch (gameStatus) {
            case OUT_OF_GAME:
                return GAME_STATUS_NO_VIEW;
            case CHAMPION_SELECT:
               return formatChampionSelectText();
            case INGAME:
                return formatInGameText();
            default:
                return gameStatus.getDescriptiveText();
        }
    }

    public String formatChampionSelectText(){
        String timeStamp = getTimeStampDifference();
        boolean noTimeStamp = timeStamp.equals(NO_DATA);
        String minutes = MainApplication.getInstance().getResources().getString(R.string.minutes);

        String message;

        if(noTimeStamp)
            message = MainApplication.getInstance().getResources().getString(R.string.in_champion_select);
        else
            message = MainApplication.getInstance().getResources().getString(R.string.in_champion_select_for) + " " + timeStamp + " " + minutes;
        return message;
    }

    public String getTimeStampDifference(){
        try {
            long serverTimeStamp = Long.parseLong(getTimeStamp());
            long nowTimeStamp = System.currentTimeMillis();

            long difference = Math.abs((nowTimeStamp - (serverTimeStamp + (2L * 1000L * 60L))) / 1000L / 60L);
            return String.valueOf(difference);
        }catch(NumberFormatException e){
            return "";
        }
    }

    public String formatInGameText(){
        String timeStamp = getTimeStampDifference();
        boolean noTimeStamp = timeStamp.equals(NO_DATA);

        String championName = getChampionName();
        boolean noChampionName = championName.equals(NO_DATA);

        String minutes = MainApplication.getInstance().getResources().getString(R.string.minutes);

        String playingAs = MainApplication.getInstance().getResources().getString(R.string.ingame_playing_as) + " ";
        String playingAsFor = MainApplication.getInstance().getResources().getString(R.string.ingame_playing_as_for) + " ";

       if(noTimeStamp){
            if(noChampionName){
                // !TIMESTAMP - !CHAMPIONNAME
                return GameStatus.INGAME.getDescriptiveText();
            }else{
                // !TIMESTAMP - CHAMPIONNAME
                return playingAs + championName;
            }
        }else{
            if(noChampionName){
                // TIMESTAMP - !CHAMPIONNAME
                return GameStatus.INGAME.getDescriptiveText() + " " + playingAsFor + " " + timeStamp + " " + minutes;
            }else{
                // TIMESTAMP - CHAMPIONNAME
                return playingAs + championName + " " + playingAsFor + timeStamp + " " + minutes;
            }
        }
    }

    public String getChampionName(){
        return getStringFromXmlTag(CHAMPION_NAME, rootElement);
    }

    public String getTimeStamp() {
        return getStringFromXmlTag(TIME_STAMP, rootElement);
    }

    @Override
    public int compareTo(@NonNull Friend friend) {
        if ((this.isOnline() && friend.isOnline()) || (!this.isOnline() && !friend.isOnline()))
                        return this.getName().compareTo(friend.getName());
                    else if (this.isOnline())
            return -1;
                    else
                        return 1;
    }

    public static class OnlineOfflineComparator implements Comparator<Friend> {
        @Override
        public int compare(Friend a, Friend b) {
            if (samePresence(a, b))
                return 0;
            else if (a.getUserRosterPresence().isAvailable() && !b.getUserRosterPresence().isAvailable())
                return -1;
            else
                return 1;
        }

        public boolean samePresence(Friend a, Friend b) {
            if (a.getUserRosterPresence().isAvailable() && b.getUserRosterPresence().isAvailable())
                return true;
            else if (!a.getUserRosterPresence().isAvailable() && !b.getUserRosterPresence().isAvailable()) {
                return true;
            }
            return false;
        }
    }

    public static class AlphabeticComparator implements Comparator<Friend> {
        @Override
        public int compare(Friend a, Friend b) {
            return a.getName().compareTo(b.getName());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Friend) {
            Friend f = (Friend) o;
            return this.getUserXmppAddress().equals(f.getUserXmppAddress());
        } else
            return false;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "name='" + name + '\'' +
                ", userXmppAddress='" + userXmppAddress + '\'' +
                ", userRosterPresence=" + userRosterPresence +
                ", rootElement=" + rootElement +
                '}';
    }
}
