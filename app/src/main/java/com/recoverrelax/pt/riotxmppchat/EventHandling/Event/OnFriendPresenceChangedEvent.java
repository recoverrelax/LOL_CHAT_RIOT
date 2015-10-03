package com.recoverrelax.pt.riotxmppchat.EventHandling.Event;

import org.jivesoftware.smack.packet.Presence;

public interface OnFriendPresenceChangedEvent {

    void onFriendPresenceChanged(Presence presence);
}
