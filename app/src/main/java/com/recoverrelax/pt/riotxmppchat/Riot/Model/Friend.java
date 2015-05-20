package com.recoverrelax.pt.riotxmppchat.Riot.Model;

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

public class Friend {
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

        if(userRosterPresence.getStatus() == null)
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
     *
     * @return The extracted String or EMPTY_STRING ("")
     */
    private String getStringFromXmlTag(String tagName, Element rootElement) {
        if(rootElement == null)
            return "";
        else {
            NodeList list = rootElement.getElementsByTagName(tagName);
            if (list != null && list.getLength() > 0) {
                NodeList subList = list.item(0).getChildNodes();

                if (subList != null && subList.getLength() > 0) {
                    return subList.item(0).getNodeValue();
                }
            }

            return null;
        }
    }

    public String getProfileIconId(){
        return getStringFromXmlTag(PROFILE_ICON, rootElement);
    }

    public String getLevel(){
        return getStringFromXmlTag(LEVEL, rootElement);
    }

    public String getWins(){
        return getStringFromXmlTag(RANKED_WINS, rootElement);
    }

    /**
     * Mode only available for online users
     * @return
     */
    public Presence.Mode getFriendMode(){
        return this.userRosterPresence.getMode();
    }

    public Presence getUserRosterPresence(){
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

    public String getRankedLeagueTier(){
        return getStringFromXmlTag(RANKED_LEAGUE_TIER, rootElement);
    }

    public String getRankedLeagueDivision(){
        return getStringFromXmlTag(RANKED_LEAGUE_DIVISION, rootElement);
    }

    public RankedLeagueTierDivision getLeagueDivisionAndTier(){
        return RankedLeagueTierDivision.getIconDrawableByLeagueAndTier(getRankedLeagueTier(), getRankedLeagueDivision());
    }

    public int getProfileIconResId() {
        return RankedLeagueTierDivision.getIconDrawableByLeagueAndTier(getRankedLeagueTier(), getRankedLeagueDivision()).getIconDrawable();
    }

    public static class OnlineOfflineComparator implements Comparator<Friend> {
        @Override
        public int compare(Friend a, Friend b) {
            if(samePresence(a, b))
                return 0;
            else if(a.getUserRosterPresence().isAvailable() && !b.getUserRosterPresence().isAvailable())
                return -1;
            else
                return 1;
        }

        public boolean samePresence(Friend a, Friend b){
            if(a.getUserRosterPresence().isAvailable() && b.getUserRosterPresence().isAvailable())
                return true;
            else if(!a.getUserRosterPresence().isAvailable() && !b.getUserRosterPresence().isAvailable()){
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
}
