package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import org.jivesoftware.smack.packet.Presence;

public interface RiotXmppRosterHelper {
    void getFullFriendsList(boolean getOfflineUsers);
    void searchFriendsList(String searchString);
    void getPresenceChanged(Presence presence, boolean getOfflineUsers);
}
