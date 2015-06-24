package com.recoverrelax.pt.riotxmppchat.Riot.Interface;

import org.jivesoftware.smack.packet.Presence;

public interface RiotXmppRosterHelper {
    void getFullFriendsList(boolean getOfflineUsers);
    void searchFriendsList(String searchString);
    void getPresenceChanged(Presence presence, boolean getOfflineUsers);
}
