package com.recoverrelax.pt.riotxmppchat.Riot.Model;

import org.jivesoftware.smack.packet.Presence;

public class Friend {
    private String name;
    private String userXmppAddress;
    private Presence userRosterPresence;

    public Friend(String name, String userXmppAddress) {
        this.name = name;
        this.userXmppAddress = userXmppAddress;
    }

    public Friend(String name, String userXmppAddress, Presence userRosterPresence) {
        this.name = name;
        this.userXmppAddress = userXmppAddress;
        this.userRosterPresence = userRosterPresence;
    }

    public Presence getUserRosterPresence(){
        return userRosterPresence;
    }

    /**
     *
     * @return A name assigned to the user (e.g. "Joe").
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return XMPP address (e.g. jsmith@example.com).
     */
    public String getUserXmppAddress() {
        return userXmppAddress;
    }
}
