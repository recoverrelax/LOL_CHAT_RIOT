package com.recoverrelax.pt.riotxmppchat.Riot.Interface;

import org.jivesoftware.smack.packet.Presence;

public interface RiotXmppRosterHelper {
    void getFullFriendsList();
    void getPresenceChanged(Presence presence);
}
