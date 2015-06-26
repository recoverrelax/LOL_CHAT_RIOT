package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import org.jivesoftware.smack.packet.Presence;

public interface RiotXmppDashboardHelper {
    void getUnreadedMessagesCount();
    void getFriendStatusInfo();
}
